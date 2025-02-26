package org.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class JsonDataService {

    private JsonData jsonData;

    @Autowired
    private ObjectMapper objectMapper;

    public void init(String jsonPath) {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
            log.info("Data loaded successfully !");
        } catch (UnrecognizedPropertyException e) {
            log.error("Unrecognized property '{}' in JSON file '{}'. Check if your JsonData class matches the JSON structure.", e.getPropertyName(), jsonPath, e);
            throw new RuntimeException("Invalid property found in JSON: " + e.getPropertyName(), e);
        } catch (JsonMappingException e) {
            log.error("JSON mapping error in file '{}'. Failed to map JSON to object '{}' at path '{}'. Possible type mismatch or missing annotation.",
                    jsonPath, e.getPathReference(), e.getPath(), e);
            throw new RuntimeException("JSON mapping error in file: " + jsonPath, e);
        } catch (IOException e) {
            log.error("I/O error while loading JSON data", e);
            throw new RuntimeException("I/O error while loading JSON data", e);
        }
    }

    public JsonData getJsonData() {
        return this.jsonData;
    }
}

