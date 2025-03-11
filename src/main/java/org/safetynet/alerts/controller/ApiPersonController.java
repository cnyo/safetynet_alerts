package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

/**
 * REST controller for managing operations on `Person` entities.
 * Provides endpoints for retrieving, creating, updating, and deleting persons.
 * Each endpoint logs its activity for debugging and monitoring purposes.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiPersonController {

    private final PersonService personService;

    /**
     * Retrieves a list of all persons from the system.
     * This method handles GET requests to the "/person/all" endpoint.
     * Logs the operation and returns the result or an error status.
     *
     * @return ResponseEntity containing a list of all registered persons as the body,
     * or an appropriate HTTP status code if an error occurs.
     */
    @GetMapping("/person/all")
    public ResponseEntity<List<Person>> getAllPersons() {
        log.info("GET /person/all");

        try {
            List<Person> persons = personService.getAll();
            log.info("GET /person/all Get medical records success.");

            return ResponseEntity.ok(persons);
        } catch (Exception e) {
            log.error("GET /person/all MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Handles POST requests to create a new person.
     *
     * @param person the person's details to create, provided in the request body
     * @return a ResponseEntity containing the created person's data as a PersonDto and a status code of 200 OK
     *         if creation is successful. Returns a 409 CONFLICT status if the person already exists,
     *         a 400 BAD REQUEST status if the input data is invalid, or a 500 INTERNAL SERVER ERROR
     *         status for other unexpected errors.
     */
    @PostMapping("/person")
    public ResponseEntity<?> postPerson(@RequestBody Person person) {
        log.info("POST /person");
        try {
            Person createdPerson = personService.create(person);
            log.info("POST /person Person created success.");

            return ResponseEntity.ok(new PersonDto(createdPerson));
        } catch (InstanceAlreadyExistsException e) {
            log.error("POST /person Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Person already exists");
        } catch (IllegalArgumentException e) {
            log.error("POST /person Error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid person data");
        } catch (Exception e) {
            log.error("POST /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Updates the details of an existing person entity.
     * Handles validation and exceptions related to updating person information.
     *
     * @param person The person object containing updated information.
     *               Must contain valid data and correspond to an existing record.
     * @return ResponseEntity containing the updated person data wrapped in a PersonDto object
     *         if the operation is successful, or an appropriate error message with the respective
     *         HTTP status code if an exception occurs.
     */
    @PatchMapping("/person")
    public ResponseEntity<?> patchPerson(@RequestBody Person person) {
        log.info("Patch /person");

        try {
            Person updatededPerson = personService.update(person);
            log.info("PUT /person Person updated success.");

            return ResponseEntity.ok(new PersonDto(updatededPerson));
        } catch (InstanceNotFoundException e) {
            log.error("PUT /person not found: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
        } catch (IllegalArgumentException e) {
            log.error("PUT /person Invalid person data: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid person data");
        } catch (Exception e) {
            log.error("PUT /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deletes a person based on their first and last name.
     * The operation logs the success or failure of the deletion.
     *
     * @param firstName the first name of the person to be deleted
     * @param lastName the last name of the person to be deleted
     * @return a ResponseEntity containing the success or failure message with the appropriate HTTP status
     */
    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("DELETE /person.");
        try {
            boolean removed = personService.remove(firstName, lastName);

            if (!removed) {
                log.info("DELETE /person person not removed.");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("person not removed.");
            }

            log.info("DELETE /person Person removed successfully");

            return ResponseEntity.ok("Person removed successfully.");
        } catch (Exception e) {
            log.error("DELETE /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
