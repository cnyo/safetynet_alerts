package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public MedicalRecord create(MedicalRecord medicalRecord) throws InstanceAlreadyExistsException, NoSuchElementException {
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
        log.debug("MedicalRecord removed: {}", removed ? "success" : "failure");

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

    public int countAdultFromFullName(List<String> fullNames) {
        int adultNbr = medicalRecordRepository.countAdultFromFullName(fullNames);
        log.debug("Count {} adult from fullName", adultNbr);

        return adultNbr;
    }

    public int countChildrenFromFullName(List<String> fullNames) {
        int childrenNbr = medicalRecordRepository.countChildrenFromFullName(fullNames);
        log.debug("Count {} children from fullName", childrenNbr);

        return childrenNbr;
    }
}
