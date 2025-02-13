package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class MedicalRecordRepository {
    protected final JsonData jsonData;

    public MedicalRecordRepository(JsonDataService jsonDataService) {
        this.jsonData = jsonDataService.getJsonData();
    }

    public MedicalRecord create(MedicalRecord medicalRecord) {
        if (findOneByFullName(medicalRecord.getFullName()).isPresent()) {
            throw new IllegalArgumentException("Person already exists");
        }

        jsonData.getMedicalrecords().add(medicalRecord);

        return medicalRecord;
    }

    public Optional<MedicalRecord> findOneByFullName(String fullName) {
        return jsonData
                .getMedicalrecords()
                .stream()
                .filter(medicalRecord -> medicalRecord.getFullName().equals(fullName))
                .findFirst();
    }

    public MedicalRecord update(MedicalRecord medicalRecord, MedicalRecord medicalRecordToUpdate) {
        medicalRecordToUpdate
                .setFirstName(medicalRecord.getFirstName())
                .setLastName(medicalRecord.getLastName())
                .setBirthdate(medicalRecord.getBirthdate())
                .setMedications(medicalRecord.getMedications())
                .setAllergies(medicalRecord.getAllergies());

        return medicalRecordToUpdate;
    }

    public boolean remove(MedicalRecord medicalRecord) {
        return jsonData.getMedicalrecords().remove(medicalRecord);
    }
}
