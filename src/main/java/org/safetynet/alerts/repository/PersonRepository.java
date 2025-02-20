package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
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
        // Delete medicalRecord corresponding to person
        boolean medicalRecordRemoved = jsonData.getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        if (!medicalRecordRemoved) {
            return false;
        }

        return jsonData.getPersons()
                .removeIf(person -> person.getFullName().equals(fullName));
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

    public List<Person> findAll() {
        return jsonData.getPersons();
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
