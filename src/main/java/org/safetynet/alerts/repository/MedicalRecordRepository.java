package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Repository for managing MedicalRecord entities. Provides methods to create, update, delete,
 * and search medical records. This repository operates primarily on in-memory data managed
 * by the JsonDataService.
 *
 * The operations include creating new records, updating existing records, removing records,
 * finding records by different criteria, and counting adult or child records based on
 * individuals' full names.
 *
 * It interacts with PersonRepository to ensure the existence of associated people
 * when manipulating medical records.
 */
@Component
@Slf4j
public class MedicalRecordRepository {
    private final PersonRepository personRepository;

    public MedicalRecordRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Creates a new medical record for a given person.
     *
     * @param medicalRecord the medical record to be created, containing the full name of the person and medical details
     * @return the created medical record
     * @throws InstanceAlreadyExistsException if a medical record for the specified person already exists
     * @throws NoSuchElementException if the person associated with the medical record does not exist
     */
    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException, NoSuchElementException {
        Optional<Person> person = personRepository.findOneByFullName(medicalRecord.getFullName());
        if (person.isEmpty()) {
            log.debug("Person for new medical record not exists.");
            throw new NoSuchElementException("Person for new medical record not exists");
        }

        Optional<MedicalRecord> existingMedicalRecord = findOneByFullName(medicalRecord.getFullName());
        if (existingMedicalRecord.isPresent()) {
            log.debug("Medical record already exists.");
            throw new InstanceAlreadyExistsException("Medical record already exists");
        }

        JsonDataService.getJsonData().getMedicalrecords().add(medicalRecord);

        return medicalRecord;
    }

    /**
     * Updates an existing medical record with new data provided.
     *
     * @param medicalRecord the updated medical record containing the new data to be persisted
     * @return the updated medical record
     * @throws NoSuchElementException if the medical record to update cannot be found
     */
    public MedicalRecord update(MedicalRecord medicalRecord) {
        MedicalRecord medicalRecordToUpdate = findOneByFullName(medicalRecord.getFullName())
                .orElseThrow(() -> new NoSuchElementException("Medical record not found"));

        medicalRecordToUpdate
                .setFirstName(medicalRecord.getFirstName())
                .setLastName(medicalRecord.getLastName())
                .setBirthdate(medicalRecord.getBirthdate())
                .setMedications(medicalRecord.getMedications())
                .setAllergies(medicalRecord.getAllergies());

        return medicalRecordToUpdate;
    }

    /**
     * Removes a medical record associated with the given first name and last name.
     *
     * @param firstName the first name of the person whose medical record is to be removed
     * @param lastName the last name of the person whose medical record is to be removed
     * @return true if a medical record matching the specified full name was found and removed, false otherwise
     */
    public boolean remove(String firstName, String lastName) {
        String fullName = firstName + " " + lastName;
        boolean removed = JsonDataService.getJsonData().getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        return removed;
    }

    /**
     * Finds a MedicalRecord by the full name of the person.
     *
     * @param fullName the full name of the person whose medical record is to be found
     * @return an Optional containing the MedicalRecord if found, or an empty Optional if no record exists for the given full name
     */
    public Optional<MedicalRecord> findOneByFullName(String fullName) {
        return JsonDataService.getJsonData()
                .getMedicalrecords()
                .stream()
                .filter(medicalRecord -> medicalRecord.getFullName().equals(fullName))
                .findFirst();
    }

    /**
     * Retrieves a list of all medical records.
     *
     * @return a list of {@code MedicalRecord} objects representing all medical records,
     *         or an empty list if no medical records are available.
     */
    public List<MedicalRecord> findAll() {
        return JsonDataService.getJsonData().getMedicalrecords();
    }

    /**
     * Counts the number of adults from the provided list of full names.
     *
     * @param fullNames the list of full names to check against the medical records.
     * @return the number of individuals who are adults and whose full names match the provided list.
     */
    public int countAdultFromFullName(List<String> fullNames) {
        return JsonDataService.getJsonData().getMedicalrecords().stream()
                .filter(medicalRecord -> fullNames.contains(medicalRecord.getFullName()) && medicalRecord.isAdult())
                .toList().size();
    }

    /**
     * Counts the number of children based on the provided list of full names.
     * A child is identified using the `isChild` method in the medical records.
     *
     * @param fullNames a list of full names to match against the medical records.
     * @return the count of children whose full names match the provided list.
     */
    public int countChildrenFromFullName(List<String> fullNames) {
        return JsonDataService.getJsonData().getMedicalrecords().stream()
                .filter(medicalRecord -> fullNames.contains(medicalRecord.getFullName()) && medicalRecord.isChild())
                .toList().size();
    }

    /**
     * Retrieves a map of all medical records indexed by the full name of the associated individual.
     *
     * @return a map where the keys are full names (as Strings) and the values are MedicalRecord objects.
     */
    public Map<String, MedicalRecord> getAllByFullName() {
        return JsonDataService.getJsonData().getMedicalrecords()
                .stream()
                .collect(Collectors.toMap(
                        MedicalRecord::getFullName,
                        medicalRecord -> medicalRecord
                ));
    }
}
