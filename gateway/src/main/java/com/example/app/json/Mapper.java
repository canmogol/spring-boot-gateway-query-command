package com.example.app.json;

import com.example.api.exception.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final JsonMapper jsonMapper;

    public <T> T readList(String value, Class<?> aClass) {
        try {
            return jsonMapper.readValue(value, jsonMapper.getTypeFactory().constructCollectionType(List.class, aClass));
        } catch (JsonProcessingException e) {
            throw new JsonParseException("Could not read List of type: %s".formatted(aClass), e);
        }
    }

    public <T> T readObject(final String value, final Class<?> aClass) {
        try {
            return jsonMapper.readValue(value, jsonMapper.getTypeFactory().constructType(aClass));
        } catch (JsonProcessingException e) {
            throw new JsonParseException("Could not read object of type: %s".formatted(aClass), e);
        }
    }

    public <T> String writeString(final T object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonParseException("Could not write string as JSON", e);
        }

    }
}
