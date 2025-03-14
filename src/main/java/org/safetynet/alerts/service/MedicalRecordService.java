package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Slf4j
@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * Create a medical record.
     *
     * @param medicalRecord The new medical record to create
     * @return the created {@code medicalRecord}
     * @throws InstanceAlreadyExistsException if the medical record is already exists
     */
    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException, NoSuchElementException, DateTimeException {
        if (!validateBirthdate(medicalRecord.getBirthdate())) {
            log.error("Invalid birthdate {} for create MedicalRecord", medicalRecord.getBirthdate());
            throw new DateTimeException("Invalid birthdate: future date provided");
        }

        MedicalRecord savedMedicalRecord = medicalRecordRepository.create(medicalRecord);
        log.debug("MedicalRecord created successfully");

        return savedMedicalRecord;
    }

    /**
     * Update a medical record.
     *
     * @param medicalRecord to update.
     * @return the updated {@code medicalRecord}
     * @throws NoSuchElementException if the medical record to update is not found.
     * @throws DateTimeException If the birthdate of the medical record is in the future.
     */
    public MedicalRecord update(MedicalRecord medicalRecord) throws NoSuchElementException, DateTimeException {
        if (!validateBirthdate(medicalRecord.getBirthdate())) {
            log.error("Invalid birthdate {} for update MedicalRecord", medicalRecord.getBirthdate());
            throw new DateTimeException("Invalid birthdate: future date provided");
        }

        MedicalRecord updatedMedicalRecord = medicalRecordRepository.update(medicalRecord);
        log.debug("MedicalRecord updated successfully");

        return updatedMedicalRecord;
    }

    /**
     * Remove a medical record.
     *
     * @param firstName the {@code firstName} of the medical record to remove
     * @param lastName the {@code lastName} of the medical record to remove
     * @return {@code true} if medical record removed successfully, {@code false} otherwise
     */
    public boolean remove(String firstName, String lastName) {
        boolean removed = medicalRecordRepository.remove(firstName, lastName);
        log.debug("MedicalRecord removed: {}", removed ? "success" : "failure");

        return removed;
    }

    /**
     * Retrieves all medical records from the repository.
     *
     * @return A List of all {@code MedicalRecord} entities;
     *        Return an empty list if no MedicalRecord found.
     */
    public List<MedicalRecord> getAll() {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();
        log.debug("getAll medical records: {}", medicalRecords.size());

        return medicalRecords;
    }

    /**
     * Retrieves all medical records mapped by fullName.
     *
     * @return A map of medical record mapped by fullname.
     */
    public Map<String, MedicalRecord> getAllByFullName() {
        Map<String, MedicalRecord> medicalRecords = medicalRecordRepository.getAllByFullName();
        log.debug("Medical records ordered by fullName found: {}", medicalRecords.size());

        return medicalRecords;
    }

    /**
     * Count the number of adult with has {@code fullNames}
     * @param fullNames a List of {@code fullName}
     * @return The number of adult with the {@code fullNames}.
     */
    public int countAdultFromFullName(List<String> fullNames) {
        if (fullNames == null || fullNames.isEmpty()) {
            log.debug("No fullNames provided for count adults, returning 0.");
            return 0;
        }

        int adultNbr = medicalRecordRepository.countAdultFromFullName(fullNames);
        log.debug("Count {} adult from fullNames", adultNbr);

        return adultNbr;
    }

    /**
     * Count the number of children with has {@code fullNames}
     * @param fullNames a List of {@code fullName}
     * @return The number of children with the {@code fullNames}.
     */
    public int countChildrenFromFullName(List<String> fullNames) {
        if (fullNames == null || fullNames.isEmpty()) {
            log.debug("No fullNames provided for count children, returning 0.");
            return 0;
        }

        int childrenNbr = medicalRecordRepository.countChildrenFromFullName(fullNames);
        log.debug("Count {} children from fullNames", childrenNbr);

        return childrenNbr;
    }

    /**
     * Retrieves the FireStation by its address and station.
     *
     * @param address The address of the FireStation
     * @param station The FiresStation number
     * @return The {@code stations} found, or {@code null} if no match is found.
     */
    /**
     * Retrieves the medical record by its firstName and lastName.
     *
     * @param firstName the firstName of the medical record
     * @param lastName the lastName of the medical record
     * @return The {@code MedicalRecord} found, or {@code null} if no match is found.
     */
    public MedicalRecord getOneByName(String firstName, String lastName) {
        MedicalRecord medicalRecord = medicalRecordRepository.findOneByFullName(String.format("%s %s", firstName, lastName)).orElse(null);
        log.debug("MedicalRecord found by one name: {}", medicalRecord != null);

        return medicalRecord;
    }

    /**
     * Check if the birthdate is in the future.
     *
     * @param birthdate The birthdate to check
     * @return return true if is valid, false otherwise
     */
    public boolean validateBirthdate(LocalDate birthdate) {
        return !birthdate.isAfter(LocalDate.now());
    }
}
