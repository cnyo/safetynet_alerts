package org.safetynet.alerts.service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JsonDataService {

    @Value("${json.data.path}")
    private String jsonPath;

    @Getter
    private JsonData jsonData;

    @Autowired
    ObjectMapper objectMapper;

    public JsonDataService() {
        log.info("JsonDataLoader instantiated");
    }

    @PostConstruct
    public void init() {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper2.readValue(inputStreamJson, JsonData.class);
            processJsonData();
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

    private void processJsonData() {
        Map<String, List<FireStation>> fireStationMap = jsonData.getFirestations()
                .stream()
                .collect(Collectors.groupingBy(FireStation::getAddress));

        jsonData
                .getPersons()
                .forEach(person -> {
                    person.setMedicalRecord(matchMedicalRecordForPerson(person));
                    person.setFireStations(fireStationMap.get(person.getAddress()));
                });
    }

    private MedicalRecord matchMedicalRecordForPerson(Person person) {
        Map<String, MedicalRecord> medicalRecordMap = jsonData.getMedicalrecords()
                .stream()
                .collect(Collectors.toMap(MedicalRecord::getFullName, medicalRecord -> medicalRecord));

        MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());

        if (medicalRecord == null) {
            return new MedicalRecord();
        }

        return medicalRecord;
    }

    public JsonData getJsonData() {
        return this.jsonData;
    }
}

