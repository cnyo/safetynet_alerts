package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

//@Data
public class PersonEmailDto {

    public String email;

    public PersonEmailDto(Person person) {
        this.email = person.getEmail();
    }
}
