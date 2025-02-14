package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.medicalRecord.MedicalRecordToDeleteDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private PersonRepository personRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        Person person = personRepository.findOneByFullName(medicalRecord.getFullName());

        if (person.getMedicalRecord() != null) {
            throw new IllegalArgumentException("Medical record already exists");
        }

        return medicalRecordRepository.create(medicalRecord, person);
    }

    public MedicalRecord getByFullName(String fullName) {
        return medicalRecordRepository.findOneByFullName(fullName)
                .orElseThrow(() -> new NoSuchElementException("No medical record found"));
    }

    public MedicalRecord update(MedicalRecord medicalRecord, MedicalRecord medicalRecordToUpdate) {
        return medicalRecordRepository.update(medicalRecord, medicalRecordToUpdate);
    }

    public boolean remove(MedicalRecordToDeleteDto personToDeleteDto) {
        return medicalRecordRepository.remove(personToDeleteDto);
    }

    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }
}
