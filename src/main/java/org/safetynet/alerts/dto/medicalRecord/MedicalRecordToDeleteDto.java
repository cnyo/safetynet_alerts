package org.safetynet.alerts.dto.medicalRecord;

import lombok.Data;

@Data
public class MedicalRecordToDeleteDto {
    public String firstName;
    public String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
