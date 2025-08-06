package com.ltest.pubsubkafka.adapter.out;


import com.ltest.pubsubkafka.domain.port.out.MessagePublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adapter de Saída (Outbound Adapter) que implementa a porta de publicação,
 * enviando mensagens para a Confluent Cloud via KafkaTemplate.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOutboundAdapter implements MessagePublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.confluent.kafka.topic-name}")
    private String topicName;

    @Override
    public void publish(String message) {
        // kafkaTemplate.send é assíncrono. Adicionamos um callback para logar o resultado.
        kafkaTemplate.send(topicName, message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Mensagem publicada com sucesso no tópico Kafka '{}', partição {}, offset {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                // O erro já será logado pelo listener de erro padrão do Kafka,
                // mas podemos logar aqui também se quisermos mais detalhes.
                log.error("Falha ao publicar mensagem no tópico Kafka '{}'", topicName, ex);
                // Lançar uma exceção aqui fará com que o try-catch no serviço a capture.
                throw new RuntimeException("Falha ao publicar no Kafka", ex);
            }
        });
    }
}