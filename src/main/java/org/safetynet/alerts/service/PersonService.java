package org.safetynet.alerts.service;

import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final int MAJORITY_AGE = 18;

    public List<String> getAllPhoneNumbersFromPersons(List<Person> persons) {
        return persons
                .stream()
                .map(Person::getPhone)
                .collect(Collectors.toList());
    }

    public List<String> getAllEmailsFromPersons(List<Person> persons) {
        return persons.stream().map(Person::getEmail).toList();
    }

    public int countAdultFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord() != null && person.getMedicalRecord().getAge() >= MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }

    public int countChildrenFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord() != null && person.getMedicalRecord().getAge() < MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }
}
