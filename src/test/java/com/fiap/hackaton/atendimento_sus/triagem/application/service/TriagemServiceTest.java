package com.fiap.hackaton.atendimento_sus.triagem.application.service;

import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.RealizarTriagemUseCase.RealizarTriagemCommand;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.AnaliseClinica;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.ContextoTriagem;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.TriagemRepositoryPort;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.NivelRisco;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.SinaisVitais;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Triagem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriagemServiceTest {

    @Mock TriagemRepositoryPort triagemRepository;
    @Mock AssistenteTriagemPort assistente;
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-04T12:00:00Z"), ZoneOffset.UTC);

    private final SinaisVitais sinaisNormais = new SinaisVitais(75, 16, 120, 80, 36.5, 98, 0);

    private TriagemService service() {
        return new TriagemService(triagemRepository, assistente, clock);
    }

    @Test
    void classificaRegistraEAnexaOrientacaoDaIA() {
        when(triagemRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(assistente.gerarOrientacao(any(), any())).thenReturn("Procure atendimento com urgência.");
        TriagemService service = service();

        // Dor torácica → LARANJA (decidido pelo classificador determinístico)
        Triagem t = service.realizar(new RealizarTriagemCommand(
                UUID.randomUUID(), UUID.randomUUID(), sinaisNormais, Set.of(Sintoma.DOR_TORACICA)));

        assertThat(t.getNivelRisco()).isEqualTo(NivelRisco.LARANJA);
        assertThat(t.getOrientacao()).isEqualTo("Procure atendimento com urgência.");
    }

    @Test
    void falhaDaIANaoQuebraATriagem() {
        when(triagemRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        // Adapter best-effort retorna null (equivale a falha/desligado)
        when(assistente.gerarOrientacao(any(), any())).thenReturn(null);

        Triagem t = service().realizar(new RealizarTriagemCommand(
                UUID.randomUUID(), UUID.randomUUID(), sinaisNormais, Set.of(Sintoma.TOSSE)));

        assertThat(t.getNivelRisco()).isEqualTo(NivelRisco.VERDE);
        assertThat(t.getOrientacao()).isNull();
    }

    @Test
    void analisarDelegaAoAssistente() {
        ContextoTriagem contexto = new ContextoTriagem("dor no peito e falta de ar", sinaisNormais, Set.of());
        AnaliseClinica esperado = new AnaliseClinica(EnumSet.of(Sintoma.DOR_TORACICA, Sintoma.FALTA_DE_AR), "dor no peito",
                Set.of("Quando começou?"), Set.of(), Set.of());
        when(assistente.analisar(contexto)).thenReturn(esperado);

        AnaliseClinica resultado = service().analisar(contexto);

        assertThat(resultado.sintomasSugeridos()).containsExactlyInAnyOrder(Sintoma.DOR_TORACICA, Sintoma.FALTA_DE_AR);
    }

}
