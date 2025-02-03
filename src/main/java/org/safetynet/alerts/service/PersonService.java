package org.safetynet.alerts.service;

import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    final int MAJORITY_AGE = 18;

    public int countAdultFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord().getAge() >= MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }

    public int countChildrenFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord().getAge() < MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }
}
