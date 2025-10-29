package com.test.pubsubkafka.domain.adapter.in;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.test.pubsubkafka.domain.domain.service.MessageProcessingUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

/**
 * Adapter de Entrada (Inbound Adapter) responsável por escutar mensagens do GCP Pub/Sub.
 */
@Slf4j
@Component
public class PubSubInboundAdapter {

    private final String subscriptionName;
    private final MessageProcessingUseCase messageProcessingUseCase;
    
    // Injeção de dependências via construtor
    public PubSubInboundAdapter(
            @Value("${spring.cloud.gcp.pubsub.subscriber.fully-qualified-name}") String subscriptionName,
            MessageProcessingUseCase messageProcessingUseCase) {
        this.subscriptionName = subscriptionName;
        this.messageProcessingUseCase = messageProcessingUseCase;
    }

    // 1. Cria um canal de mensagens do Spring Integration.
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    // 2. Cria o adapter que conecta a subscription do Pub/Sub ao canal de mensagens.
    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, this.subscriptionName);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL); // Controle manual para garantir o processamento
        adapter.setPayloadType(String.class); // Esperamos que a mensagem seja uma String
        
        return adapter;
    }

    // 3. Cria um "ouvinte" (@ServiceActivator) para o canal de mensagens.
    //    Este metodo será executado para cada mensagem que chegar no canal.
    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            
            try {
                String payload = (String) message.getPayload();
                messageProcessingUseCase.process(payload);
                // Confirma o recebimento da mensagem (ack) para o Pub/Sub
                if (originalMessage != null) {
                    originalMessage.ack();
                }
            } catch (Exception e) {
                log.error("Erro ao processar a mensagem do Pub/Sub. A mensagem não será confirmada (nack).", e);
                // Rejeita a mensagem (nack) para que o Pub/Sub possa reenviá-la
                if (originalMessage != null) {
                    originalMessage.nack();
                }
            }
        };
    }
}