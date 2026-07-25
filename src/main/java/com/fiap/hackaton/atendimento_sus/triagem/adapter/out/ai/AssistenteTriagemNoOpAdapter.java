package com.fiap.hackaton.atendimento_sus.triagem.adapter.out.ai;

import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.ContextoTriagem;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.NivelRisco;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * Fallback usado quando a IA está desligada ({@code app.ai.ollama.enabled} != true).
 * Mantém a triagem 100% funcional sem Ollama (inclusive em testes/CI).
 */
@Component
@ConditionalOnProperty(name = "app.ai.ollama.enabled", havingValue = "false", matchIfMissing = true)
public class AssistenteTriagemNoOpAdapter implements AssistenteTriagemPort {

    @Override
    public AnaliseClinica analisar(ContextoTriagem contexto) {
        return new AnaliseClinica(EnumSet.noneOf(Sintoma.class), null, Set.of(), Set.of(), Set.of());
    }

    @Override
    public String gerarOrientacao(NivelRisco nivel, Set<Sintoma> sintomas) {
        return null;
    }

}
