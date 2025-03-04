package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

public class OtherPersonDto {
    public String firstName;
    public String lastName;

    public OtherPersonDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
