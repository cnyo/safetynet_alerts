package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.util.List;
import java.util.stream.Collectors;

public class PhoneAlertDto {
    public List<String> phoneNumbers;

    public PhoneAlertDto(List<Person> persons) {
        this.phoneNumbers = persons.stream().map(Person::getPhone).collect(Collectors.toList());
    }
}
