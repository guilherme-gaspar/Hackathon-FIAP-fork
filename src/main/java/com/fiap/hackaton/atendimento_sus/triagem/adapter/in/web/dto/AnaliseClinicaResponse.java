package com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto;

import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.AnaliseClinica;
import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;

import java.util.Set;

/** Sugestão da IA para revisão humana. Não é uma triagem persistida. */
public record AnaliseClinicaResponse(Set<Sintoma> sintomasSugeridos, String resumo,
                                    Set<String> perguntasComplementares, Set<String> alertasParaConferencia,
                                    Set<String> camposAusentes) {

    public static AnaliseClinicaResponse de(AnaliseClinica a) {
        return new AnaliseClinicaResponse(a.sintomasSugeridos(), a.resumo(), a.perguntasComplementares(),
                a.alertasParaConferencia(), a.camposAusentes());
    }
}
