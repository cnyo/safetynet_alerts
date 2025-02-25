package org.safetynet.alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JsonDataService {

    final String JSON_PATH = "json/data.json";
    private static JsonData jsonData;

    public JsonDataService() {
    }

    @PostConstruct
    public void init() throws IOException {
        if (jsonData == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new ClassPathResource(JSON_PATH).getFile();
            jsonData = objectMapper.readValue(jsonFile, JsonData.class);

            jsonData.getPersons().forEach(person -> setMedicalRecordForPerson(person, jsonData.getMedicalrecords()));
        }
    }

    private void setMedicalRecordForPerson(Person person, List<MedicalRecord> medicalRecords) {
        for (MedicalRecord medicalRecord : medicalRecords) {
            if (medicalRecord.getFullName().equals(person.getFullName())) {
                person.setMedicalRecord(medicalRecord);
            }
        }
    }

    public List<Person> getAllPersonFromFireStation(List<FireStation> fireStations) {
        List<String> addresses = fireStations.stream().map(FireStation::getAddress).toList();
        List<Person> persons = new ArrayList<>();

        for (Person person : jsonData.getPersons()) {
            if (addresses.contains(person.getAddress())) {
                persons.add(person);
            }
        }

        return persons;
    }

    public List<FireStation> getAllFireStationByStation(String stationNumber) {
        return jsonData.getFirestations().stream().filter(f -> f.getStation().equals(stationNumber)).toList();
    }

    public List<Person> getChildrenAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(p -> p.getMedicalRecord().isChild()).collect(Collectors.toList());
    }

    public List<Person> getAdultAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(p -> p.getMedicalRecord().isAdult())
                .collect(Collectors.toList());
    }

    public FireStation getFireStationAtAddress(String address) {
        return jsonData.getFirestations().stream().filter(f -> f.getAddress().equals(address)).findFirst().orElse(null);
    }

    public List<FireStation> filterFireStationForStations(String stations) {
        String[] stationNumbers = stations.split(",");

        return jsonData.getFirestations()
                .stream()
                .filter(f -> List.of(stationNumbers).contains(f.getStation()))
                .collect(Collectors.toList());
    }

    public List<Person> getAllPersonAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public List<Person> getAllPersonByFireStations(List<FireStation> fireStations) {
        List<String> addresses = fireStations.stream().map(FireStation::getAddress).toList();
        List<Person> persons = new ArrayList<>();

        for (String address : addresses) {

            List<Person> personsAtAddress = jsonData
                    .getPersons()
                    .stream()
                    .filter(person -> person.getAddress().equals(address))
                    .toList();

            persons.addAll(personsAtAddress);
        }

        return persons;
    }

    public List<Person> getAllPersonByLastName(String lastName) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    public List<Person> getAllPersonByCity(String city) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getCity().equals(city))
                .collect(Collectors.toList());
    }

    public Person createPerson(Person person) {
        jsonData.addPerson(person);

        return person;
    }

    public Person updatePerson(Person person, Person currentPerson) {
        return currentPerson
                .setFirstName(person.getFirstName())
                .setLastName(person.getFirstName())
                .setAddress(person.getAddress())
                .setCity(person.getCity())
                .setZip(person.getZip())
                .setEmail(person.getEmail())
                .setPhone(person.getPhone())
                .setAddress(person.getAddress());
    }

    public Person removePerson(Person person) {
        Person personToRemove = jsonData.getPersonByFullName(person.getFullName());
        jsonData.removePerson(personToRemove);

        return person;
    }

    public Person getPersonByFullName(String fullName) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getFullName().equals(fullName))
                .findFirst()
                .orElse(null);
    }
}
