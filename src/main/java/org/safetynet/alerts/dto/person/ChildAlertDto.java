package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.ArrayList;
import java.util.List;

public class ChildAlertDto {

    public String firstName;
    public String lastName;
    public String address;
    public int age;
    public List<OtherPersonDto> otherPersons;

    public ChildAlertDto(Person person, int age) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.age = age;
        this.otherPersons = new ArrayList<>();
    }
}
