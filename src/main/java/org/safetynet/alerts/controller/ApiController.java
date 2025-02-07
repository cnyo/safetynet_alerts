package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.*;
import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.dto.person.PersonInfoDto;
import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.dto.person.PhoneAlertDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private final JsonDataService jsonDataService;

    final static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonsAtAddressDtoMapper personsAtAddressDtoMapper;

    @Autowired
    private FloodStationDtoMapper floodStationDtoMapper;

    @Autowired
    private PersonInfoDtoMapper personInfoDtoMapper;

    public ApiController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @GetMapping("/firestation")
    public ResponseEntity<FireStationCoverageDto> getPersonByStationNumber(@RequestParam(required = false, defaultValue = "3") String station_number) {
        try {
            List<FireStation> fireStations = jsonDataService.getAllFireStationByStation(station_number);
            List<Person> persons = jsonDataService.getAllPersonFromFireStation(fireStations);
            LOGGER.info("GET /firestation Persons from firestation number {}: {} persons", station_number, (long) persons.size());

            return ResponseEntity.ok(personMapper.personToFireStationCoverageDto(persons, station_number));
        } catch (Exception e) {
            LOGGER.error("GET /firestation Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }

    }

    @GetMapping("/childAlert")
    public ResponseEntity<ChildAlertDto> getChildAlert(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            List<Person> children = jsonDataService.getChildrenAtAddress(address);

            if (children.isEmpty()) {
                LOGGER.info("GET /childAlert Children not found for address {}.", address);

                return ResponseEntity.noContent().build();
            }

            List<Person> adults = jsonDataService.getAdultAtAddress(address);
            ChildAlertDto childAlertDto = personMapper.toChildAlertDtoDto(children, adults);

            LOGGER.info("GET /childAlert Children found for address {}: {}", address, (long) children.size());

            return ResponseEntity.ok(childAlertDto);
        } catch (Exception e) {
            LOGGER.error("GET /childAlert Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<PhoneAlertDto> getAllPhoneNumberByStation(@RequestParam(required = false, defaultValue = "3") String firestation_number) {
        try {
            List<FireStation> fireStations = jsonDataService.getAllFireStationByStation(firestation_number);
            List<Person> persons = jsonDataService.getAllPersonFromFireStation(fireStations);
            PhoneAlertDto phoneAlertDto = new PhoneAlertDto(persons);
            LOGGER.info("GET /phoneAlert Phone numbers found for fire station number {}: {}", firestation_number, (long) phoneAlertDto.phoneNumbers.size());

            return ResponseEntity.ok(phoneAlertDto);
        } catch (Exception e) {
            LOGGER.error("GET /phoneAlert Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/fire")
    public ResponseEntity<FireInfoDto> getAllPersonAtAddress(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            FireStation fireStation = jsonDataService.getFireStationAtAddress(address);

            if (fireStation == null) {
                LOGGER.info("GET /fire Person not found for fire station address {}.", address);

                return ResponseEntity.noContent().build();
            }

            List<Person> persons = jsonDataService.getAllPersonAtAddress(address);
            LOGGER.info("GET /fire Persons found for fire station address {}: {}", address, (long) persons.size());

            return ResponseEntity.ok(personsAtAddressDtoMapper.toDto(persons, fireStation));

        } catch (Exception e) {
            LOGGER.error("GET /fire Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonMedicalInfoDto>>> getFloodStation(@RequestParam(required = false, defaultValue = "1,3") String stations) {
        try {
            List<FireStation> fireStations = jsonDataService.filterFireStationForStations(stations);
            List<Person> persons = jsonDataService.getAllPersonByFireStations(fireStations);

            LOGGER.info("GET /flood/stations Persons found for fire stations {}: {}", stations, (long) persons.size());

            return ResponseEntity.ok(floodStationDtoMapper.toDto(persons, fireStations));

        } catch (Exception e) {
            LOGGER.error("GET /flood/stations Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/personInfoLastName")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfoLastName(@RequestParam(required = false, defaultValue = "Boyd") String lastName) {
        try {
            List<Person> persons = jsonDataService.getAllPersonByLastName(lastName);
            LOGGER.info("GET /personInfoLastName Persons found for lastname {}: {}", lastName, (long) persons.size());

            return ResponseEntity.ok(personInfoDtoMapper.personToPersonInfoDto(persons));

        } catch (Exception e) {
            LOGGER.error("GET /personInfoLastName Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam(required = false, defaultValue = "Culver") String city) {
        try {
            List<Person> persons = jsonDataService.getAllPersonByCity(city);
            List<String> emails = persons.stream().map(Person::getEmail).toList();
            LOGGER.info("GET /communityEmail Email found for city {}: {}", city, (long) emails.size());

            return ResponseEntity.ok(emails);

        } catch (Exception e) {
            LOGGER.error("GET /communityEmail Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/person")
    public ResponseEntity<PersonDto> postPerson(@RequestBody Person person) {
        try {
            Person createdPerson = jsonDataService.createPerson(person);
            LOGGER.info("POST /person {} has created with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(createdPerson));
        } catch (Exception e) {
            LOGGER.error("POST /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/person")
    public ResponseEntity<PersonDto> putPerson(@RequestBody Person person) {
        try {
            Person currentPerson = jsonDataService.getPersonByFullName(person.getFullName());
            Person updatededPerson = jsonDataService.updatePerson(person, currentPerson);
            LOGGER.info("PUT /person {} has updated with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(updatededPerson));
        } catch (Exception e) {
            LOGGER.error("PUT /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestBody String fullName) {
        try {
            Person person = jsonDataService.getPersonByFullName(fullName);

            if (person == null) {
                LOGGER.info("DELETE /person No person with fullname {}.", fullName);

                return ResponseEntity.noContent().build();
            }

            jsonDataService.removePerson(person);
            LOGGER.info("DELETE /person {} has removed with success.", person.getFullName());

            return ResponseEntity.ok(person.getFullName());
        } catch (Exception e) {
            LOGGER.error("DELETE /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
