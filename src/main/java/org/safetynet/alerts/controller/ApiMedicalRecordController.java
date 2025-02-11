package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.person.PersonDto;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataLoader;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiMedicalRecordController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApiMedicalRecordController.class);

    @Autowired
    private JsonDataLoader jsonDataService;

    @Autowired
    private PersonDtoMapper personDtoMapper;

    @Autowired
    private PersonService personService;

    public ApiMedicalRecordController(JsonDataLoader jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @PostMapping("/medicalRecord")
    public ResponseEntity<PersonDto> postPerson(@RequestBody Person person) {
        try {
            Person createdPerson = personService.createPerson(person);
            LOGGER.info("POST /person {} has created with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(createdPerson));
        } catch (Exception e) {
            LOGGER.error("POST /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/medicalRecord")
    public ResponseEntity<PersonDto> putPerson(@RequestBody Person person) {
        try {
            Person currentPerson = personService.getPersonByFullName(person.getFullName());
            Person updatededPerson = personService.updatePerson(person, currentPerson);
            LOGGER.info("PUT /person {} has updated with success.", person.getFullName());

            return ResponseEntity.ok(new PersonDto(updatededPerson));
        } catch (Exception e) {
            LOGGER.error("PUT /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deletePerson(@RequestBody Person person) {
        try {

            if (person == null) {
                LOGGER.info("DELETE /person Person to update not found.");

                return ResponseEntity.notFound().build();
            }

            Person personRemoved = personService.removePerson(person);

            // todo :  Vérifier ici si la personne n'existe effectivement plus ?
            LOGGER.info("DELETE /person {} has removed with success.", person.getFullName());

            return ResponseEntity.ok(personRemoved.getFullName());
        } catch (Exception e) {
            LOGGER.error("DELETE /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
