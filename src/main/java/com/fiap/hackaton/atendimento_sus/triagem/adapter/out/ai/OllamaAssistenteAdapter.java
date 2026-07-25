package com.fiap.hackaton.atendimento_sus.triagem.adapter.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.ContextoTriagem;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.NivelRisco;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter que assiste a triagem via Ollama (LLM local). Best-effort: qualquer
 * falha resulta em retorno vazio/nulo — a triagem nunca é interrompida por
 * causa da IA. Ativado por {@code app.ai.ollama.enabled=true}.
 */
@Component
@ConditionalOnProperty(name = "app.ai.ollama.enabled", havingValue = "true")
public class OllamaAssistenteAdapter implements AssistenteTriagemPort {

    private static final Logger log = LoggerFactory.getLogger(OllamaAssistenteAdapter.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OllamaProperties props;

    @org.springframework.beans.factory.annotation.Autowired
    public OllamaAssistenteAdapter(OllamaProperties props) {
        this(props, defaultRestClient(props));
    }

    /** Construtor de teste: permite injetar um RestClient (ex.: com MockRestServiceServer). */
    OllamaAssistenteAdapter(OllamaProperties props, RestClient restClient) {
        this.props = props;
        this.restClient = restClient;
    }

    private static RestClient defaultRestClient(OllamaProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(props.timeoutSeconds()));
        factory.setReadTimeout(Duration.ofSeconds(props.timeoutSeconds()));
        return RestClient.builder().baseUrl(props.baseUrl()).requestFactory(factory).build();
    }

    @Override
    public AnaliseClinica analisar(ContextoTriagem contexto) {
        if (contexto == null || contexto.queixaLivre() == null || contexto.queixaLivre().isBlank()) {
            return analiseVazia();
        }
        try {
            String valoresEnum = Arrays.stream(Sintoma.values()).map(Enum::name).collect(Collectors.joining(", "));
            String system = """
                    Você é um assistente de triagem clínica. A partir da queixa do paciente em
                    português, identifique os sintomas presentes e mapeie APENAS para os valores
                    exatos deste enum: %s. Gere obrigatoriamente um resumo factual e curto da queixa.
                    Além disso, sugira perguntas complementares, alertas para conferência e campos ausentes
                    para revisão humana. Não faça diagnóstico, prescrição, prognóstico nem classificação de
                    risco. Gere no máximo 5 itens por lista. Responda somente em JSON. Não invente sintomas.
                    """.formatted(valoresEnum);

            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "sintomas", Map.of("type", "array",
                                    "items", Map.of("type", "string",
                                            "enum", Arrays.stream(Sintoma.values()).map(Enum::name).toList())),
                            "resumo", Map.of("type", "string"),
                            "perguntasComplementares", arraySchema(),
                            "alertasParaConferencia", arraySchema(),
                            "camposAusentes", arraySchema()),
                    "required", List.of("sintomas", "resumo", "perguntasComplementares",
                            "alertasParaConferencia", "camposAusentes"),
                    "additionalProperties", false);

            String content = chat(system, contextoParaPrompt(contexto), schema);
            JsonNode raiz = objectMapper.readTree(content);

