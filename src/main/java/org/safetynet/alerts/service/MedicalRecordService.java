package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException {
        MedicalRecord savedMedicalRecord = medicalRecordRepository.create(medicalRecord);
        log.debug("MedicalRecord created successfully");

        return savedMedicalRecord;
    }

    public MedicalRecord update(MedicalRecord medicalRecord) {
        MedicalRecord updatedMedicalRecord = medicalRecordRepository.update(medicalRecord);
        log.debug("MedicalRecord updated successfully");

        return updatedMedicalRecord;
    }

    public boolean remove(String firstName, String lastName) {
        boolean removed = medicalRecordRepository.remove(firstName, lastName);
        log.debug("MedicalRecord removed : {}", removed ? "success" : "failure");

        return removed;
    }

    public List<MedicalRecord> getAll() {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();
        log.debug("getAll medical records: {}", medicalRecords.size());

        return medicalRecords;
    }

    public Map<String, MedicalRecord> getAllByFullName() {
        Map<String, MedicalRecord> medicalRecords = medicalRecordRepository.getAllByFullName();
        log.debug("Medical records ordered by fullName found: {}", medicalRecords.size());

        return medicalRecords;
    }
}
