package com.ltest.pubsubkafka.domain.service;

public interface MessageProcessingUseCase {
    /**
     * Processa a mensagem raw (String) recebida do PubSub.
     * @param message O conteúdo da mensagem.
     */
    void process(String message);
}