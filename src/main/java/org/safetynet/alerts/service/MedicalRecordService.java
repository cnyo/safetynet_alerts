package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.medicalRecord.MedicalRecordToDeleteDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.create(medicalRecord);
    }

    public MedicalRecord update(MedicalRecord medicalRecord) {
        return medicalRecordRepository.update(medicalRecord);
    }

    public boolean remove(MedicalRecordToDeleteDto personToDeleteDto) {
        return medicalRecordRepository.remove(personToDeleteDto);
    }

    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }
}
