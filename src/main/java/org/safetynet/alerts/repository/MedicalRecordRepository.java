package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MedicalRecordRepository {
    private final PersonRepository personRepository;

    public MedicalRecordRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

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

    public Optional<MedicalRecord> findOneByFullName(String fullName) {
        return JsonDataService.getJsonData()
                .getMedicalrecords()
                .stream()
                .filter(medicalRecord -> medicalRecord.getFullName().equals(fullName))
                .findFirst();
    }

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

    public boolean remove(String firstName, String lastName) {
        String fullName = firstName + " " + lastName;
        boolean removed = JsonDataService.getJsonData().getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        return removed;
    }

    public List<MedicalRecord> findAll() {
        return JsonDataService.getJsonData().getMedicalrecords();
    }

    public int countAdultFromFullName(List<String> fullNames) {
        return JsonDataService.getJsonData().getMedicalrecords().stream()
                .filter(medicalRecord -> fullNames.contains(medicalRecord.getFullName()) && medicalRecord.isAdult())
                .toList().size();
    }

    public int countChildrenFromFullName(List<String> fullNames) {
        return JsonDataService.getJsonData().getMedicalrecords().stream()
                .filter(medicalRecord -> fullNames.contains(medicalRecord.getFullName()) && medicalRecord.isChild())
                .toList().size();
    }

    public Map<String, MedicalRecord> getAllByFullName() {
        return JsonDataService.getJsonData().getMedicalrecords()
                .stream()
                .collect(Collectors.toMap(
                        MedicalRecord::getFullName,
                        medicalRecord -> medicalRecord
                ));
    }
}
