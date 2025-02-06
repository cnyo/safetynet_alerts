package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.AddressPersonDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//@Data
@Component
public class PersonsAtAddressDtoMapper {

    public FireInfoDto toDto(List<Person> persons, FireStation fireStation) {
        List<AddressPersonDto> personsDto = persons.stream().map(AddressPersonDto::new).collect(Collectors.toList());

        return new FireInfoDto(personsDto, fireStation);
    }
}
