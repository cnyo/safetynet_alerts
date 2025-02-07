package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.*;
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

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private StationPersonsMapper stationPersonsMapper;

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
        logger.info("Test de Log4j2 - Est-ce que le fichier se crée ?");

        try {
            List<FireStation> fireStations = jsonDataService.getAllFireStationByStation(station_number);
            List<Person> persons = jsonDataService.getAllPersonFromFireStation(fireStations);
            logger.info("Persons from firestation number {}: {} persons", station_number, (long) persons.size());

            return ResponseEntity.ok(stationPersonsMapper.toDto(persons, station_number));
        } catch (Exception e) {
            logger.info("Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }

    }

    @GetMapping("/childAlert")
    public ResponseEntity<ChildAlertDto> getChildAlert(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            List<Person> children = jsonDataService.getChildrenAtAddress(address);

            if (children.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<Person> adults = jsonDataService.getAdultAtAddress(address);
            ChildAlertDto childAlertDto = personMapper.toChildAlertDtoDto(children, adults);

            return ResponseEntity.ok(childAlertDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<PhoneAlertDto> getAllPhoneNumberByStation(@RequestParam(required = false, defaultValue = "3") String firestation_number) {
        try {
            List<FireStation> fireStations = jsonDataService.getAllFireStationByStation(firestation_number);
            List<Person> persons = jsonDataService.getAllPersonFromFireStation(fireStations);

            return ResponseEntity.ok(new PhoneAlertDto(persons));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/fire")
    public ResponseEntity<FireInfoDto> getAllPersonAtAddress(@RequestParam(required = false, defaultValue = "1509 Culver St") String address) {
        try {
            FireStation fireStation = jsonDataService.getFireStationAtAddress(address);

            if (fireStation == null) {
                return ResponseEntity.noContent().build();
            }

            List<Person> persons = jsonDataService.getAllPersonAtAddress(address);

            return ResponseEntity.ok(personsAtAddressDtoMapper.toDto(persons, fireStation));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonMedicalInfoDto>>> getFloodStation(@RequestParam(required = false, defaultValue = "1,3") String stations) {
        try {
            List<FireStation> fireStations = jsonDataService.filterFireStationForStations(stations);
            List<Person> persons = jsonDataService.getAllPersonByFireStations(fireStations);

            return ResponseEntity.ok(floodStationDtoMapper.toDto(persons, fireStations));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/personInfoLastName")
    public ResponseEntity<List<PersonInfoDto>> getPersonInfoLastName(@RequestParam(required = false, defaultValue = "Boyd") String lastName) {
        try {
            List<Person> persons = jsonDataService.getAllPersonByLastName(lastName);

            return ResponseEntity.ok(personInfoDtoMapper.personToPersonInfoDto(persons));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam(required = false, defaultValue = "Culver") String city) {
        try {
            List<Person> persons = jsonDataService.getAllPersonByCity(city);

            return ResponseEntity.ok(persons.stream().map(Person::getEmail).toList());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/person")
    public PersonDto postPerson(@RequestBody Person person) {
        Person createdPerson = jsonDataService.createPerson(person);

        return new PersonDto(createdPerson);
    }

    @PutMapping("/person")
    public PersonDto putPerson(@RequestBody Person person) {
        Person createdPerson = jsonDataService.updatePerson(person);

        return new PersonDto(createdPerson);
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestBody String fullName) {
        try {
            Person person = jsonDataService.getPersonByFullName(fullName);

            if (person == null) {
                return ResponseEntity.noContent().build();
            }

            jsonDataService.removePerson(person);

            return ResponseEntity.ok(person.getFullName());
        } catch (Exception e) {
//            logger.info("Person {} not found !", fullName);

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
