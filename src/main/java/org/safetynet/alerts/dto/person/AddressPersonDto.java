package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.ArrayList;
import java.util.List;

//@Data
public class AddressPersonDto {

    public String lastName;
    public String phoneNumber;
    public int age;
    public List<String> medications;
    public List<String> allergies;

    public AddressPersonDto(Person person) {
        this.lastName = person.getLastName();
        this.phoneNumber = person.getPhone();
        this.age = person.getMedicalRecord().getAge();
        this.medications = new ArrayList<>(person.getMedicalRecord().getMedications());
        this.allergies = new ArrayList<>(person.getMedicalRecord().getAllergies());
    }
}
