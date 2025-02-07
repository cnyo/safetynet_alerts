package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.person.PersonInfoDto;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonInfoDtoMapper {
    public List<PersonInfoDto> personToPersonInfoDto(List<Person> persons) {
        return persons
                .stream()
                .map(PersonInfoDto::new)
                .toList();
    }

    public List<PersonInfoDto> personToPersonDto(List<Person> persons) {
        return persons
                .stream()
                .map(PersonInfoDto::new)
                .toList();
    }
}
