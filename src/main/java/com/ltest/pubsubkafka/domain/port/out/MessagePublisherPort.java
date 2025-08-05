package com.ltest.pubsubkafka.domain.port.out;

/**
 * Porta de Saída (Outbound Port) que define o contrato para publicar mensagens.
 * A implementação desta interface saberá como se comunicar com um sistema de mensageria externo (Kafka).
 */
public interface MessagePublisherPort {

    /**
     * Publica uma mensagem em um destino pré-configurado.
     * @param message A mensagem a ser publicada.
     */
    void publish(String message);
}