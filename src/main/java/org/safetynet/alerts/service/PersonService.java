package org.safetynet.alerts.service;

import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    public List<String> getAllPhoneNumbersFromPersons(List<Person> persons) {
        return persons
                .stream()
                .map(Person::getPhone)
                .collect(Collectors.toList());
    }

    public List<String> getAllEmailsFromPersons(List<Person> persons) {
        return persons.stream().map(Person::getEmail).toList();
    }
}
