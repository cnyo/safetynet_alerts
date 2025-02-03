package org.safetynet.alerts.dto;

import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StationPersonsMapper {
    @Autowired
    PersonService personService;

    public StationPersonsDto toDto(List<Person> persons, String stationNumber) {
        List<PersonDto> personsDto = persons.stream().map(PersonDto::new).toList();
        int adultNbr = personService.countAdultFromPersons(persons);
        int childrenNbr = personService.countChildrenFromPersons(persons);

        return new StationPersonsDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }
}
