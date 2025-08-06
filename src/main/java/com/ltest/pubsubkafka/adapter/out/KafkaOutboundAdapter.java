package com.ltest.pubsubkafka.adapter.out;


import br.com.cafe.especial.CustomSchema;
import com.ltest.pubsubkafka.domain.port.out.MessagePublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOutboundAdapter implements MessagePublisherPort {

    // O KafkaTemplate agora é tipado com o POJO CustomSchema
    private final KafkaTemplate<String, CustomSchema> kafkaTemplate;

    @Value("${app.confluent.kafka.topic-name}")
    private String topicName;

    @Override
    public void publish(CustomSchema message) {
        // Envia o objeto diretamente. O KafkaAvroSerializer cuidará da serialização.
        kafkaTemplate.send(topicName, message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Mensagem (POJO) publicada com sucesso no tópico Kafka '{}'",
                        result.getRecordMetadata().topic());
            } else {
                log.error("Falha ao publicar mensagem (POJO) no tópico Kafka '{}'", topicName, ex);
                throw new RuntimeException("Falha ao publicar no Kafka", ex);
            }
        });
    }
}