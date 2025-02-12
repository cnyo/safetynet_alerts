package org.safetynet.alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JsonDataLoader {

    @Value("${json.data.path}")
    private String jsonPath;

    private static JsonData jsonData;

    final static Logger LOGGER = LoggerFactory.getLogger(JsonDataLoader.class);

    private final ObjectMapper objectMapper;

    public JsonDataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Loading json data from {}", jsonPath);

        if (jsonData == null) {
            try {
                loadJsonData();
            } catch (Exception e) {
                LOGGER.error("Error loading json data from {}", jsonPath);
                throw new RuntimeException("Failed to load json data from " + jsonPath, e);
            }
        }

        processJsonData();
    }

    private void loadJsonData() throws IOException {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
        }
    }

    private void processJsonData() {
        Map<String, List<FireStation>> fireStationMap = jsonData.getFireStations()
                .stream()
                .collect(Collectors.groupingBy(FireStation::getAddress));

        jsonData
                .getPersons()
                .forEach(person -> {
                    person.setMedicalRecord(getMedicalRecordForPerson(person));
                    person.setFireStations(fireStationMap.get(person.getAddress()));
                });
    }

    private MedicalRecord getMedicalRecordForPerson(Person person) {
        Map<String, MedicalRecord> medicalRecordMap = jsonData.getMedicalRecords()
                .stream()
                .collect(Collectors.toMap(MedicalRecord::getFullName, medicalRecord -> medicalRecord));

        MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());

        if (medicalRecord == null) {
            return new MedicalRecord();
        }

        return medicalRecord;
    }

    public JsonData getJsonData() {
        return jsonData;
    }
}

