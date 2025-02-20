package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;

import java.util.List;

public class PersonMedicalInfoDto {

    public String lastName;
    public int age;
    public String phone;
    public List<String> medications;
    public List<String> allergies;

    public PersonMedicalInfoDto(Person person, MedicalRecord medicalRecord) {
        this.lastName = person.getLastName();
        this.age = medicalRecord.getAge();
        this.phone = person.getPhone();
        this.medications = medicalRecord.getMedications();
        this.allergies = medicalRecord.getAllergies();
    }
}
