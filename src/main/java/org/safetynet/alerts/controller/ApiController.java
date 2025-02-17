package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.*;
import org.safetynet.alerts.dto.fireStation.FireInfoDto;
import org.safetynet.alerts.dto.person.PersonInfoDto;
import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final PersonDtoMapper personDtoMapper;
    private final PersonService personService;
    private final FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<PersonByStationNumberDto> getPersonByStationNumber(@RequestParam String stationNumber) {
        log.info("GET /firestation");

        try {
            List<Person> persons = personService.getAllPersonFromFireStation(stationNumber);
            int adultNbr = personService.countAdultFromPersons(persons);
            int childrenNbr = personService.countChildrenFromPersons(persons);

            if (persons.isEmpty()) {
                log.info("GET /firestation No person found");
                return ResponseEntity.noContent().build();
            }

            log.info("GET /firestation Success get person by sation number");

            return ResponseEntity.ok(personDtoMapper.toPersonByStationNumberDto(persons, stationNumber, adultNbr, childrenNbr));
        } catch (Exception e) {
            log.error("GET /firestation Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/childAlert")
    public ResponseEntity<ChildAlertDto> getChildAlert(@RequestParam String address) {
        log.info("GET /childAlert");

        try {
            List<Person> children = personService.getChildrenAtAddress(address);
            List<Person> adults = personService.getAdultAtAddress(address);
            ChildAlertDto childAlertDto = personDtoMapper.toChildAlertDto(children, adults);

            log.info("GET /childAlert Get children and adults success");

            return ResponseEntity.ok(childAlertDto);
        } catch (Exception e) {
            log.error("GET /childAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getAllPhoneNumberByStation(@RequestParam String fireStation) {
        log.info("GET /phoneAlert");

        try {
            List<Person> persons = personService.getAllPersonFromFireStation(fireStation);
            List<String> phoneNumbers = personService.getAllPhoneNumbersFromPersons(persons);

            log.info("GET /phoneAlert Get all phone numbers by station number success");

            return ResponseEntity.ok(phoneNumbers);
        } catch (Exception e) {
            log.error("GET /phoneAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/fire")
    public ResponseEntity<FireInfoDto> getAddressPersons(@RequestParam String address) {
        log.info("GET /fire");

        try {
            FireStation fireStation = fireStationService.getFireStationAtAddress(address);

            if (fireStation == null) {
                log.info("GET /fire Person not found for fire station address");

                return ResponseEntity.notFound().build();
            }

            List<Person> persons = personService.getAllPersonAtAddress(address);
            log.info("GET /fire Persons Get persons at fire station address success");

            return ResponseEntity.ok(personDtoMapper.toAddressPersonDto(persons, fireStation));

        } catch (Exception e) {
            log.error("GET /fire Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonMedicalInfoDto>>> getFloodStation(@RequestParam String stations) {
        log.info("GET /flood/stations");

        try {
            List<Person> persons = personService.getAllPersonFromFireStations(stations);
            log.info("GET /flood/stations Persons found for fire stations");

            return ResponseEntity.ok(personDtoMapper.toFloodStationDto(persons));

        } catch (Exception e) {
            log.error("GET /flood/stations Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfoLastName(@RequestParam String lastName) {
        log.info("GET /personInfoLastName");

        try {
            List<Person> persons = personService.getAllPersonByLastName(lastName);

            log.info("GET /personInfoLastName Success get info lastName");

            return ResponseEntity.ok(personDtoMapper.toPersonInfoLastNameDto(persons));

        } catch (Exception e) {
            log.error("GET /personInfoLastName Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam String city) {
        log.info("GET /communityEmail");

        try {
            List<Person> persons = personService.getAllPersonByCity(city);
            List<String> emails = personService.getAllEmailsFromPersons(persons);

            log.info("GET /communityEmail Get all email for city success");
            ResponseEntity<List<String>> test = ResponseEntity.ok(emails);
            return ResponseEntity.ok(emails);

        } catch (Exception e) {
            log.error("GET /communityEmail Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
