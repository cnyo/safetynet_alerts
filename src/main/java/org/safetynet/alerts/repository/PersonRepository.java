package org.safetynet.alerts.repository;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PersonRepository {

    protected final JsonData jsonData;

    public PersonRepository(JsonDataLoader jsonDataLoader) {
        this.jsonData = jsonDataLoader.getJsonData();
    }

    public Person createPerson(Person person) {
        if (person == null || person.getFullName() == null) {
            throw new NullPointerException("Invalid person data");
        }

        if (findOneByFullName(person.getFullName()) != null) {
            throw new IllegalArgumentException("Person already exists");
        }

        jsonData.getPersons().add(person);

        return person;
    }

    public Person updatePerson(Person person, Person currentPerson) {
        return currentPerson
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setAddress(person.getAddress())
                .setCity(person.getCity())
                .setZip(person.getZip())
                .setEmail(person.getEmail())
                .setPhone(person.getPhone());
    }

    public boolean remove(Person personToRemove) {
        boolean personResult = jsonData.getPersons()
                .removeIf(person -> person.getFullName().equals(personToRemove.getFullName()));

        boolean medicalRecordResult = jsonData.getMedicalRecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(personToRemove.getFullName()));

        if (!(personResult && medicalRecordResult)) {
            throw new IllegalArgumentException("Person or medical record not removed");
        }

        return true;
    }

    public List<Person> findAdultAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(person -> person.getMedicalRecord() != null && person.getMedicalRecord().isAdult())
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonByLastName(String lastName) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonByCity(String city) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getCity().equals(city))
                .collect(Collectors.toList());
    }

    public Person findOneByFullName(String fullName) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getFullName().equals(fullName))
                .findFirst()
                .orElse(null);
    }

    public List<Person> findAllPersonFromFireStation(List<FireStation> fireStations) {
        List<String> addresses = fireStations.stream().map(FireStation::getAddress).toList();

        return jsonData.getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    public List<Person> findAllChildrenAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(p -> p.getMedicalRecord() != null && p.getMedicalRecord().isChild())
                .collect(Collectors.toList());
    }

    public Person addFireStationToPerson(Person person, FireStation fireStation) {
        person.getFireStations().add(fireStation);

        return person;
    }

    public Person attachMedicalRecordToPerson(Person person, MedicalRecord medicalRecord) {
        return person.setMedicalRecord(medicalRecord);
    }
}
