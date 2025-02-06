package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.List;

//@Data
public class PersonMedicalInfoDto {

    public String lastName;
    public int age;
    public String phone;
    public List<String> medications;
    public List<String> allergies;

    public PersonMedicalInfoDto(Person person) {
        this.lastName = person.getLastName();
        this.age = person.getMedicalRecord().getAge();
        this.phone = person.getPhone();
        this.medications = person.getMedicalRecord().getMedications();
        this.allergies = person.getMedicalRecord().getAllergies();
    }
}
