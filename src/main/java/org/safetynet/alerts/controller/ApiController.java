package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.*;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final PersonDtoMapper personDtoMapper;
    private final PersonService personService;
    private final FireStationService fireStationService;
    private final MedicalRecordService medicalRecordService;

    @GetMapping("/firestation")
    public ResponseEntity<PersonByStationNumberDto> getPersonByStationNumber(@RequestParam String stationNumber) {
        log.info("GET /firestation");

        try {
            List<Person> persons = personService.getAllPersonFromFireStation(stationNumber);
            List<String> fullNames = personService.getFullNamesFromPersons(persons);
            int adultNbr = personService.countAdultFromPersons(fullNames);
            int childrenNbr = personService.countChildrenFromPersons(fullNames);

            if (persons.isEmpty()) {
                log.info("GET /firestation No person found");
                return ResponseEntity.noContent().build();
            }

            log.info("GET /firestation Get person by sation number success");

            return ResponseEntity.ok(personDtoMapper.toPersonByStationNumberDto(persons, stationNumber, adultNbr, childrenNbr));
        } catch (Exception e) {
            log.error("GET /firestation Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildAlertDto>> getChildAlert(@RequestParam String address) {
        log.info("GET /childAlert");

        try {
//            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
//            List<Person> persons = personService.getAllPersonAtAddress(address, medicalRecordMap);
//            Map<String, ChildAlertDto> childAlerts = personDtoMapper.toChildAlertDto(persons, address, medicalRecordMap);
//
//            if (childAlerts.isEmpty()) {
//                log.info("GET /childAlert Children not found at address");
//
//                return ResponseEntity.ok(Collections.emptyList());
//            }

            List<ChildAlertDto> withOtherChildAlerts = personService.attachOtherPersonToChildAlertDto(address);
            log.info("GET /childAlert Get children with other persons household at address success");

            return ResponseEntity.ok(withOtherChildAlerts);
//            return ResponseEntity.ok(childAlertDto);
        } catch (Exception e) {
            log.error("GET /childAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getAllPhoneNumberByStation(@RequestParam String fireStation) {
        log.info("GET /phoneAlert");

        try {
            List<String> addresses = fireStationService.getAddressesForOneFireStation(fireStation);
            List<String> phones = personService.getAllPhoneNumberFromAddresses(addresses);
            log.info("GET /phoneAlert Get all phone numbers by station number success");

            return ResponseEntity.ok(phones);
        } catch (Exception e) {
            log.error("GET /phoneAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/fire")
    public ResponseEntity<?> getAddressPersons(@RequestParam String address) {
        log.info("GET /fire");

        try {
            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
            FireStation fireStation = fireStationService.getFireStationAtAddress(address);
            List<Person> persons = personService.getAllPersonAtAddress(address);
            log.info("GET /fire Persons Get persons at fire station address success");

            return ResponseEntity.ok(personDtoMapper.toAddressPersonDto(persons, fireStation, medicalRecordMap));

        } catch (NoSuchElementException e) {
            log.info("GET /fire Fire station not found: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fire station not found.");
        } catch (Exception e) {
            log.error("GET /fire Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonMedicalInfoDto>>> getFloodStation(@RequestParam String stations) {
        log.info("GET /flood/stations");

        try {
            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
            List<String> addresses = fireStationService.getAddressesForFireStations(stations);
            List<Person> persons = personService.getAllPersonFromAddresses(addresses);
            log.info("GET /flood/stations Persons found for fire stations");

            return ResponseEntity.ok(personDtoMapper.toFloodStationDto(persons, medicalRecordMap));

        } catch (Exception e) {
            log.error("GET /flood/stations Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/personInfo")
    public ResponseEntity<?> getPersonInfoLastName(@RequestParam String lastName) {
        log.info("GET /personInfoLastName");

        try {
            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
            List<Person> persons = personService.getAllPersonByLastName(lastName);
            log.info("GET /personInfoLastName Success get info lastName");

            return ResponseEntity.ok(personDtoMapper.toPersonInfoLastNameDto(persons, medicalRecordMap));

        } catch (IllegalArgumentException e) {
            log.error("GET /personInfoLastName Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("GET /personInfoLastName Error");
        } catch (Exception e) {
            log.error("GET /personInfoLastName Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<?> getCommunityEmail(@RequestParam String city) {
        log.info("GET /communityEmail");

        try {
            List<String> emails = personService.getAllEmailsAtCity(city);
            log.info("GET /communityEmail Get all email for city success");

            return ResponseEntity.ok(emails);

        } catch (IllegalArgumentException e) {
            log.error("GET /communityEmail Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("GET /communityEmail City name cannot be null or empty");
        } catch (Exception e) {
            log.error("GET /communityEmail Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
