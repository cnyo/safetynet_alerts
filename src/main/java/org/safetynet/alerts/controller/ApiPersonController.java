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

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiPersonController {

    private final PersonService personService;

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

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid person data");
        } catch (Exception e) {
            log.error("PUT /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

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
