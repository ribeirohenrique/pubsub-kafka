package com.test.pubsubkafka.domain.domain.service;

import com.test.pubsubkafka.domain.domain.port.out.MessagePublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingServiceImpl implements MessageProcessingUseCase {

    private final MessagePublisherPort messagePublisherPort;
    private final SchemaValidationService schemaValidationService; // Injeta o serviço de validação

    @Override
    public void process(String message) {
//        log.info("==================================================");
//        log.info("MENSAGEM RECEBIDA DO GCP PUBSUB:");
//        log.info(">> {}", message);
//        log.info("==================================================");

        // 1. Valida a mensagem
        schemaValidationService.validateAndParse(message)
            .ifPresentOrElse(
                // 2. Se for válida, publica
                validMessage -> {
                    try {
                        //log.info("Enviando mensagem validada para a Confluent Cloud Kafka...");
                        messagePublisherPort.publish(validMessage);
                        //log.info("Mensagem enviada com sucesso para o Kafka.");
                    } catch (Exception e) {
                        log.error("Falha ao enviar mensagem para o Kafka após validação.", e);
                        // Lógica de "dead-letter queue" aqui
                    }
                },
                // 3. Se for inválida, loga o erro e para
                () -> {
                    log.error("Processamento interrompido: a mensagem não passou na validação do schema Avro.");
                    // Lógica de "dead-letter queue" para mensagens inválidas aqui
                }
            );
    }
}