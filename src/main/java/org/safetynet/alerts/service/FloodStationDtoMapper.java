package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class FloodStationDtoMapper {
    public Map<String, List<PersonMedicalInfoDto>> toDto(List<Person> persons, List<FireStation> fireStations) {
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
}
