package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.*;

/**
 * Service class for operations related to Person entities.
 * Provides functionality for creating, updating, deleting, and retrieving Person data,
 * as well as methods for complex queries involving associated entities such as
 * fire stations and medical records.
 */
public interface PersonService {

    /**
     * Create a new person.
     *
     * @param person the new person to create.
     * @return the created {@code Person}.
     * @throws IllegalArgumentException if the person is already exists
     * @throws IllegalArgumentException if {@code person} is null or its firstName or lastName is null or blank
     */
    public Person create(Person person) throws IllegalArgumentException, InstanceAlreadyExistsException;

    /**
     * Update a fire station.
     *
     * @param person person to update.
     * @return the updated {@code Person}
     * @throws IllegalArgumentException if {@code person} is null or its firstName or lastName is null or blank
     */
    public Person update(Person person) throws InstanceNotFoundException;

    /**
     * Remove a person.
     *
     * @param firstName firstName of the person to remove
     * @param lastName lastName of the person to remove
     * @return {@code true} if FireStation removed successfully, {@code false} otherwise
     */
    public boolean remove(String firstName, String lastName);
    /**
     * Retrieves all persons from the repository.
     *
     * @return A List of all {@code Person} entities;
     *        Return an empty list if no Person found.
     */
    public List<Person> getAll();

    /**
     * Retrieves phone numbers associated with a list of persons' addresses.
     *
     * @param addresses a list of persons addresses.
     * @return a list oh phones found from addresses persons
     * @throws IllegalArgumentException if the addresses list is null or empty.
     */
    public List<String> getAllPhoneNumberFromAddresses(List<String> addresses);

    /**
     * Counts the number of adults from a list of full names.
     *
     * @param fullNames the list of full names from which the count of adults will be determined
     * @return the number of adults in the provided list of full names
     * @throws IllegalArgumentException if the fullNames list is null
     */
    public Integer countAdultFromPersons(List<String> fullNames);

    /**
     * Counts the number of children based on the provided list of full names.
     *
     * @param fullNames List of full names to be used for counting children.
     *                  It cannot be null; otherwise, an IllegalArgumentException will be thrown.
     * @return The total number of children associated with the provided full names.
     * @throws IllegalArgumentException if the fullNames list is null.
     */
    public int countChildrenFromPersons(List<String> fullNames);

    /**
     * Retrieves a list of child alerts for a given address. A child alert contains details
     * about children residing at the specified address, as well as other individuals
     * residing there.
     *
     * @param address the address to search for child alerts; must not be null or empty
     * @return a list of {@link ChildAlertDto} objects representing children and their household members
     * @throws IllegalArgumentException if the provided address is null or empty
     */
    public List<ChildAlertDto> getChildAlerts(String address);

    /**
     * Retrieves a list of all persons residing at the specified address.
     *
     * @param address the address to search for persons; must not be null, empty, or blank
     * @return a list of persons found at the given address
     * @throws IllegalArgumentException if the address is null, empty, or blank
     */
    public List<Person> getAllPersonAtAddress(String address);

    /**
     * Retrieves a list of persons that match the given last name.
     *
     * @param lastName the last name to search for; must not be null or empty
     * @return a list of {@code Person} objects with the specified last name;
     *         returns an empty list if no persons are found
     * @throws IllegalArgumentException if the provided last name is null or empty
     */
    public List<Person> getAllPersonByLastName(String lastName);

    /**
     * Retrieves a list of all persons associated with a given fire station number.
     *
     * @param stationNumber the number of the fire station to retrieve persons for; must not be null or empty.
     * @return a list of {@code Person} objects representing all persons linked to the specified fire station.
     *         Returns an empty list if no persons are found.
     * @throws IllegalArgumentException if the stationNumber is null or empty.
     */
    public List<Person> getAllPersonFromFireStation(String stationNumber);

    /**
     * Retrieves a list of Person objects associated with the given list of addresses.
     *
     * @param addresses a list of address strings used to fetch the associated Person objects.
     *                  This parameter must not be null, or an IllegalArgumentException will be thrown.
     * @return a list of Person objects found for the supplied addresses.
     * @throws IllegalArgumentException if the addresses parameter is null.
     */
    public List<Person> getAllPersonFromAddresses(List<String> addresses);

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
    public List<String> getFullNamesFromPersons(List<Person> persons);

    /**
     * Retrieves a list of all email addresses associated with people located in a specified city.
     *
     * @param city the name of the city for which the email addresses are to be retrieved;
     *             must not be null or empty
     * @return a list of email addresses of people in the specified city
     * @throws IllegalArgumentException if the provided city name is null or empty
     */
    public List<String> getAllEmailsAtCity(String city);

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
    public List<ChildAlertDto> attachOtherPersonToChildAlertDto(Map<String, ChildAlertDto> childAlerts, List<Person> persons);

    /**
     * Transforms a list of persons, a fire station, and a map of medical records
     * into a FireInfoDto object containing AddressPersonDto objects and fire station details.
     *
     * @param persons the list of Person objects to be transformed into AddressPersonDto objects
     * @param fireStation the FireStation object containing fire station details
     * @param medicalRecordMap a map associating person full names with their corresponding MedicalRecord objects
     * @return a FireInfoDto object containing a list of AddressPersonDto objects and fire station information
     */
    public FireInfoDto toFireInfoDto(List<Person> persons, FireStation fireStation, Map<String, MedicalRecord> medicalRecordMap);
}