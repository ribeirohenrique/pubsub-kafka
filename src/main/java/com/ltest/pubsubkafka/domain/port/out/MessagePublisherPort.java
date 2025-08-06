package com.ltest.pubsubkafka.domain.port.out;


import br.com.cafe.especial.CustomSchema;

public interface MessagePublisherPort {
    /**
     * Publica uma mensagem Avro-compatível.
     * @param message O objeto da mensagem a ser publicado.
     */
    void publish(CustomSchema message);
}