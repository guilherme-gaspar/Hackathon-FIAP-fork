package com.fiap.hackaton.atendimento_sus.triagem.application.service;

import com.fiap.hackaton.atendimento_sus.shared.exception.NotFoundException;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.AnalisarQueixaUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.ConsultarTriagemUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.RealizarTriagemUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.AnaliseClinica;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.TriagemRepositoryPort;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.NivelRisco;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Triagem;
import com.fiap.hackaton.atendimento_sus.triagem.domain.service.ClassificadorRiscoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class TriagemService implements RealizarTriagemUseCase, ConsultarTriagemUseCase, AnalisarQueixaUseCase {

    private final TriagemRepositoryPort triagemRepository;
    private final AssistenteTriagemPort assistente;
    private final ClassificadorRiscoService classificador;
    private final Clock clock;

    public TriagemService(TriagemRepositoryPort triagemRepository, AssistenteTriagemPort assistente, Clock clock) {
        this.triagemRepository = triagemRepository;
        this.assistente = assistente;
        this.clock = clock;
        // Serviço de domínio puro, sem dependências de framework: instanciado diretamente.
        this.classificador = new ClassificadorRiscoService();
    }

    @Override
    @Transactional
    public Triagem realizar(RealizarTriagemCommand command) {
        // O risco é decidido pelo classificador determinístico — a IA nunca decide.
        NivelRisco nivelRisco = classificador.classificar(command.sinaisVitais(), command.sintomas());
        Triagem triagem = Triagem.registrar(command.pacienteId(), command.profissionalId(),
                command.sinaisVitais(), command.sintomas(), nivelRisco, clock);
        // Orientação por IA é best-effort; adapter degrada para null em falha/desligado.
        triagem.atribuirOrientacao(assistente.gerarOrientacao(nivelRisco, command.sintomas()));
        return triagemRepository.salvar(triagem);
    }

    @Override
    @Transactional(readOnly = true)
    public Triagem buscarPorId(UUID id) {
        return triagemRepository.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Triagem não encontrada: " + id));
    }

    @Override
    public AnaliseClinica analisar(AssistenteTriagemPort.ContextoTriagem contexto) {
        return assistente.analisar(contexto);
    }
}
