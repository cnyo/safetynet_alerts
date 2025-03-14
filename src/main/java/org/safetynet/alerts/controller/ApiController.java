package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.ChildAlertDto;
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

/**
 * ApiController is a REST controller that provides various endpoints
 * to handle requests related to persons, fire stations, medical records,
 * and emergency-related features.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {

    private final PersonDtoMapper personDtoMapper;
    private final PersonService personService;
    private final FireStationService fireStationService;
    private final MedicalRecordService medicalRecordService;

    /**
     * Retrieves information about persons associated with a specific fire station number.
     *
     * @param stationNumber the fire station number to filter persons by
     * @return a ResponseEntity containing the data of persons associated with the fire station,
     *         including their full names, the count of adults, and the count of children,
     *         or an appropriate error message or status if no persons are found,
     *         invalid input is given, or an internal server error occurs
     */
    @GetMapping("/firestation")
    public ResponseEntity<?> getPersonByStationNumber(@RequestParam String stationNumber) {
        log.info("GET /firestation");

        try {
            List<Person> persons = personService.getAllPersonFromFireStation(stationNumber);

            if (persons.isEmpty()) {
                log.info("GET /firestation No person found");
                return ResponseEntity.ok().body(Collections.emptyList());
            }

            List<String> fullNames = personService.getFullNamesFromPersons(persons);
            int adultNbr = personService.countAdultFromPersons(fullNames);
            int childrenNbr = personService.countChildrenFromPersons(fullNames);

            log.info("GET /firestation Get person by sation number success");

            return ResponseEntity.ok(personDtoMapper.toPersonByStationNumberDto(persons, stationNumber, adultNbr, childrenNbr));
        } catch (IllegalArgumentException e) {
            log.error("GET /firestation Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (Exception e) {
            log.error("GET /firestation Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves a list of children living at a given address, along with other household members.
     *
     * @param address the address to retrieve child alerts for; must not be null or empty
     * @return a ResponseEntity containing a list of ChildAlertDto objects if successful,
     * or an error message with an appropriate HTTP status code if an error occurs
     */
    @GetMapping("/childAlert")
    public ResponseEntity<?> getChildAlert(@RequestParam String address) {
        log.info("GET /childAlert");

        try {
            List<ChildAlertDto> childAlerts = personService.getChildAlerts(address);
            log.info("GET /childAlert Get children with other persons household at address success");

            return ResponseEntity.ok(childAlerts);
        } catch (IllegalArgumentException e) {
            log.error("GET /childAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Address cannot be null or empty");
        } catch (Exception e) {
            log.error("GET /childAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves all phone numbers associated with addresses covered by a specific fire station.
     *
     * @param fireStation the identifier for the fire station to retrieve phone numbers for
     * @return a ResponseEntity containing a list of phone numbers if successful,
     *         a BAD_REQUEST status if no addresses are associated with the provided station,
     *         or an INTERNAL_SERVER_ERROR status if an unexpected error occurs
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<?> getAllPhoneNumberByStation(@RequestParam String fireStation) {
        log.info("GET /phoneAlert");

        try {
            List<String> addresses = fireStationService.getAddressesForFireStation(fireStation);

            if (addresses.isEmpty()) {
                log.info("GET /phoneAlert No fire station found");
                return ResponseEntity.ok().body(Collections.emptyList());
            }

            List<String> phones = personService.getAllPhoneNumberFromAddresses(addresses);
            log.info("GET /phoneAlert Get all phone numbers by station number success");

            return ResponseEntity.ok(phones);
        } catch (IllegalArgumentException e) {
            log.error("GET /phoneAlert None addresse found: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("GET /phoneAlert Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves a list of persons located at the specified address along with associated fire station and medical record information.
     *
     * @param address The address for which to retrieve person information, fire station information, and medical records.
     * @return A ResponseEntity containing the FireInfoDto object if the operation is successful,
     *         or an appropriate HTTP status message in case of errors such as not found, bad request, or server error.
     */
    @GetMapping("/fire")
    public ResponseEntity<?> getAddressPersons(@RequestParam String address) {
        log.info("GET /fire");

        try {
            FireStation fireStation = fireStationService.getFireStationAtAddress(address);

            if (fireStation == null) {
                log.info("GET /fire No fire station found");
                return ResponseEntity.ok().body(Collections.emptyList());
            }

            List<Person> persons = personService.getAllPersonAtAddress(address);
            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
            FireInfoDto fireInfoDto = personService.toFireInfoDto(persons, fireStation, medicalRecordMap);
            log.info("GET /fire Persons Get persons at fire station address success");

            return ResponseEntity.ok(fireInfoDto);

        } catch (NoSuchElementException e) {
            log.info("GET /fire Fire station not found: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fire station not found.");
        } catch (IllegalArgumentException e) {
            log.info("GET /fire Fire station not found: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("GET /fire Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Handles the GET request for retrieving information about persons linked to specific fire stations.
     *
     * @param stations a string representing the fire station numbers, separated by commas.
     * @return a {@link ResponseEntity} containing the result of the operation.
     *         If successful, it returns a list of persons with their medical records in a flood station DTO format.
     *         If an error occurs, it returns an appropriate HTTP status with an error message.
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<?> getFloodStation(@RequestParam String stations) {
        log.info("GET /flood/stations");

        try {
            List<String> addresses = fireStationService.getAddressesForFireStations(stations);
            List<Person> persons = personService.getAllPersonFromAddresses(addresses);
            Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
            log.info("GET /flood/stations Persons found for fire stations");

            return ResponseEntity.ok(personDtoMapper.toFloodStationDto(persons, medicalRecordMap));

        } catch (IllegalArgumentException e) {
            log.error("GET /flood/stations Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("GET /flood/stations Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves information about a person or persons based on the provided last name.
     * This method fetches a list of persons whose last name matches the given input
     * and maps their data along with associated medical records into a DTO for the response.
     *
     * @param lastName the last name of the person(s) to retrieve information for; must not be null or empty
     * @return a {@code ResponseEntity} containing the processed data if successful,
     *         a BAD_REQUEST response if the input is invalid,
     *         or an INTERNAL_SERVER_ERROR response in case of an unexpected error
     */
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

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last name cannot be null or empty");
        } catch (Exception e) {
            log.error("GET /personInfoLastName Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Handles GET requests for retrieving all email addresses of individuals residing in a specified city.
     *
     * @param city the name of the city to retrieve email addresses for; must not be null or empty
     * @return ResponseEntity containing a list of email addresses if the city is valid and the retrieval is successful;
     *         BAD_REQUEST status with an error message if the city parameter is invalid;
     *         INTERNAL_SERVER_ERROR status if an unexpected error occurs during processing
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<?> getCommunityEmail(@RequestParam String city) {
        log.info("GET /communityEmail");

        try {
            List<String> emails = personService.getAllEmailsAtCity(city);
            log.info("GET /communityEmail Get all email for city success");

            return ResponseEntity.ok(emails);

        } catch (IllegalArgumentException e) {
            log.error("GET /communityEmail Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City cannot be null or empty");
        } catch (Exception e) {
            log.error("GET /communityEmail Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
