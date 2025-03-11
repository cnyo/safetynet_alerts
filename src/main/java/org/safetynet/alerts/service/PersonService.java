package org.safetynet.alerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.*;

/**
 * Service class for operations related to Person entities.
 * Provides functionality for creating, updating, deleting, and retrieving Person data,
 * as well as methods for complex queries involving associated entities such as
 * fire stations and medical records.
 */
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

    /**
     * Create a new person.
     *
     * @param person the new person to create.
     * @return the created {@code Person}.
     * @throws IllegalArgumentException if the person is already exists
     * @throws IllegalArgumentException if {@code person} is null or its firstName or lastName is null or blank
     */
    public Person create(Person person) throws IllegalArgumentException, InstanceAlreadyExistsException {
        if (Objects.isNull(person) || Strings.isBlank(person.getFirstName()) || Strings.isBlank(person.getLastName())) {
            log.debug("Invalid person data");
            throw new IllegalArgumentException("Invalid person data");
        }

        Person savedPerson = personRepository.create(person);
        log.debug("Person created");

        return savedPerson;
    }

    /**
     * Update a fire station.
     *
     * @param person person to update.
     * @return the updated {@code Person}
     * @throws IllegalArgumentException if {@code person} is null or its firstName or lastName is null or blank
     */
    public Person update(Person person) throws InstanceNotFoundException {
        if (Objects.isNull(person) || Strings.isBlank(person.getFirstName()) || Strings.isBlank(person.getLastName())) {
            log.debug("Invalid person data");
            throw new IllegalArgumentException("Invalid person data");
        }

        Person updatedPerson = personRepository.update(person);
        log.debug("Person updated");

        return updatedPerson;
    }

    /**
     * Remove a person.
     *
     * @param firstName firstName of the person to remove
     * @param lastName lastName of the person to remove
     * @return {@code true} if FireStation removed successfully, {@code false} otherwise
     */
    public boolean remove(String firstName, String lastName) {
        String fullName = String.format("%s %s", firstName, lastName);
        boolean removed = personRepository.remove(fullName);
        log.debug("Removed person {}", removed ? "successfully" : "failed");

        return removed;
    }

    /**
     * Retrieves all persons from the repository.
     *
     * @return A List of all {@code Person} entities;
     *        Return an empty list if no Person found.
     */
    public List<Person> getAll() {
        List<Person> persons = personRepository.findAll();
        log.debug("Found {} persons", persons.size());

        return persons;
    }

    /**
     * Retrieves phone numbers associated with a list of persons' addresses.
     *
     * @param addresses a list of persons addresses.
     * @return a list oh phones found from addresses persons
     * @throws IllegalArgumentException if the addresses list is null or empty.
     */
    public List<String> getAllPhoneNumberFromAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            log.debug("No addresses provided");
            throw new IllegalArgumentException("addresses cannot be empty");
        }

        List<String> phoneNumbers = personRepository.findPhoneNumbersFromAddresses(addresses);
        log.debug("{} phone numbers found", phoneNumbers.size());

        return phoneNumbers;
    }

    /**
     * Counts the number of adults from a list of full names.
     *
     * @param fullNames the list of full names from which the count of adults will be determined
     * @return the number of adults in the provided list of full names
     * @throws IllegalArgumentException if the fullNames list is null
     */
    public Integer countAdultFromPersons(List<String> fullNames) {
        if (fullNames == null) {
            log.debug("fullNames adults cannot be null");

            throw new IllegalArgumentException("fullNames adults cannot be null");
        }

        int adultNbr = medicalRecordService.countAdultFromFullName(fullNames);
        log.debug("Count {} adults", adultNbr);

        return adultNbr;
    }

    /**
     * Counts the number of children based on the provided list of full names.
     *
     * @param fullNames List of full names to be used for counting children.
     *                  It cannot be null; otherwise, an IllegalArgumentException will be thrown.
     * @return The total number of children associated with the provided full names.
     * @throws IllegalArgumentException if the fullNames list is null.
     */
    public int countChildrenFromPersons(List<String> fullNames) {
        if (fullNames == null) {
            log.debug("fullNames children cannot be null");

            throw new IllegalArgumentException("fullNames cannot be null");
        }

        int childrenNbr = medicalRecordService.countChildrenFromFullName(fullNames);
        log.debug("Count {} children", childrenNbr);

        return childrenNbr;
    }

    /**
     * Retrieves a list of child alerts for a given address. A child alert contains details
     * about children residing at the specified address, as well as other individuals
     * residing there.
     *
     * @param address the address to search for child alerts; must not be null or empty
     * @return a list of {@link ChildAlertDto} objects representing children and their household members
     * @throws IllegalArgumentException if the provided address is null or empty
     */
    public List<ChildAlertDto> getChildAlerts(String address) {
        if (Strings.isBlank(address)) {
            log.debug("Address cannot be null or empty");
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
        List<Person> persons = getAllPersonAtAddress(address);
        Map<String, ChildAlertDto> childAlerts = personDtoMapper.toChildAlertDto(persons, medicalRecordMap);

        return attachOtherPersonToChildAlertDto(childAlerts, persons);
    }

    /**
     * Retrieves a list of all persons residing at the specified address.
     *
     * @param address the address to search for persons; must not be null, empty, or blank
     * @return a list of persons found at the given address
     * @throws IllegalArgumentException if the address is null, empty, or blank
     */
    public List<Person> getAllPersonAtAddress(String address) {
        if (Strings.isBlank(address)) {
            log.debug("Address cannot be empty");
            throw new IllegalArgumentException("address cannot be empty");
        }

        List<Person> persons = personRepository.findAllPersonAtAddress(address);
        log.debug("{} person(s) found at address", persons.size());

        return persons;
    }

    /**
     * Retrieves a list of persons that match the given last name.
     *
     * @param lastName the last name to search for; must not be null or empty
     * @return a list of {@code Person} objects with the specified last name;
     *         returns an empty list if no persons are found
     * @throws IllegalArgumentException if the provided last name is null or empty
     */
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

    /**
     * Retrieves a list of all persons associated with a given fire station number.
     *
     * @param stationNumber the number of the fire station to retrieve persons for; must not be null or empty.
     * @return a list of {@code Person} objects representing all persons linked to the specified fire station.
     *         Returns an empty list if no persons are found.
     * @throws IllegalArgumentException if the stationNumber is null or empty.
     */
    public List<Person> getAllPersonFromFireStation(String stationNumber) {
        if (stationNumber == null || stationNumber.trim().isEmpty()) {
            log.debug("stationNumber cannot be null or empty");
            throw new IllegalArgumentException("stationNumber name cannot be null or empty");
        }

        List <String> addresses = Optional.ofNullable(fireStationService.getAddressesForFireStation(stationNumber))
                .orElse(Collections.emptyList());
        log.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

        List <Person> persons = Optional.ofNullable(personRepository.findAllPersonFromAddresses(addresses))
                .orElse(Collections.emptyList());
        log.debug("Found {} persons for station {}", persons.size(), stationNumber);

        return persons;
    }

    /**
     * Retrieves a list of Person objects associated with the given list of addresses.
     *
     * @param addresses a list of address strings used to fetch the associated Person objects.
     *                  This parameter must not be null, or an IllegalArgumentException will be thrown.
     * @return a list of Person objects found for the supplied addresses.
     * @throws IllegalArgumentException if the addresses parameter is null.
     */
    public List<Person> getAllPersonFromAddresses(List<String> addresses) {
        if (addresses == null) {
            log.debug("addresses cannot be null");
            throw new IllegalArgumentException("addresses cannot be null");
        }

        List<Person> persons = personRepository.findAllPersonFromAddresses(addresses);
        log.debug("Found {} persons from addresses", persons.size());

        return persons;
    }

    /**
     * Extracts full names from a list of Person objects.
     *
     * @param persons the list of Person objects from which full names will be extracted.
     *                Cannot be null. An exception will be thrown if this parameter is null.
     *                If the list is empty, an empty list will be returned.
     * @return a list containing the full names of the provided Person objects.
     *         If the input list is empty, an empty list is returned.
     * @throws IllegalArgumentException if the provided persons list is null.
     */
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

    /**
     * Retrieves a list of all email addresses associated with people located in a specified city.
     *
     * @param city the name of the city for which the email addresses are to be retrieved;
     *             must not be null or empty
     * @return a list of email addresses of people in the specified city
     * @throws IllegalArgumentException if the provided city name is null or empty
     */
    public List<String> getAllEmailsAtCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            log.debug("City name cannot be null or empty");

            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        List<String> fullNames = personRepository.findAllEmailsAtCity(city);
        log.debug("Found {} full names from city {}", fullNames.size(), city);

        return fullNames;
    }

    /**
     * Attaches a list of other persons to each ChildAlertDto in the specified child alerts map.
     * For each person in the list, this method evaluates all entries in the child alerts map
     * and adds a representation of the person as an other person to the respective ChildAlertDto,
     * provided the person's full name does not match the key of the current entry.
     *
     * @param childAlerts a map where the key is a string representing a child's identity
     *                    (e.g., full name or unique identifier) and the value is the
     *                    corresponding ChildAlertDto containing child-related information
     * @param persons     a list of Person objects to be processed and attached as other persons
     *                    to the ChildAlertDto entries in the childAlerts map
     * @return a list of updated ChildAlertDto objects containing the added other person information
     */
    public List<ChildAlertDto> attachOtherPersonToChildAlertDto(Map<String, ChildAlertDto> childAlerts, List<Person> persons) {
        persons.forEach(person -> {
            int otherPersonCount = 0;

            for (Map.Entry<String, ChildAlertDto> entry : childAlerts.entrySet()) {
                if (!entry.getKey().equals(person.getFullName())) {
                    entry.getValue().otherPersons.add(new OtherPersonDto(person.getFirstName(), person.getLastName()));
                    otherPersonCount++;
                }
            }
            log.debug("{} other person(s) household added for ChildAlertDto", otherPersonCount);
        });
        log.debug("ChildPersonDto mapped for {} children at address", childAlerts.size());

        return new ArrayList<>(childAlerts.values());
    }

    /**
     * Transforms a list of persons, a fire station, and a map of medical records
     * into a FireInfoDto object containing AddressPersonDto objects and fire station details.
     *
     * @param persons the list of Person objects to be transformed into AddressPersonDto objects
     * @param fireStation the FireStation object containing fire station details
     * @param medicalRecordMap a map associating person full names with their corresponding MedicalRecord objects
     * @return a FireInfoDto object containing a list of AddressPersonDto objects and fire station information
     */
    public FireInfoDto toFireInfoDto(List<Person> persons, FireStation fireStation, Map<String, MedicalRecord> medicalRecordMap) {
        List<AddressPersonDto> addressPersons = new ArrayList<>();

        for (Person person : persons) {
            AddressPersonDto addressPersonDto = new AddressPersonDto(person, medicalRecordMap.get(person.getFullName()));
            addressPersons.add(addressPersonDto);
            log.debug("person transformed to AddressPersonDto");
        }
        log.debug("{} person(s) transformed to AddressPersonDto", addressPersons.size());

        return new FireInfoDto(addressPersons, fireStation);
    }
}