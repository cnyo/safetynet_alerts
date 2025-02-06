package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.person.PersonBasicInfoDto;
import org.safetynet.alerts.dto.FireStationCoverageDto;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StationPersonsMapper {

    final int MAJORITY_AGE = 18;

    public FireStationCoverageDto toDto(List<Person> persons, String stationNumber) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();
        int adultNbr = countAdultFromPersons(persons);
        int childrenNbr = countChildrenFromPersons(persons);

        return new FireStationCoverageDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }

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
