package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

public class AdultPersonDto {
    public String firstName;
    public String lastName;

    public AdultPersonDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }
}
