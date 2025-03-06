package org.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class JsonDataService implements ApplicationRunner {

    private static JsonData jsonData;

    @Value("${json.data.path}")
    private String jsonPath;

    private static ObjectMapper objectMapper = null;

    public JsonDataService(ObjectMapper objectMapper) {
        JsonDataService.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        init(jsonPath);
    }

    public static void init(String jsonPath) {
        log.info("Initializing JSON data from path: {}", jsonPath);

        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
            log.info("Data loaded successfully !");
        } catch (FileNotFoundException e) {
            log.error("JSON file not found at path '{}'.", jsonPath, e);
            throw new RuntimeException("JSON file not found", e);
        } catch (IOException e) {
            log.error("I/O error while loading JSON data", e);
            throw new RuntimeException("I/O error while loading JSON data", e);
        }
    }

    public static JsonData getJsonData() {
        return jsonData;
    }
}

