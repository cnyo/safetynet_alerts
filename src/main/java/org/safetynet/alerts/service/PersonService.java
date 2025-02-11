package org.safetynet.alerts.service;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final int MAJORITY_AGE = 18;

    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person person) {

        return personRepository.createPerson(person);
    }

    public Person updatePerson(Person person, Person currentPerson) {
        return personRepository.updatePerson(person, currentPerson);
    }

    public Person removePerson(Person person) {
        return personRepository.removePerson(person);
    }

    public List<String> getAllPhoneNumbersFromPersons(List<Person> persons) {
        return persons
                .stream()
                .map(Person::getPhone)
                .collect(Collectors.toList());
    }

    public List<String> getAllEmailsFromPersons(List<Person> persons) {
        return persons.stream().map(Person::getEmail).toList();
    }

    public int countAdultFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord() != null && person.getMedicalRecord().getAge() >= MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }

    public int countChildrenFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord() != null && person.getMedicalRecord().getAge() < MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }

    public List<Person> getAdultAtAddress(String address) {
        return personRepository.findAdultAtAddress(address);
    }

    public List<Person> getAllPersonAtAddress(String address) {
        return personRepository.findAllPersonAtAddress(address);
    }

    public List<Person> getAllPersonByFireStations(List<FireStation> fireStations) {
        return personRepository.findAllPersonByFireStations(fireStations);
    }

    public List<Person> getAllPersonByLastName(String lastName) {
        return personRepository.findAllPersonByLastName(lastName);
    }

    public List<Person> getAllPersonByCity(String city) {
        return personRepository.findAllPersonByCity(city);
    }

    public Person getPersonByFullName(String fullName) {
        return personRepository.findOneByFullName(fullName);
    }

    public List<Person> getAllPersonFromFireStation(List<FireStation> fireStations) {
        return personRepository.findAllPersonFromFireStation(fireStations);
    }

    public List<Person> getChildrenAtAddress(String address) {
        return personRepository.findAllChildrenAtAddress(address);
    }
}