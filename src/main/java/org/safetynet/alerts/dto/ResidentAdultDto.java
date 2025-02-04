package org.safetynet.alerts.dto;

import org.safetynet.alerts.model.Person;

public class ResidentAdultDto {
    public String firstName;
    public String lastName;

    public ResidentAdultDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }
}
