package org.safetynet.alerts.service;

import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.create(medicalRecord);
    }

    public MedicalRecord getByFullName(String fullName) {
        return medicalRecordRepository.findOneByFullName(fullName)
                .orElseThrow(() -> new NoSuchElementException("No medical record found"));
    }

    public MedicalRecord update(MedicalRecord medicalRecord, MedicalRecord medicalRecordToUpdate) {
        return medicalRecordRepository.update(medicalRecord, medicalRecordToUpdate);
    }

    public void remove(MedicalRecord medicalRecord) {
        medicalRecordRepository.remove(medicalRecord);
    }
}
