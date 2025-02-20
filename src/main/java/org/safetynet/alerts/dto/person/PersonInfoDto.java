package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;

import java.util.List;

public class PersonInfoDto {

    public String lastName;
    public String address;
    public int age;
    public String email;
    public List<String> medications;
    public List<String> allergies;

    public PersonInfoDto(Person person, MedicalRecord medicalRecord) {
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.age = medicalRecord.getAge();
        this.email = person.getEmail();
        this.medications = medicalRecord.getMedications();
        this.allergies = medicalRecord.getAllergies();
    }
}
