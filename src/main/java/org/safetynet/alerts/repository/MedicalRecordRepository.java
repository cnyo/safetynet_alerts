package org.safetynet.alerts.repository;

import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.JsonDataLoader;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MedicalRecordRepository {
    protected final JsonData jsonData;

    public MedicalRecordRepository(JsonDataLoader jsonDataLoader) {
        this.jsonData = jsonDataLoader.getJsonData();
    }

    public Optional<MedicalRecord> findOne(String fullName) {
        return Optional.of(jsonData.getMedicalRecords().stream().filter(mr -> mr.getFullName().equals(fullName)).findFirst().get());
    }

    public MedicalRecord create(MedicalRecord medicalRecord) {
        jsonData.getMedicalRecords().add(medicalRecord);

        return medicalRecord;
    }

    public MedicalRecord findOneByFullName(String fullName) {
        return jsonData
                .getMedicalRecords()
                .stream()
                .filter(medicalRecord -> medicalRecord.getFullName().equals(fullName))
                .findFirst().get();
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

    public void remove(MedicalRecord medicalRecord) {
        jsonData.getMedicalRecords().remove(medicalRecord);
    }
}