            Set<Sintoma> sintomas = EnumSet.noneOf(Sintoma.class);
            if (raiz.has("sintomas") && raiz.get("sintomas").isArray()) {
                for (JsonNode n : raiz.get("sintomas")) {
                    parseSintoma(n.asText()).ifPresent(sintomas::add);
                }
            }
            String resumo = textoLimitado(raiz.path("resumo").asText(null));
            if (resumo == null) {
                log.warn("Ollama retornou análise sem resumo; utilizando a queixa original como fallback");
                resumo = textoLimitado(contexto.queixaLivre());
            }
            return new AnaliseClinica(sintomas, resumo,
                    textosLimitados(raiz.path("perguntasComplementares")),
                    textosLimitados(raiz.path("alertasParaConferencia")),
                    textosLimitados(raiz.path("camposAusentes")));
        } catch (Exception ex) {
            log.warn("Falha ao analisar queixa via Ollama (degradando para vazio): {}", ex.getMessage());
            return analiseVazia();
        }
    }

    @Override
    public String gerarOrientacao(NivelRisco nivel, Set<Sintoma> sintomas) {
        if (nivel == null) {
            return null;
        }
        try {
            String system = """
                    Você é um assistente de saúde do SUS. Gere uma orientação curta (até 3 frases),
                    em português claro e acolhedor, para um paciente classificado no risco %s (%s,
                    tempo-alvo de %d min). Não faça diagnóstico; oriente sobre a conduta e urgência.
                    """.formatted(nivel.name(), nivel.descricao(), nivel.tempoAlvoMinutos());
            String user = "Sintomas relatados: " + (sintomas == null || sintomas.isEmpty()
                    ? "não informados" : sintomas.stream().map(Enum::name).collect(Collectors.joining(", ")));
            String content = chat(system, user, null);
            return content == null || content.isBlank() ? null : content.trim();
        } catch (Exception ex) {
            log.warn("Falha ao gerar orientação via Ollama (degradando para null): {}", ex.getMessage());
            return null;
        }
    }

    private static String contextoParaPrompt(ContextoTriagem contexto) {
        String sintomas = contexto.sintomasSelecionados() == null || contexto.sintomasSelecionados().isEmpty()
                ? "não informados" : contexto.sintomasSelecionados().stream().map(Enum::name).collect(Collectors.joining(", "));
        String sinais = contexto.sinaisVitais() == null ? "não informados" : "FC=%d bpm, FR=%d irpm, PA=%d/%d mmHg, temperatura=%.1f °C, saturação=%d%%, dor=%d/10"
                .formatted(contexto.sinaisVitais().frequenciaCardiaca(), contexto.sinaisVitais().frequenciaRespiratoria(),
                        contexto.sinaisVitais().pressaoSistolica(), contexto.sinaisVitais().pressaoDiastolica(), contexto.sinaisVitais().temperatura(),
                        contexto.sinaisVitais().saturacaoOxigenio(), contexto.sinaisVitais().escalaDor());
        return "Queixa: %s\nSintomas selecionados: %s\nSinais vitais: %s".formatted(contexto.queixaLivre(), sintomas, sinais);
    }

    private static Map<String, Object> arraySchema() {
        return Map.of("type", "array", "items", Map.of("type", "string"));
    }

    private static AnaliseClinica analiseVazia() {
        return new AnaliseClinica(EnumSet.noneOf(Sintoma.class), null, Set.of(), Set.of(), Set.of());
    }

    private static Set<String> textosLimitados(JsonNode valores) {
        Set<String> resultado = new LinkedHashSet<>();
        if (!valores.isArray()) return resultado;
        for (JsonNode valor : valores) {
            String texto = textoLimitado(valor.asText(null));
            if (texto != null) resultado.add(texto);
            if (resultado.size() == 5) break;
        }
        return resultado;
    }

    private static String textoLimitado(String texto) {
        if (texto == null || texto.isBlank()) return null;
        String normalizado = texto.trim();
        return normalizado.length() <= 300 ? normalizado : normalizado.substring(0, 300);
    }

    /** Chama /api/chat (stream=false) e retorna o texto de message.content. */
    private String chat(String system, String user, Map<String, Object> formatSchema) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.model());
        body.put("stream", false);
        body.put("messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", user)));
        if (formatSchema != null) {
            body.put("format", formatSchema);
        }
        String resposta = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        JsonNode raiz = objectMapper.readTree(resposta);
        JsonNode msg = raiz.path("message").path("content");
        return msg.isMissingNode() ? null : msg.asText();
    }

    private java.util.Optional<Sintoma> parseSintoma(String valor) {
        try {
            return java.util.Optional.of(Sintoma.valueOf(valor.trim().toUpperCase()));
        } catch (IllegalArgumentException ignored) {
            return java.util.Optional.empty();
        }
    }
}
