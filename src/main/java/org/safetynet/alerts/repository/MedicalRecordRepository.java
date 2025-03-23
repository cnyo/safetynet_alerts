package org.safetynet.alerts.repository;

import org.safetynet.alerts.model.MedicalRecord;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;


/**
 * Repository for managing MedicalRecord entities. Provides methods to create, update, delete,
 * and search medical records. This repository operates primarily on in-memory data managed
 * by the JsonDataService.
 * The operations include creating new records, updating existing records, removing records,
 * finding records by different criteria, and counting adult or child records based on
 * individuals' full names.
 * It interacts with PersonRepository to ensure the existence of associated people
 * when manipulating medical records.
 */
public interface MedicalRecordRepository {
//    private final PersonRepository personRepository;

//    public MedicalRecordRepository(PersonRepository personRepository) {
//        this.personRepository = personRepository;
//    }

    /**
     * Creates a new medical record for a given person.
     *
     * @param medicalRecord the medical record to be created, containing the full name of the person and medical details
     * @return the created medical record
     * @throws InstanceAlreadyExistsException if a medical record for the specified person already exists
     * @throws NoSuchElementException if the person associated with the medical record does not exist
     */
    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException, NoSuchElementException;

    /**
     * Updates an existing medical record with new data provided.
     *
     * @param medicalRecord the updated medical record containing the new data to be persisted
     * @return the updated medical record
     */
    public MedicalRecord update(MedicalRecord medicalRecord);

    /**
     * Removes a medical record associated with the given first name and last name.
     *
     * @param firstName the first name of the person whose medical record is to be removed
     * @param lastName the last name of the person whose medical record is to be removed
     * @return true if a medical record matching the specified full name was found and removed, false otherwise
     */
    public boolean remove(String firstName, String lastName);

    /**
     * Finds a MedicalRecord by the full name of the person.
     *
     * @param fullName the full name of the person whose medical record is to be found
     * @return an Optional containing the MedicalRecord if found, or an empty Optional if no record exists for the given full name
     */
    public Optional<MedicalRecord> findOneByFullName(String fullName);

    /**
     * Retrieves a list of all medical records.
     *
     * @return a list of {@code MedicalRecord} objects representing all medical records,
     *         or an empty list if no medical records are available.
     */
    public List<MedicalRecord> findAll();

    /**
     * Counts the number of adults from the provided list of full names.
     *
     * @param fullNames the list of full names to check against the medical records.
     * @return the number of individuals who are adults and whose full names match the provided list.
     */
    public int countAdultFromFullName(List<String> fullNames);

    /**
     * Counts the number of children based on the provided list of full names.
     * A child is identified using the `isChild` method in the medical records.
     *
     * @param fullNames a list of full names to match against the medical records.
     * @return the count of children whose full names match the provided list.
     */
    public int countChildrenFromFullName(List<String> fullNames);
    /**
     * Retrieves a map of all medical records indexed by the full name of the associated individual.
     *
     * @return a map where the keys are full names (as Strings) and the values are MedicalRecord objects.
     */
    public Map<String, MedicalRecord> getAllByFullName();
}
