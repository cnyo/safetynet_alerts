package org.safetynet.alerts.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class JsonDataService {

    private JsonData jsonData;

    @Autowired
    ObjectMapper objectMapper;

    public JsonDataService() {
        log.info("JsonDataLoader instantiated");
    }

    public void init(String jsonPath) {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
            log.info("Données chargées avec succès !");
        } catch (StreamReadException e) {
            log.error("Failed to read the JSON stream.", e);
            throw new RuntimeException("Failed to read the JSON stream.", e);
        } catch (DatabindException e) {
            log.error("Failed to bind JSON data to Java objects.", e);
            throw new RuntimeException("Failed to bind JSON data to Java objects.", e);
        } catch (IOException e) {
            log.error("I/O error while loading JSON data.", e);
            throw new RuntimeException("I/O error while loading JSON data.", e);
        }
    }

    public JsonData getJsonData() {
        return this.jsonData;
    }
}

