package org.safetynet.alerts.service;

import org.safetynet.alerts.model.MedicalRecord;

import javax.management.InstanceAlreadyExistsException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Medical records management service.
 * This service allows you to retrieve, add, update and delete medical records
 */
public interface MedicalRecordService {

    /**
     * Create a medical record.
     *
     * @param medicalRecord The new medical record to create
     * @return the created {@code medicalRecord}
     * @throws InstanceAlreadyExistsException if the medical record is already exists
     */
    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException, NoSuchElementException, DateTimeException;

    /**
     * Update a medical record.
     *
     * @param medicalRecord to update.
     * @return the updated {@code medicalRecord}
     * @throws NoSuchElementException if the medical record to update is not found.
     * @throws DateTimeException If the birthdate of the medical record is in the future.
     */
    public MedicalRecord update(MedicalRecord medicalRecord) throws NoSuchElementException, DateTimeException;

    /**
     * Remove a medical record.
     *
     * @param firstName the {@code firstName} of the medical record to remove
     * @param lastName the {@code lastName} of the medical record to remove
     * @return {@code true} if medical record removed successfully, {@code false} otherwise
     */
    public boolean remove(String firstName, String lastName);

    /**
     * Retrieves all medical records from the repository.
     *
     * @return A List of all {@code MedicalRecord} entities;
     *        Return an empty list if no MedicalRecord found.
     */
    public List<MedicalRecord> getAll();

    /**
     * Retrieves all medical records mapped by fullName.
     *
     * @return A map of medical record mapped by fullname.
     */
    public Map<String, MedicalRecord> getAllByFullName();

    /**
     * Count the number of adult with has {@code fullNames}
     * @param fullNames a List of {@code fullName}
     * @return The number of adult with the {@code fullNames}.
     */
    public int countAdultFromFullName(List<String> fullNames);

    /**
     * Count the number of children with has {@code fullNames}
     * @param fullNames a List of {@code fullName}
     * @return The number of children with the {@code fullNames}.
     */
    public int countChildrenFromFullName(List<String> fullNames);

    /**
     * Retrieves the medical record by its firstName and lastName.
     *
     * @param firstName the firstName of the medical record
     * @param lastName the lastName of the medical record
     * @return The {@code MedicalRecord} found, or {@code null} if no match is found.
     */
    public MedicalRecord getOneByName(String firstName, String lastName);

    /**
     * Check if the birthdate is in the future.
     *
     * @param birthdate The birthdate to check
     * @return return true if is valid, false otherwise
     */
    public boolean validateBirthdate(LocalDate birthdate);
}
