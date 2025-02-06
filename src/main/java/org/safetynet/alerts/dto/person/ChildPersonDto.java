package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.text.MessageFormat;

//@Data
public class ChildPersonDto {

    public String firstName;
    public String lastName;
    public String address;
    public int age;

    public ChildPersonDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.address = MessageFormat.format("{0}, {1} {2}", person.getAddress(), person.getZip(), person.getCity());
        this.age = person.getMedicalRecord().getAge();
    }
}
