package org.safetynet.alerts.controller;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class is responsible for mapping {@link Person} objects into various DTOs based
 * on specified details and context. It contains methods that handle transformations
 * for specific use cases while integrating related information such as medical records
 * or station details.
 */
@Component
@Slf4j
public class PersonDtoMapper {

    /**
     * Converts a list of Person objects and additional station-related information into a PersonByStationNumberDto.
     *
     * @param persons The list of Person objects to map.
     * @param stationNumber The station number associated with these persons.
     * @param adultNbr The number of adults associated with the provided station.
     * @param childrenNbr The number of children associated with the provided station.
     * @return A PersonByStationNumberDto that contains basic information about persons, the station number, and counts of adults and children.
     */
    public PersonByStationNumberDto toPersonByStationNumberDto(List<Person> persons, String stationNumber, int adultNbr, int childrenNbr) {
        List<PersonBasicInfoDto> personsDto = persons.stream().map(PersonBasicInfoDto::new).toList();

        return new PersonByStationNumberDto(personsDto, stationNumber, adultNbr, childrenNbr);
    }

    /**
     * Converts a list of Person objects and a map of MedicalRecord objects into a map where
     * the key is the address and the value is a list of PersonMedicalInfoDto objects.
     *
     * @param persons the list of Person objects to be converted
     * @param medicalRecordMap a map where the key is the person's full name and the value is their MedicalRecord
     * @return a map where the key is the address and the value is a list of PersonMedicalInfoDto objects
     */
    public Map<String, List<PersonMedicalInfoDto>> toFloodStationDto(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
        Map<String, List<PersonMedicalInfoDto>> personMedicalInfoDtoMap = new HashMap<>();

        persons.forEach(person -> {
            personMedicalInfoDtoMap
                    .computeIfAbsent(person.getAddress(), k -> new ArrayList<>())
                    .add(new PersonMedicalInfoDto(person, medicalRecordMap.get(person.getFullName())));
        });

        return personMedicalInfoDtoMap;
    }

    /**
     * Converts a list of {@link Person} objects into a list of {@link PersonInfoDto} objects.
     * Each PersonInfoDto is created using the corresponding {@link Person} object and its
     * associated {@link MedicalRecord} obtained from the provided medicalRecordMap.
     *
     * @param persons a list of {@link Person} objects to be converted
     * @param medicalRecordMap a map where the key is a person's full name, and the value is the
     *                         corresponding {@link MedicalRecord}
     * @return a list of {@link PersonInfoDto} objects that represent the converted data
     */
    public List<PersonInfoDto> toPersonInfoLastNameDto(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
        return persons
                .stream()
                .map(person -> new PersonInfoDto(person, medicalRecordMap.get(person.getFullName())))
                .toList();
    }

    /**
     * Converts a list of {@link Person} objects into a map of {@link ChildAlertDto}, where the keys are
     * the full names of child persons and the values are their corresponding child alert data.
     * This includes their personal details and age, filtered specifically for individuals classified as children.
     *
     * @param persons a list of {@link Person} objects to be processed
     * @param medicalRecordMap a map containing {@link MedicalRecord} objects, keyed by the full name
     *                         of the persons; used to determine the age of each individual
     * @return a map where the keys are the full names of child persons and the values are {@link ChildAlertDto}
     *         representing their child alert details
     */
    public Map<String, ChildAlertDto> toChildAlertDto(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
        Map<String, ChildAlertDto> childAlerts = new HashMap<>();

        persons.forEach(person -> {
            MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());
            if (medicalRecord.isChild() && !childAlerts.containsKey(person.getFullName())) {
                childAlerts.put(person.getFullName(), new ChildAlertDto(person, medicalRecord.getAge()));
            }
        });

        log.debug("{} person(s) converted to ChildAlertDto at address", childAlerts.size());

        return childAlerts;
    }
}
