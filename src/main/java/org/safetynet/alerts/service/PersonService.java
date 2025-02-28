package org.safetynet.alerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.fireStation.FireInfoDto;
import org.safetynet.alerts.dto.person.AddressPersonDto;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.dto.person.OtherPersonDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final FireStationService fireStationService;

    @Autowired
    private final MedicalRecordService medicalRecordService;

    @Autowired
    private final PersonDtoMapper personDtoMapper;

    public Person create(Person person) throws IllegalArgumentException, InstanceAlreadyExistsException {
        if (person == null || person.getFirstName().trim().isEmpty() || person.getLastName().trim().isEmpty()) {
            log.debug("Invalid person data");
            throw new IllegalArgumentException("Invalid person data");
        }

        Person savedPerson = personRepository.create(person);
        log.debug("Person created");

        return savedPerson;
    }

    public Person update(Person person) throws InstanceNotFoundException {
        if (person == null || person.getFirstName().trim().isEmpty() || person.getLastName().trim().isEmpty()) {
            log.debug("Invalid person data");
            throw new IllegalArgumentException("Invalid person data");
        }

        Person updatedPerson = personRepository.update(person);
        log.debug("Person updated");

        return updatedPerson;
    }

    public List<String> getAllPhoneNumberFromAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            log.debug("No addresses provided");
            throw new IllegalArgumentException("addresses cannot be empty");
        }

        List<String> phoneNumbers = personRepository.findPhoneNumbersFromAddresses(addresses);
        log.debug("{} phone numbers found", phoneNumbers.size());

        return phoneNumbers;
    }

    public Integer countAdultFromPersons(List<String> fullNames) {
        if (fullNames == null) {
            log.debug("fullNames adults cannot be null");

            throw new IllegalArgumentException("fullNames adults cannot be null");
        }

        int adultNbr = medicalRecordService.countAdultFromFullName(fullNames);
        log.debug("Count {} adults", adultNbr);

        return adultNbr;
    }

    public int countChildrenFromPersons(List<String> fullNames) {
        if (fullNames == null) {
            log.debug("fullNames children cannot be null");

            throw new IllegalArgumentException("fullNames cannot be null");
        }

        int childrenNbr = medicalRecordService.countChildrenFromFullName(fullNames);
        log.debug("Count {} children", childrenNbr);

        return childrenNbr;
    }

    public List<Person> getAllPersonAtAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            log.debug("Address cannot be empty");
            throw new IllegalArgumentException("address cannot be empty");
        }

        List<Person> persons = personRepository.findAllPersonAtAddress(address);
        log.debug("{} person(s) found at address", persons.size());

        return persons;
    }

    public List<Person> getAllPersonByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            log.debug("Last name cannot be null or empty");
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        List<Person> persons = Optional.ofNullable(personRepository.findAllPersonByLastName(lastName))
                .orElse(Collections.emptyList());

        log.debug("Found {} persons by lastname", persons.size());

        return persons;
    }

    public List<Person> getAllPersonFromFireStation(String stationNumber) {
        if (stationNumber == null || stationNumber.trim().isEmpty()) {
            log.debug("stationNumber cannot be null or empty");
            throw new IllegalArgumentException("stationNumber name cannot be null or empty");
        }

        List <String> addresses = Optional.ofNullable(fireStationService.getAddressesForOneFireStation(stationNumber))
                .orElse(Collections.emptyList());
        log.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

        List <Person> persons = Optional.ofNullable(personRepository.findAllPersonFromAddresses(addresses))
                .orElse(Collections.emptyList());
        log.debug("Found {} persons for station {}", persons.size(), stationNumber);

        return persons;
    }

    public List<Person> getAllPersonFromAddresses(List<String> addresses) {
        if (addresses == null) {
            log.debug("addresses cannot be null");
            throw new IllegalArgumentException("addresses cannot be null");
        }

        List<Person> persons = personRepository.findAllPersonFromAddresses(addresses);
        log.debug("Found {} persons from addresses", persons.size());

        return persons;
    }

    public List<Person> getAll() {
        List<Person> persons = personRepository.findAll();
        log.debug("Found {} persons", persons.size());

        return persons;
    }

    public boolean remove(String firstName, String lastName) {
        String fullName = String.format("%s %s", firstName, lastName);
        boolean removed = personRepository.remove(fullName);
        log.debug("Removed person {}", removed ? "successfully" : "failed");

        return removed;
    }

    public List<String> getFullNamesFromPersons(List<Person> persons) {
        if (persons == null) {
            log.debug("Null argument is invalid");

            throw new IllegalArgumentException("Null argument is invalid");
        }
        if (persons.isEmpty()) {
            log.debug("No persons found");

            return Collections.emptyList();
        }

        List<String> fullNames = persons.stream().map(Person::getFullName).toList();
        log.debug("Found {} full names", fullNames.size());

        return fullNames;
    }

    public List<String> getAllEmailsAtCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            log.debug("City name cannot be null or empty");

            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        List<String> fullNames = personRepository.findAllEmailsAtCity(city);
        log.debug("Found {} full names from city {}", fullNames.size(), city);

        return fullNames;
    }

    public List<ChildAlertDto> attachOtherPersonToChildAlertDto(String address) {
        if (address == null || address.trim().isEmpty()) {
            log.debug("Address cannot be null or empty");
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
        List<Person> persons = getAllPersonAtAddress(address);
        Map<String, ChildAlertDto> childAlerts = personDtoMapper.toChildAlertDto(persons, address, medicalRecordMap);

        if (childAlerts.isEmpty()) {
            log.debug("No children found at this address");

            return Collections.emptyList();
        }

        persons.forEach(person -> {
            int otherPersonCount = 0;

            for (Map.Entry<String, ChildAlertDto> entry : childAlerts.entrySet()) {
                if (!entry.getKey().equals(person.getFullName())) {
                    entry.getValue().otherPersons.add(new OtherPersonDto(person));
                    otherPersonCount++;
                }
            }
            log.debug("{} other person(s) household added for ChildAlertDto at address {}", otherPersonCount, address);
        });
        log.debug("ChildPersonDto mapped for {} children at address {}", childAlerts.size(), address);

        return new ArrayList<>(childAlerts.values());
    }

    public FireInfoDto tofireInfoDto(List<Person> persons, FireStation fireStation, Map<String, MedicalRecord> medicalRecordMap) {
        List<AddressPersonDto> personsDto = persons.stream()
                .map(person -> new AddressPersonDto(person, medicalRecordMap.get(person.getFullName())))
                .collect(Collectors.toList());

        return new FireInfoDto(personsDto, fireStation);
    }
}