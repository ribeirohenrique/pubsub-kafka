package com.ltest.pubsubkafka.domain.service;

import com.ltest.pubsubkafka.domain.port.out.MessagePublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementação do serviço que orquestra a lógica de negócio.
 * Recebe a mensagem, a exibe e a envia para a porta de publicação.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingServiceImpl implements MessageProcessingUseCase {

    private final MessagePublisherPort messagePublisherPort;

    @Override
    public void process(String message) {
        log.info("==================================================");
        log.info("MENSAGEM RECEBIDA DO GCP PUBSUB:");
        log.info(">> {}", message);
        log.info("==================================================");
        
        try {
            log.info("Enviando mensagem para a Confluent Cloud Kafka...");
            messagePublisherPort.publish(message);
            log.info("Mensagem enviada com sucesso para o Kafka.");
        } catch (Exception e) {
            log.error("Falha ao enviar mensagem para o Kafka.", e);
            // Aqui você pode adicionar uma lógica para tratar o erro,
            // como enviar para uma "dead-letter queue".
        }
    }
}