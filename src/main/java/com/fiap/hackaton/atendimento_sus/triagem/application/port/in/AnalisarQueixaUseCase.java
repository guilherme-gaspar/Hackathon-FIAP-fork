package com.fiap.hackaton.atendimento_sus.triagem.application.port.in;

import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.AnaliseClinica;
import com.fiap.hackaton.atendimento_sus.triagem.application.port.out.AssistenteTriagemPort.ContextoTriagem;

/**
 * Caso de uso: analisar uma queixa em texto livre e sugerir sintomas
 * estruturados e itens para conferência (pré-preenchimento da triagem). Não persiste nada.
 */
public interface AnalisarQueixaUseCase {

    AnaliseClinica analisar(ContextoTriagem contexto);
}
