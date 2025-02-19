package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PersonDto> postPerson(@RequestBody Person person) {
        log.info("POST /person");
        try {
            Person createdPerson = personService.createPerson(person);
            log.info("POST /person Person created success.");

            return ResponseEntity.ok(new PersonDto(createdPerson));
        } catch (Exception e) {
            log.error("POST /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/person")
    public ResponseEntity<PersonDto> putPerson(@RequestBody Person person) {
        log.info("Patch /person");
        try {
            Person currentPerson = personService.getPersonByFullName(person.getFullName());
            Person updatededPerson = personService.updatePerson(person, currentPerson);
            log.info("PUT /person Person updated success.");

            return ResponseEntity.ok(new PersonDto(updatededPerson));
        } catch (Exception e) {
            log.error("PUT /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestBody String firstName, @RequestParam String lastName) {
        log.info("DELETE /person.");
        try {
            boolean removed = personService.remove(firstName, lastName);

            if (!removed) {
                log.info("DELETE /person medicalRecord not found.");

                return ResponseEntity.notFound().build();
            }

            log.info("DELETE /person Person removed successfully");

            return ResponseEntity.ok("Person removed successfully.");
        } catch (Exception e) {
            log.error("DELETE /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
