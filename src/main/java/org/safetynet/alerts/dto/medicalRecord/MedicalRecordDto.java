package org.safetynet.alerts.dto.medicalRecord;

import org.safetynet.alerts.model.MedicalRecord;

import java.util.List;

public class MedicalRecordDto {
    public String firstName;
    public String lastName;
    public String birthdate;
    public List<String> medications;
    public List<String> allergies;

    public MedicalRecordDto(MedicalRecord medicalRecord) {
        this.firstName = medicalRecord.getFirstName();
        this.lastName = medicalRecord.getLastName();
        this.birthdate = medicalRecord.getBirthdate();
        this.medications = medicalRecord.getMedications();
        this.allergies = medicalRecord.getAllergies();
    }
}
