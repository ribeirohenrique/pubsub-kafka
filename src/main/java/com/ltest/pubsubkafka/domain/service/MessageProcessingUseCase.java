package com.ltest.pubsubkafka.domain.service;

/**
 * Use Case que define a principal lógica de negócio da aplicação.
 */
public interface MessageProcessingUseCase {

    /**
     * Processa uma mensagem recebida.
     * @param message O conteúdo da mensagem.
     */
    void process(String message);
}