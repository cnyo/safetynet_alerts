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

/**
 * Repository class responsible for managing operations related to Person objects.
 * Provides functionality to create, update, delete, and query Person data.
 * The data is stored and managed via the JsonDataService, which loads and holds data in memory
 * from a JSON source file at application startup.
 */
@Component
@Slf4j
public class PersonRepository {

    /**
     * Creates a new Person entry and adds it to the existing dataset.
     * If a Person with the same full name already exists, an exception is thrown.
     *
     * @param person the Person object to add
     * @return the created Person object
     * @throws IllegalArgumentException if the provided Person object is invalid
     * @throws InstanceAlreadyExistsException if a Person with the same full name already exists
     */
    public Person create(Person person) throws IllegalArgumentException, InstanceAlreadyExistsException {

        if (findOneByFullName(person.getFullName()).isPresent()) {
            log.debug("Person already exists");
            throw new InstanceAlreadyExistsException("Person already exists");
        }

        JsonDataService.getJsonData().getPersons().add(person);

        return person;
    }

    /**
     * Updates the details of an existing person in the dataset.
     * This method identifies the person to update by matching the given person object with an
     * existing person in the dataset. If a match is not found, an {@code InstanceNotFoundException}
     * is thrown. If a match is found, the person's details are updated with the new values provided
     * in the input object and the updated person is returned.
     *
     * @param person the {@code Person} object containing the updated details of the person to find and update
     * @return the updated {@code Person} object reflecting the modified values
     * @throws InstanceNotFoundException if the person to update does not exist in the dataset
     */
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

    /**
     * Removes a person and their corresponding medical record from the data repository
     * based on the provided full name.
     *
     * @param fullName the full name of the individual to remove (e.g., "John Doe")
     * @return {@code true} if the person and their medical record were successfully removed,
     *         {@code false} otherwise
     */
    public boolean remove(String fullName) {
        JsonDataService.getJsonData().getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        return JsonDataService.getJsonData().getPersons()
                .removeIf(person -> person.getFullName().equals(fullName));
    }

    /**
     * Finds all persons associated with the specified address.
     *
     * @param address the address for which to find associated persons
     * @return a list of {@code Person} objects representing all persons
     *         residing at the given address, or an empty list if no
     *         persons are found
     */
    public List<Person> findAllPersonAtAddress(String address) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all persons with the given last name.
     *
     * @param lastName the last name of persons to filter and retrieve
     * @return a list of {@code Person} objects whose last name matches the given parameter
     */
    public List<Person> findAllPersonByLastName(String lastName) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    /**
     * Finds a single {@code Person} object that matches the given full name.
     * Searches the list of persons for the first occurrence where the full name matches
     * the provided value.
     *
     * @param fullName the full name of the person to search for
     * @return an {@code Optional} containing the matching {@code Person} if found,
     *         or an empty {@code Optional} if no person matches the given full name
     */
    public Optional<Person> findOneByFullName(String fullName) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getFullName().equals(fullName))
                .findFirst();
    }

    /**
     * Retrieves a list of persons whose addresses match any of the addresses provided in the input list.
     * This method filters the available persons based on their address and returns a list of matching persons.
     *
     * @param addresses a list of address strings to filter persons by
     * @return a list of {@code Person} objects corresponding to the given addresses
     */
    public List<Person> findAllPersonFromAddresses(List<String> addresses) {
        return JsonDataService.getJsonData().getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all persons available in the data source.
     *
     * @return a list of {@code Person} objects representing all persons, or an empty list if no persons are available.
     */
    public List<Person> findAll() {
        return JsonDataService.getJsonData().getPersons();
    }

    /**
     * Finds and returns a list of phone numbers associated with the provided list of addresses.
     * The method filters the persons data obtained from the JsonDataService and matches their addresses
     * with the provided addresses to retrieve phone numbers.
     *
     * @param addresses a list of addresses to search for corresponding phone numbers
     * @return a list of phone numbers belonging to persons whose addresses match the provided list
     */
    public List<String> findPhoneNumbersFromAddresses(List<String> addresses) {
        return JsonDataService.getJsonData().getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .map(Person::getPhone).toList();
    }

    /**
     * Retrieves a list of email addresses for all persons who reside in the specified city.
     *
     * @param city the name of the city for which to find email addresses
     * @return a list of email addresses of persons living in the specified city
     */
    public List<String> findAllEmailsAtCity(String city) {
        return JsonDataService.getJsonData()
                .getPersons()
                .stream()
                .filter(person -> person.getCity().equals(city))
                .map(Person::getEmail)
                .collect(Collectors.toList());
    }
}
