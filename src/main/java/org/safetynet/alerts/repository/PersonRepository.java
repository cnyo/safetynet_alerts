package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PersonRepository {

    public Person create(Person person) throws IllegalArgumentException, InstanceAlreadyExistsException {
        if (person == null || person.getFullName() == null) {
            log.debug("Invalid person data");
            throw new IllegalArgumentException("Invalid person data");
        }

        if (findOneByFullName(person.getFullName()).isPresent()) {
            log.debug("Person already exists");
            throw new InstanceAlreadyExistsException("Person already exists");
        }

        JsonDataService.getJsonData().getPersons().add(person);

        return person;
    }

    public Person update(Person person) throws InstanceNotFoundException {
        Optional<Person> personToUpdate = JsonDataService.getJsonData().getPersons().stream()
                .filter(curentPerson -> curentPerson.equals(person)).findFirst();

        if (personToUpdate.isEmpty()) {
            log.debug("Person not found");
            throw new InstanceNotFoundException("Person not found to update not found");
        }

        return personToUpdate.get()
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
        boolean medicalRecordRemoved = JsonDataService.getJsonData().getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        if (!medicalRecordRemoved) {
            return false;
        }

        return JsonDataService.getJsonData().getPersons()
                .removeIf(person -> person.getFullName().equals(fullName));
    }

    public List<Person> findAllPersonAtAddress(String address) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public List<Person> findAllPersonByLastName(String lastName) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    public Optional<Person> findOneByFullName(String fullName) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getFullName().equals(fullName))
                .findFirst();
    }

    public List<Person> findAllPersonFromAddresses(List<String> addresses) {
        return JsonDataService.getJsonData().getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    public List<Person> findAll() {
        return JsonDataService.getJsonData().getPersons();
    }

    public List<String> findPhoneNumbersFromAddresses(List<String> addresses) {
        return JsonDataService.getJsonData().getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .map(Person::getPhone).toList();
    }

    public List<String> findAllEmailsAtCity(String city) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getCity().equals(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());
    }
}
