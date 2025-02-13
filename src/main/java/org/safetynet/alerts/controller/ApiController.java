package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.*;
import org.safetynet.alerts.dto.person.PersonInfoDto;
import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final PersonDtoMapper personDtoMapper;
    private final PersonService personService;
    private final FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<PersonByStationNumberDto> getPersonByStationNumber(@RequestParam(required = false, defaultValue = "3") String station_number) {
        log.info("GET /firestation Request getPersonByStationNumber for firestation number {}", station_number);

        try {
            List<Person> persons = personService.getAllPersonFromFireStation(station_number);
            int adultNbr = personService.countAdultFromPersons(persons);
            int childrenNbr = personService.countChildrenFromPersons(persons);

            if (persons.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            log.info("GET /firestation Persons from firestation number {}: {} persons", station_number, (long) persons.size());

            return ResponseEntity.ok(personDtoMapper.toPersonByStationNumberDto(persons, station_number, adultNbr, childrenNbr));
        } catch (Exception e) {
            log.error("GET /firestation Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/childAlert")
    public ResponseEntity<ChildAlertDto> getChildAlert(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            List<Person> children = personService.getChildrenAtAddress(address);

            if (children.isEmpty()) {
                log.info("GET /childAlert Children not found for address {}.", address);

                return ResponseEntity.notFound().build();
            }

            List<Person> adults = personService.getAdultAtAddress(address);
            ChildAlertDto childAlertDto = personDtoMapper.toChildAlertDto(children, adults);

            log.info("GET /childAlert Children found for address {}: {}", address, (long) children.size());

            return ResponseEntity.ok(childAlertDto);
        } catch (Exception e) {
            log.error("GET /childAlert Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getAllPhoneNumberByStation(@RequestParam(required = false, defaultValue = "3") String firestation_number) {
        try {
            List<Person> persons = personService.getAllPersonFromFireStation(firestation_number);
            List<String> phoneNumbers = personService.getAllPhoneNumbersFromPersons(persons);

            log.info("GET /phoneAlert Phone numbers found for fire station number {}: {}", firestation_number, (long) phoneNumbers.size());

            return ResponseEntity.ok(phoneNumbers);
        } catch (Exception e) {
            log.error("GET /phoneAlert Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/fire")
    public ResponseEntity<FireInfoDto> getAddressPersons(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            FireStation fireStation = fireStationService.getFireStationAtAddress(address);

            if (fireStation == null) {
                log.info("GET /fire Person not found for fire station address {}.", address);

                return ResponseEntity.notFound().build();
            }

            List<Person> persons = personService.getAllPersonAtAddress(address);
            log.info("GET /fire Persons found for fire station address {}: {}", address, (long) persons.size());

            return ResponseEntity.ok(personDtoMapper.toAddressPersonDto(persons, fireStation));

        } catch (Exception e) {
            log.error("GET /fire Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonMedicalInfoDto>>> getFloodStation(@RequestParam(required = false, defaultValue = "1,3") String stations) {
        try {
            List<Person> persons = personService.getAllPersonFromFireStations(stations);

            log.info("GET /flood/stations Persons found for fire stations {}: {}", stations, (long) persons.size());

            return ResponseEntity.ok(personDtoMapper.toFloodStationDto(persons));

        } catch (Exception e) {
            log.error("GET /flood/stations Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/personInfoLastName")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfoLastName(@RequestParam(required = false, defaultValue = "Boyd") String lastName) {
        try {
            List<Person> persons = personService.getAllPersonByLastName(lastName);
            log.info("GET /personInfoLastName Persons found for lastname {}: {}", lastName, (long) persons.size());

            return ResponseEntity.ok(personDtoMapper.toPersonInfoLastNameDto(persons));

        } catch (Exception e) {
            log.error("GET /personInfoLastName Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam(required = false, defaultValue = "Culver") String city) {
        try {
            List<Person> persons = personService.getAllPersonByCity(city);
            List<String> emails = personService.getAllEmailsFromPersons(persons);

            log.info("GET /communityEmail Email found for city {}: {}", city, (long) emails.size());

            return ResponseEntity.ok(emails);

        } catch (Exception e) {
            log.error("GET /communityEmail Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
