package org.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.safetynet.alerts.model.FireStation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
public class FireStationRepository {
    final String JSON_PATH = "resources/json/data.json";

    public List<FireStation> findAll() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Charger le fichier depuis resources/json/data.json
        File jsonFile = new ClassPathResource("json/data.json").getFile();
        JsonNode jsonNodeRoot = objectMapper.readTree(jsonFile);
        JsonNode jsonNodeFireStation = jsonNodeRoot.get("firestations");

        return objectMapper.readValue(jsonNodeFireStation.toString(), new TypeReference<List<FireStation>>() {});
    }
}
