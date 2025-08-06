package com.ltest.pubsubkafka.domain.service;

import br.com.cafe.especial.CustomSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaValidationService {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Valida uma string JSON e a converte para o POJO CustomSchema.
     * @param jsonMessage A mensagem em formato JSON a ser validada.
     * @return Um Optional contendo o objeto CustomSchema se a validação for bem-sucedida,
     * ou um Optional vazio caso contrário.
     */
    public Optional<CustomSchema> validateAndParse(String jsonMessage) {
        try {
            CustomSchema message = objectMapper.readValue(jsonMessage, CustomSchema.class);
            //log.info("Mensagem JSON validada com sucesso e convertida para o formato Avro POJO.");
            return Optional.of(message);
        } catch (Exception e) {
            log.error("Falha na validação do schema. A mensagem JSON é incompatível. Mensagem: '{}'", jsonMessage, e);
            return Optional.empty();
        }
    }
}