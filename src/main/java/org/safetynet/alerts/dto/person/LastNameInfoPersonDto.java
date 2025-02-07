package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.List;

//@Data
public class LastNameInfoPersonDto {

    public String lastName;
    public String address;
    public String email;
    public List<String> medications;
    public List<String> allergies;

    public LastNameInfoPersonDto(Person person) {
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.email = person.getEmail();
        this.medications = person.getMedicalRecord().getMedications();
        this.allergies = person.getMedicalRecord().getAllergies();
    }
}
