package org.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.safetynet.alerts.model.MedicalRecord;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordDto {
    public String firstName;
    public String lastName;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonSerialize(using = com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer.class)
    public LocalDate birthdate;

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
