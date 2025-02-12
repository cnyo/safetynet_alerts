package org.safetynet.alerts.service;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean removePerson(Person person) {
        return personRepository.remove(person);
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

    public Integer countAdultFromPersons(List<Person> persons) {
        return (int) persons.stream()
                .filter(person -> person.getMedicalRecord() != null && person.getMedicalRecord().getAge() >= MAJORITY_AGE)
                .count();
    }

    public int countChildrenFromPersons(List<Person> persons) {
        return (int) persons.stream()
                .filter(person -> person.getMedicalRecord() != null && person.getMedicalRecord().getAge() < MAJORITY_AGE)
                .count();
    }

    public List<Person> getAdultAtAddress(String address) {
        return personRepository.findAdultAtAddress(address);
    }

    public List<Person> getAllPersonAtAddress(String address) {
        return personRepository.findAllPersonAtAddress(address);
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

    public void attachFireStationToPersons(FireStation fireStation) {
        List<Person> persons = personRepository.findAllPersonAtAddress(fireStation.getAddress());

        persons.forEach(person -> personRepository.addFireStationToPerson(person, fireStation));
    }

    public Person attachMedicalRecordToPersons(MedicalRecord medicalRecord) {
        Person person = personRepository.findOneByFullName(medicalRecord.getFullName());

        return personRepository.attachMedicalRecordToPerson(person, medicalRecord);
    }
}