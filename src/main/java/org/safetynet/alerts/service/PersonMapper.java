package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.ChildAlertDto;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.AdultPersonDto;
import org.safetynet.alerts.dto.person.ChildPersonDto;
import org.safetynet.alerts.dto.person.PersonBasicInfoDto;
import org.safetynet.alerts.dto.FireStationCoverageDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    final int MAJORITY_AGE = 18;

    public FireStationCoverageDto personToFireStationCoverageDto(List<Person> persons, String stationNumber) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();
        int adultNbr = countAdultFromPersons(persons);
        int childrenNbr = countChildrenFromPersons(persons);

        return new FireStationCoverageDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }

    public ChildAlertDto toChildAlertDtoDto(List<Person> children, List<Person> adults) {
        List<ChildPersonDto> childrenDto = children.stream().map(ChildPersonDto::new).collect(Collectors.toList());
        List<AdultPersonDto> adultDto = adults.stream().map(AdultPersonDto::new).collect(Collectors.toList());

        return new ChildAlertDto(childrenDto, adultDto);
    }

    // todo: déplacer dans personService
    private int countAdultFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord().getAge() >= MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }

    private int countChildrenFromPersons(List<Person> persons) {
        int count = 0;

        for (Person person : persons) {
            if (person.getMedicalRecord().getAge() < MAJORITY_AGE) {
                count++;
            }
        }

        return count;
    }
}
