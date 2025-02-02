package org.safetynet.alerts.dto;

import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonDto personToDto(Person person) {
        return new PersonDto(person);
    }
}
