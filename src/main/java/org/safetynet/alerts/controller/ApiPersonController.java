package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.PersonDtoMapper;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPersonController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApiPersonController.class);

    @Autowired
    private JsonDataService jsonDataService;

    public ApiPersonController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
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
    public ResponseEntity<String> deletePerson(@RequestBody Person person) {
        try {

            if (person == null) {
                LOGGER.info("DELETE /person Person to update not found.");

                return ResponseEntity.notFound().build();
            }

            Person personRemoved = jsonDataService.removePerson(person);

            // todo :  Vérifier ici si la personne n'existe effectivement plus ?
            LOGGER.info("DELETE /person {} has removed with success.", person.getFullName());

            return ResponseEntity.ok(personRemoved.getFullName());
        } catch (Exception e) {
            LOGGER.error("DELETE /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
