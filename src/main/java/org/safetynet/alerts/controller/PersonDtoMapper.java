package org.safetynet.alerts.controller;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.fireStation.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PersonDtoMapper {

    public PersonByStationNumberDto toPersonByStationNumberDto(List<Person> persons, String stationNumber, int adultNbr, int childrenNbr) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();

        return new PersonByStationNumberDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }

    public Map<String, List<PersonMedicalInfoDto>> toFloodStationDto(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
        Map<String, List<PersonMedicalInfoDto>> personMedicalInfoDtoMap = new HashMap<>();

        persons.forEach(person -> {
            personMedicalInfoDtoMap
                    .computeIfAbsent(person.getAddress(), k -> new ArrayList<>())
                    .add(new PersonMedicalInfoDto(person, medicalRecordMap.get(person.getFullName())));
        });

        return personMedicalInfoDtoMap;
    }

    public List<PersonInfoDto> toPersonInfoLastNameDto(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
        return persons
                .stream()
                .map(person -> new PersonInfoDto(person, medicalRecordMap.get(person.getFullName())))
                .toList();
    }

    public Map<String, ChildAlertDto> toChildAlertDto(List<Person> persons, String address, Map<String, MedicalRecord> medicalRecordMap) {
        Map<String, ChildAlertDto> childAlerts = new HashMap<>();

        persons.forEach(person -> {
            MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());
            if (medicalRecord.isChild() && !childAlerts.containsKey(person.getFullName())) {
                childAlerts.put(person.getFullName(), new ChildAlertDto(person, medicalRecord.getAge()));
            }
        });

        log.debug("{} person(s) converted to ChildAlertDto at address {}", childAlerts.size(), address);

        return childAlerts;
    }
}
