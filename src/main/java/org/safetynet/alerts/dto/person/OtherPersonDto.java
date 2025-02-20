package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

public class OtherPersonDto {
    public String firstName;
    public String lastName;

    public OtherPersonDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }
}
