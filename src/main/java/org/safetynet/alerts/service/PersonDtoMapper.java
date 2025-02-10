package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.ChildAlertDto;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class PersonDtoMapper {

    final int MAJORITY_AGE = 18;

    @Autowired
    private PersonService personService;

    public PersonByStationNumberDto toPersonByStationNumberDto(List<Person> persons, String stationNumber) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();
        int adultNbr = personService.countAdultFromPersons(persons);
        int childrenNbr = personService.countChildrenFromPersons(persons);

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

    public Map<String, List<PersonMedicalInfoDto>> toFloodStationDto(List<Person> persons, List<FireStation> fireStations) {
        List<String> addresses = fireStations.stream().map(FireStation::getAddress).toList();
        Map<String, List<PersonMedicalInfoDto>> personsByAddress = new TreeMap<>();

        for (String address : addresses) {
            List<PersonMedicalInfoDto> personsAtAddress = persons
                    .stream()
                    .filter(person -> person.getAddress().equals(address))
                    .map(PersonMedicalInfoDto::new).toList();

            personsByAddress.put(address, personsAtAddress);
        }

        return personsByAddress;
    }

    public List<PersonInfoDto> toPersonInfoLastNameDto(List<Person> persons) {
        return persons
                .stream()
                .map(PersonInfoDto::new)
                .toList();
    }
}
