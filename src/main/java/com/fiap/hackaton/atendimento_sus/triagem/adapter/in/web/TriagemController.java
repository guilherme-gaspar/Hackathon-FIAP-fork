package com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web;

import com.fiap.hackaton.atendimento_sus.shared.security.AuthenticatedUser;
import com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto.AnaliseClinicaResponse;
import com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto.AnalisarQueixaRequest;
import com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto.RealizarTriagemRequest;
import com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto.TriagemResponse;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.AnalisarQueixaUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.ConsultarTriagemUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.RealizarTriagemUseCase;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.in.RealizarTriagemUseCase.RealizarTriagemCommand;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.ContextoTriagem;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.SinaisVitais;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/triagens")
@Tag(name = "Triagem", description = "Classificação de risco (protocolo de Manchester)")
public class TriagemController {

    private final RealizarTriagemUseCase realizarTriagem;
    private final ConsultarTriagemUseCase consultarTriagem;
    private final AnalisarQueixaUseCase analisarQueixa;

    public TriagemController(RealizarTriagemUseCase realizarTriagem, ConsultarTriagemUseCase consultarTriagem,
                             AnalisarQueixaUseCase analisarQueixa) {
        this.realizarTriagem = realizarTriagem;
        this.consultarTriagem = consultarTriagem;
        this.analisarQueixa = analisarQueixa;
    }

    @PostMapping("/analise")
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @Operation(summary = "Analisa uma queixa em texto livre (IA) e sugere sintomas estruturados")
    public ResponseEntity<AnaliseClinicaResponse> analisar(@Valid @RequestBody AnalisarQueixaRequest req) {
        return ResponseEntity.ok(AnaliseClinicaResponse.de(analisarQueixa.analisar(contextoDe(req))));
    }

    private ContextoTriagem contextoDe(AnalisarQueixaRequest req) {
        Set<Sintoma> sintomas = (req.sintomasSelecionados() == null || req.sintomasSelecionados().isEmpty())
                ? EnumSet.noneOf(Sintoma.class) : EnumSet.copyOf(req.sintomasSelecionados());
        if (req.frequenciaCardiaca() == null || req.frequenciaRespiratoria() == null || req.pressaoSistolica() == null
                || req.pressaoDiastolica() == null || req.temperatura() == null || req.saturacaoOxigenio() == null
                || req.escalaDor() == null) {
            return new ContextoTriagem(req.queixaLivre(), null, sintomas);
        }
        SinaisVitais sinais = new SinaisVitais(req.frequenciaCardiaca(), req.frequenciaRespiratoria(),
                req.pressaoSistolica(), req.pressaoDiastolica(), req.temperatura(), req.saturacaoOxigenio(), req.escalaDor());
        return new ContextoTriagem(req.queixaLivre(), sinais, sintomas);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFISSIONAL', 'ADMIN')")
    @Operation(summary = "Realiza a triagem: classifica o risco e registra")
    public ResponseEntity<TriagemResponse> realizar(@Valid @RequestBody RealizarTriagemRequest req,
                                                    @AuthenticationPrincipal AuthenticatedUser usuario) {
        SinaisVitais sinais = new SinaisVitais(
                req.frequenciaCardiaca(), req.frequenciaRespiratoria(), req.pressaoSistolica(),
                req.pressaoDiastolica(), req.temperatura(), req.saturacaoOxigenio(), req.escalaDor());
        Set<Sintoma> sintomas = (req.sintomas() == null || req.sintomas().isEmpty())
                ? EnumSet.noneOf(Sintoma.class) : EnumSet.copyOf(req.sintomas());
        var triagem = realizarTriagem.realizar(
                new RealizarTriagemCommand(req.pacienteId(), usuario.id(), sinais, sintomas));
        return ResponseEntity.status(HttpStatus.CREATED).body(TriagemResponse.de(triagem));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta uma triagem por id")
    public ResponseEntity<TriagemResponse> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(TriagemResponse.de(consultarTriagem.buscarPorId(id)));
    }
}
