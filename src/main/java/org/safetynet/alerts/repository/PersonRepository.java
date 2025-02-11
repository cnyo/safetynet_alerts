package org.safetynet.alerts.repository;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataLoader;
import org.safetynet.alerts.service.PersonService;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PersonRepository {

    protected final JsonData jsonData;
    private final PersonService personService;

    public PersonRepository(JsonDataLoader jsonDataLoader, PersonService personService) {
        this.jsonData = jsonDataLoader.getJsonData();
        this.personService = personService;
    }

    public Person createPerson(Person person) {
        jsonData.getPersons().add(person);

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
        Person personToRemove = personService.getPersonByFullName(person.getFullName());
        jsonData.getPersons().remove(personToRemove);

        return person;
    }

    public List<Person> findAdultAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(p -> p.getMedicalRecord().isAdult())
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonByFireStations(List<FireStation> fireStations) {
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
        List<Person> persons = new ArrayList<>();

        for (Person person : jsonData.getPersons()) {
            if (addresses.contains(person.getAddress())) {
                persons.add(person);
            }
        }

        return persons;
    }

    public List<Person> findAllChildrenAtAddress(String address) {
        return jsonData
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .filter(p -> p.getMedicalRecord().isChild()).collect(Collectors.toList());
    }
}
