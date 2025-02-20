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

//    public ChildAlertDto toChildAlertDto(Map<String, List<Person>> adultChildrenMap, Map<String, MedicalRecord> medicalRecordMap) {
//        List<ChildPersonDto> childrenDto = adultChildrenMap.get(AgeGroupConstants.CHILDREN).stream()
//                .map(person -> new ChildPersonDto(person,
//                        Optional.of(medicalRecordMap.get(person.getFullName()).getAge()).orElse(0))
//                )
//                .toList();
//        List<AdultPersonDto> adultDto = adultChildrenMap.get(AgeGroupConstants.ADULTS)
//                .stream()
//                .map(AdultPersonDto::new)
//                .collect(Collectors.toList());
//        ChildAlertDto childAlertDto = new ChildAlertDto(childrenDto, adultDto);
//        log.debug("childAlertDto mapped with {} adults and {} children",
//                childAlertDto.adults.size(), childAlertDto.children.size());
//
//        return childAlertDto;
//    }

    public FireInfoDto toAddressPersonDto(List<Person> persons, FireStation fireStation, Map<String, MedicalRecord> medicalRecordMap) {
        List<AddressPersonDto> personsDto = persons.stream()
                .map(person -> new AddressPersonDto(person, medicalRecordMap.get(person.getFullName())))
                .collect(Collectors.toList());

        return new FireInfoDto(personsDto, fireStation);
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

//    public List<ChildPersonDto> toChildAlert2Dto(List<Person> persons, String address, Map<String, MedicalRecord> medicalRecordMap) {
//        Map<String, ChildPersonDto> childs = new HashMap<>();
//        Map<String, List<Person>> otherMap = new HashMap<>();
//        List<AdultPersonDto> other = new ArrayList<>();
//
//        persons.forEach(person -> {
//            MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());
//            if (medicalRecord.isChild() && !childs.containsKey(person.getFullName())) {
//                childs.put(person.getFullName(), new ChildPersonDto(person, medicalRecord.getAge()));
//            }
//        });
//
//        persons.forEach(person -> {
//            for (Map.Entry<String, ChildPersonDto> entry : childs.entrySet()) {
//                if (!entry.getKey().equals(person.getFullName())) {
//                    entry.getValue().otherPersons.add(new AdultPersonDto(person));
//                }
//            }
//        });
//
//        List<ChildPersonDto> childPersons = new ArrayList<>(childs.values());
//        log.debug("ChildPersonDto mapped for {} children at address {}", childPersons.size(), address);
//
//        return childPersons;
//    }
}
