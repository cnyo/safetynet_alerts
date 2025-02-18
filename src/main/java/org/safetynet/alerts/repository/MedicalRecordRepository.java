package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
public class MedicalRecordRepository {
    protected final JsonData jsonData;
    private final PersonRepository personRepository;

    public MedicalRecordRepository(JsonDataService jsonDataService, PersonRepository personRepository) {
        this.jsonData = jsonDataService.getJsonData();
        this.personRepository = personRepository;
    }

    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException {
        Person person = personRepository.findOneByFullName(medicalRecord.getFullName());

        if (person.getMedicalRecord() != null) {
            log.info("Medical record already exists");
            throw new InstanceAlreadyExistsException("Medical record already exists");
        }

        jsonData.getMedicalrecords().add(medicalRecord);
        person.setMedicalRecord(medicalRecord);
        log.info("Medical record added to person.");

        return medicalRecord;
    }

    public Optional<MedicalRecord> findOneByFullName(String fullName) {
        return jsonData
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
        boolean removed = jsonData.getMedicalrecords()
                .removeIf(medicalRecord -> medicalRecord.getFullName().equals(fullName));

        if (removed) {
            try {
                personRepository.findOneByFullName(fullName).setMedicalRecord(null);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("Person not found.");
            }
        }

        return removed;
    }

    public List<MedicalRecord> findAll() {
        return jsonData.getMedicalrecords();
    }
}
