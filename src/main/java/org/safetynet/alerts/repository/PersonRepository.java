package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PersonRepository {

    protected final JsonData jsonData;

    public PersonRepository(JsonDataService jsonDataService) {
        this.jsonData = jsonDataService.getJsonData();
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

    public boolean remove(String fullName) {
        boolean personResult = jsonData.getPersons()
                .removeIf(person -> person.getFullName().equals(fullName));

        boolean medicalRecordResult = jsonData.getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        if (!(personResult && medicalRecordResult)) {
            throw new IllegalArgumentException("Person or medical record not removed");
        }

        return true;
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
                .orElseThrow(() -> new NoSuchElementException("Person not found"));
    }

    public List<Person> findAllPersonFromAddresses(List<String> addresses) {
        return jsonData.getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    public List<Person> findAllForFullNameAtAddress(String address, List<String> fullNames) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address) && fullNames.contains(person.getFullName()))
                .collect(Collectors.toList());
    }

    public Person addFireStationToPerson(Person person, FireStation fireStation) {
//        person.getFireStations().add(fireStation);

        return person;
    }

    public void attachFireStation(FireStation savedFireStation) {
        List<Person> persons = findAllPersonAtAddress(savedFireStation.getAddress());
        persons.forEach(person -> addFireStationToPerson(person, savedFireStation));
    }

    public List<Person> findAll() {
        return jsonData.getPersons();
    }

    public long countPersonByFullName(String fullName) {
        return jsonData.getPersons().stream().filter(person -> person.getFullName().equals(fullName)).count();
    }

    public List<String> findPhoneNumbersFromAddresses(List<String> addresses) {
        return jsonData.getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .map(Person::getPhone).toList();
    }

    public List<String> findAllEmailsAtCity(String city) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getCity().equals(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());
    }
}
