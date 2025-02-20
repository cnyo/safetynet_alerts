package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.text.MessageFormat;
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
        this.address = MessageFormat.format("{0}, {1} {2}", person.getAddress(), person.getZip(), person.getCity());
        this.age = age;
        this.otherPersons = new ArrayList<>();
    }
}
