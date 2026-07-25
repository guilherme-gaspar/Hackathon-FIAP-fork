package com.fiap.hackaton.atendimento_sus.triagem.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;

import com.fiap.hackaton.atendimento_sus.triagem.domain.model.Sintoma;

import java.util.Set;

/** Queixa em texto livre para pré-análise assistida por IA. */
public record AnalisarQueixaRequest(
        @NotBlank String queixaLivre,
        Integer frequenciaCardiaca,
        Integer frequenciaRespiratoria,
        Integer pressaoSistolica,
        Integer pressaoDiastolica,
        Double temperatura,
        Integer saturacaoOxigenio,
        Integer escalaDor,
        Set<Sintoma> sintomasSelecionados) {

    @AssertTrue(message = "Os sinais vitais devem ser informados integralmente")
    public boolean temSinaisVitaisCompletos() {
        int informados = 0;
        if (frequenciaCardiaca != null) informados++;
        if (frequenciaRespiratoria != null) informados++;
        if (pressaoSistolica != null) informados++;
        if (pressaoDiastolica != null) informados++;
        if (temperatura != null) informados++;
        if (saturacaoOxigenio != null) informados++;
        if (escalaDor != null) informados++;
        return informados == 0 || informados == 7;
    }
}
