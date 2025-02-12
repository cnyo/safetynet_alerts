package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.ChildAlertDto;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PersonDtoMapper {

    public PersonByStationNumberDto toPersonByStationNumberDto(List<Person> persons, String stationNumber, int adultNbr, int childrenNbr) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();

        return new PersonByStationNumberDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }

    public ChildAlertDto toChildAlertDto(List<Person> children, List<Person> adults) {
        List<ChildPersonDto> childrenDto = children.stream().map(ChildPersonDto::new).collect(Collectors.toList());
        List<AdultPersonDto> adultDto = adults.stream().map(AdultPersonDto::new).collect(Collectors.toList());

        return new ChildAlertDto(childrenDto, adultDto);
    }

    public FireInfoDto toDto(List<Person> persons, FireStation fireStation) {
        List<AddressPersonDto> personsDto = persons.stream().map(AddressPersonDto::new).collect(Collectors.toList());

        return new FireInfoDto(personsDto, fireStation);
    }

    public FireInfoDto toAddressPersonDto(List<Person> persons, FireStation fireStation) {
        List<AddressPersonDto> personsDto = persons.stream().map(AddressPersonDto::new).collect(Collectors.toList());

        return new FireInfoDto(personsDto, fireStation);
    }

    public Map<String, List<PersonMedicalInfoDto>> toFloodStationDto(List<Person> persons) {
        Map<String, List<PersonMedicalInfoDto>> personMedicalInfoDtoMap = new HashMap<>();

        persons.forEach(person -> {
                    personMedicalInfoDtoMap
                            .computeIfAbsent(person.getAddress(), k -> new ArrayList<>())
                            .add(new PersonMedicalInfoDto(person));
        });

        return personMedicalInfoDtoMap;
    }

    public List<PersonInfoDto> toPersonInfoLastNameDto(List<Person> persons) {
        return persons
                .stream()
                .map(PersonInfoDto::new)
                .toList();
    }
}
