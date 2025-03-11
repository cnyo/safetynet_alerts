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

/**
 * Service class responsible for loading and providing access to JSON data.
 * The JSON data is initialized from a file path specified in the application configuration
 * and mapped to a Java object using Jackson's ObjectMapper.
 *
 * This class implements the {@link ApplicationRunner} interface,
 * ensuring that the data is loaded during the application startup phase.
 */
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

    /**
     * Executes the initialization process for loading JSON data during the application startup.
     *
     * @param args the arguments passed to the application at startup, provided by the Spring framework
     */
    @Override
    public void run(ApplicationArguments args) {
        init(jsonPath);
    }

    /**
     * Initializes the JSON data by loading it from the provided file path.
     * The method reads the JSON file, maps its content to a JsonData object,
     * and logs the success or failure of the operation.
     *
     * @param jsonPath the path to the JSON file to be loaded
     * @throws RuntimeException if the JSON file is not found or an I/O error occurs
     */
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

    /**
     * Retrieves the loaded JSON data as a JsonData object.
     * This method provides access to the data initialized during the application startup phase.
     *
     * @return an instance of {@code JsonData} containing the loaded JSON data, or {@code null} if the data has not been initialized.
     */
    public static JsonData getJsonData() {
        return jsonData;
    }
}

