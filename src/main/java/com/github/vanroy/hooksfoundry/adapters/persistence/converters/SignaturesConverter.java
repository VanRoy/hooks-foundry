package com.github.vanroy.hooksfoundry.adapters.persistence.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.github.vanroy.hooksfoundry.domain.models.Signature;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Deque;

@Slf4j
@Component
public class SignaturesConverter {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .findAndRegisterModules();

    private final TypeReference<Deque<Signature>> type = new TypeReference<>() {
    };

    private final ObjectReader reader = this.objectMapper.readerFor(type);
    private final ObjectWriter writer = this.objectMapper.writerFor(type);

    public Deque<Signature> from(PGobject source) {
        if (source == null) {
            return null;
        }
        try {
            return reader.readValue(source.getValue());
        } catch (JsonProcessingException e) {
            log.error("Cannot deserialize signatures", e);
            return null;
        }
    }

    public PGobject to(Deque<Signature> signatures) {
        try {
            String value = writer.writeValueAsString(signatures);
            var object = new PGobject();
            object.setType("jsonb");
            object.setValue(value);
            return object;
        } catch (JsonProcessingException | SQLException e) {
            log.error("Cannot serialize signatures", e);
            return null;
        }
    }
}
