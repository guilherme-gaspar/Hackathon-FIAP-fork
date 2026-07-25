# SUS Flow — Triagem inteligente e agendamento prioritário

MVP back-end para apoiar o acolhimento no SUS. O sistema permite cadastrar pacientes, analisar queixas com IA local, realizar uma classificação de risco determinística e agendar atendimentos conforme a prioridade resultante.

> **Importante:** a IA é assistiva. Ela sugere sintomas, perguntas e alertas para revisão profissional; não diagnostica, não prescreve e não decide o nível de risco.

## Funcionalidades

- Autenticação JWT com os perfis `PACIENTE`, `PROFISSIONAL` e `ADMIN`.
- Cadastro e consulta de pacientes.
- Análise de queixa livre via Ollama, com sintomas sugeridos, perguntas complementares, alertas e campos ausentes.
- Classificação de risco determinística baseada em sinais vitais e sintomas.
- Orientação ao paciente gerada em modo best-effort após a classificação.
- Agendamento de consultas/exames com prioridade derivada da triagem.
- Documentação OpenAPI/Swagger, health check, migrations Flyway e collection Postman.

## Arquitetura e relatório

- [Relatório do projeto](docs/RELATORIO-PROJETO.md)
- [Diagrama de arquitetura](docs/diagramas/arquitetura.svg)
- [Diagrama de fluxo de triagem](docs/diagramas/fluxo-triagem.svg)
- [Collection Postman](postman/Atendimento-SUS.postman_collection.json)
- [Slides e roteiro completo do pitch](apresentacao/ROTEIRO-PITCH.md)
- [Roteiro resumido e natural do pitch](apresentacao/ROTEIRO-PITCH-RESUMIDO.md)

## Pré-requisitos

- Docker Engine e Docker Compose Plugin.
- Para execução sem Docker: Java 21, PostgreSQL 16 e uma instância Ollama com o modelo configurado.

## Execução rápida com Docker

Na raiz do repositório, execute:

```bash
docker compose up --build
```

O Compose sobe automaticamente:

| Serviço | Endereço | Finalidade |
|---|---|---|
| API | `http://localhost:8080` | Aplicação Spring Boot |
| PostgreSQL | `localhost:5432` | Banco de dados |
| Ollama | `http://localhost:11434` | IA local |

Na primeira execução, o serviço `ollama-pull` baixa o modelo `llama3.2:3b`; isso pode levar alguns minutos.

### Verificar a saúde da API

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:

```json
{"status":"UP"}
```

## Documentação e demonstração

| Recurso | URL/arquivo |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health check | `http://localhost:8080/actuator/health` |
| Postman | Importe `postman/Atendimento-SUS.postman_collection.json` |

### Ordem sugerida no Postman

1. **Registrar profissional**.
2. **Login - Profissional** — o script salva `token` e `profissionalId` nas variáveis da collection.
3. **Cadastrar paciente** — o script salva `pacienteId`.
4. **Analisar queixa com IA** — use o exemplo mínimo ou o exemplo com contexto completo.
5. **Realizar triagem** — o script salva `triagemId`.
6. **Agendar consulta** — o script salva `agendamentoId`.

## Análise de queixa com IA

Endpoint protegido para `PROFISSIONAL` e `ADMIN`:

```http
POST /api/triagens/analise
Authorization: Bearer <token>
Content-Type: application/json
```

Request mínimo, compatível com o fluxo inicial:

```json
{
  "queixaLivre": "Estou com febre e tosse há três dias."
}
```

Request enriquecido, que oferece contexto ao assistente:

```json
{
  "queixaLivre": "Dor no peito há duas horas, com falta de ar.",
  "sintomasSelecionados": ["DOR_TORACICA", "FALTA_DE_AR"],
  "frequenciaCardiaca": 112,
  "frequenciaRespiratoria": 24,
  "pressaoSistolica": 145,
  "pressaoDiastolica": 90,
  "temperatura": 37.2,
  "saturacaoOxigenio": 93,
  "escalaDor": 8
}
```

Quando informados, os sinais vitais devem ser enviados integralmente. A resposta contém `sintomasSugeridos`, `resumo`, `perguntasComplementares`, `alertasParaConferencia` e `camposAusentes`.

## Configuração

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host PostgreSQL |
| `DB_PORT` | `5432` | Porta PostgreSQL |
| `DB_NAME` | `atendimento_sus` | Banco de dados |
| `DB_USER` / `DB_PASSWORD` | `postgres` | Credenciais do banco |
| `JWT_SECRET` | valor de desenvolvimento | Segredo JWT; troque em produção |
| `APP_AI_ENABLED` | `true` | Habilita Ollama; use `false` para fallback sem IA |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL do Ollama |
| `OLLAMA_MODEL` | `llama3.2:3b` | Modelo utilizado |
| `OLLAMA_TIMEOUT_SECONDS` | `60` | Timeout das chamadas IA |

## Testes

```bash
./mvnw test
```

Os testes de integração usam Testcontainers e podem exigir Docker disponível. Caso o wrapper não tenha permissão de execução, use:

```bash
bash ./mvnw test
```

## Limitações do MVP

- As regras são uma implementação **no estilo** do protocolo de Manchester; não representam certificação clínica oficial.
- O projeto não substitui avaliação médica ou de enfermagem.
- Uma evolução produtiva deve incluir autorização por recurso/unidade, auditoria de IA, observabilidade e validação clínica formal.

## Materiais de entrega

Antes de submeter o trabalho, preencher no relatório os links públicos para:

- Repositório remoto;
- Drive da entrega;
- Vídeo de pitch;
- Vídeo de demonstração do MVP.

### Gerar o relatório em DOCX

O arquivo Word não é versionado para evitar limitações de visualização de binários em Pull Requests. Antes de enviar os materiais ao Drive, gere-o localmente a partir do relatório Markdown:

```bash
python scripts/generate_report_docx.py
```

O comando cria `docs/Relatorio-SUS-Flow.docx`, que deve ser enviado para a pasta pública da entrega.
