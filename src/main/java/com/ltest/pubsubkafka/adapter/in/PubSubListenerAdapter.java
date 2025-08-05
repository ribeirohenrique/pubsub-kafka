package com.ltest.pubsubkafka.adapter.in;

import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.ltest.pubsubkafka.domain.service.MessageProcessingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Adapter de Entrada (Inbound Adapter) que usa a anotação @PubSubListener
 * para uma forma mais simples e direta de consumir mensagens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PubSubListenerAdapter {

    private final MessageProcessingUseCase messageProcessingUseCase;

    /**
     * Este método é ativado automaticamente para cada mensagem recebida na subscription
     * definida em 'app.gcp.pubsub.subscription-name'.
     *
     * @param payload A mensagem recebida, convertida para String.
     * @param message O objeto original da mensagem, usado para acknowledge (ack/nack).
     */
    @ServiceActivator(inputChannel = "topico-teste-pubsub-sub")
    public void receiveMessage(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        
        log.info("Mensagem recebida via @PubSubListener com ID: {}", message.getPubsubMessage().getMessageId());

        try {
            // Chama o serviço central para processar a mensagem
            messageProcessingUseCase.process(payload);
            
            // Se o processamento foi bem-sucedido, confirma a mensagem (ack).
            // O Pub/Sub não a enviará novamente.
            message.ack();
            log.debug("Mensagem {} confirmada (acked).", message.getPubsubMessage().getMessageId());

        } catch (Exception e) {
            log.error("Erro ao processar mensagem {}. A mensagem será rejeitada (nacked).", 
                      message.getPubsubMessage().getMessageId(), e);
                      
            // Se ocorreu um erro, rejeita a mensagem (nack).
            // O Pub/Sub tentará reenviá-la após o tempo de 'ack deadline'.
            message.nack();
        }
    }
}