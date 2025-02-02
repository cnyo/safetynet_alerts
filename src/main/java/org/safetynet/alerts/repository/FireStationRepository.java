package org.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FireStationRepository extends AbstractBaseRepository {

    public List<String> findAllAddressByStation(String stationNumber) throws IOException {
        List<String> addresses = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource(JSON_PATH).getFile();
        JsonNode jsonNodeRoot = objectMapper.readTree(jsonFile);
        JsonNode jsonNodeFireStations = jsonNodeRoot.get("firestations");

        for (JsonNode jsonNodeFireStation : jsonNodeFireStations) {
            if (jsonNodeFireStation.get("station").asText().equals(stationNumber)) {
                addresses.add(jsonNodeFireStation.get("address").asText());
            }
        }

        return addresses;
    }
}
