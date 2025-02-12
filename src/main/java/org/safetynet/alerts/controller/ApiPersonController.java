package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiPersonController {

    private final PersonService personService;

    @PostMapping("/person")
    public ResponseEntity<PersonDto> postPerson(@RequestBody Person person) {
        try {
            Person createdPerson = personService.createPerson(person);
            log.info("POST /person {} has created with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(createdPerson));
        } catch (Exception e) {
            log.error("POST /person Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/person")
    public ResponseEntity<PersonDto> putPerson(@RequestBody Person person) {
        try {
            Person currentPerson = personService.getPersonByFullName(person.getFullName());
            Person updatededPerson = personService.updatePerson(person, currentPerson);
            log.info("PUT /person {} has updated with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(updatededPerson));
        } catch (Exception e) {
            log.error("PUT /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/person")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        try {
            Person personToRemove = personService.getPersonByFullName(firstName + " " + lastName);
            if (personToRemove == null) {
                log.info("DELETE /person Person not found.");

                return ResponseEntity.notFound().build();
            }

            boolean removed = personService.removePerson(personToRemove);

            if (!removed) {
                log.info("DELETE /person medicalRecord not found.");

                return ResponseEntity.notFound().build();
            }

            log.info("DELETE /person Person removed");

            return ResponseEntity.ok("Person removed successfully.");
        } catch (NoSuchElementException e) {
            log.warn("Person to delete not found");

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("DELETE /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
