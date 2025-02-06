package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.List;

//@Data
public class PersonInfoDto {

    public String lastName;
    public String address;
    public int age;
    public String email;
    public List<String> medications;
    public List<String> allergies;

    public PersonInfoDto(Person person) {
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.age = person.getMedicalRecord().getAge();
        this.email = person.getEmail();
        this.medications = person.getMedicalRecord().getMedications();
        this.allergies = person.getMedicalRecord().getAllergies();
    }
}
