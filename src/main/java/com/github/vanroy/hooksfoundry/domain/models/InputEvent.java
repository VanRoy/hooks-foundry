package com.github.vanroy.hooksfoundry.domain.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.io.IOException;

public class InputEvent {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ParseContext JSON_PATH_CONTEXT = JsonPath
            .using(Configuration.builder()
                    .jsonProvider(new JacksonJsonNodeJsonProvider())
                    .mappingProvider(new JacksonMappingProvider())
                    .build()
            );

    private final byte[] data;
    private final JsonNode json;
    private final DocumentContext jsonPathReader;

    InputEvent(byte[] data) throws IOException {
        this.data = data;
        this.json = MAPPER.readTree(data);
        this.jsonPathReader = JSON_PATH_CONTEXT.parse(this.json);
    }

    public static InputEvent of(byte[] data) throws IOException {
        return new InputEvent(data);
    }

    public JsonNode asJsonNode() {
        return this.json;
    }

    public String asString() {
        return new String(this.data);
    }

    public <T> T readPath(JsonPath value) {
        return this.jsonPathReader.read(value);
    }

    public <T> T readPath(JsonPath value, Class<T> type) {
        return this.jsonPathReader.read(value, type);
    }
}
