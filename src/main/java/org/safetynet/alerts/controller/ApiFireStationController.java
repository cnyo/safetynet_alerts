package org.safetynet.alerts.controller;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.JsonDataLoader;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiFireStationController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApiFireStationController.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    @PostMapping("/firestation")
    public ResponseEntity<FireStation> postPerson(@RequestBody FireStation fireStation) {
        try {
            FireStation createdFireStation = fireStationService.createFireStation(fireStation);
            personService.attachFireStationToPersons(createdFireStation);

            LOGGER.info("POST /firestation FireStation created: {}", createdFireStation);

            return ResponseEntity.ok(fireStation);
        } catch (Exception e) {
            LOGGER.error("POST /firestation FireStation error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/firestation")
    public ResponseEntity<FireStation> putFireStation(@RequestParam String address, String oldStation, String station) {
        try {
            FireStation fireStationToUpdate = fireStationService.getFireStation(address, oldStation);

            if (fireStationToUpdate == null) {
                LOGGER.info("PUT /firestation FireStation not found");

                return ResponseEntity.notFound().build();
            }


            fireStationToUpdate = fireStationService.update(fireStationToUpdate, station);
            LOGGER.info("PUT /firestation Firestation updated: {}", fireStationToUpdate.toString());

            return ResponseEntity.ok(fireStationToUpdate);

        } catch (Exception e) {
            LOGGER.error("PUT /person Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestParam String address, String station) {
        try {
            FireStation fireStationToDelete = fireStationService.getFireStation(address, station);
            if (fireStationToDelete == null) {
                LOGGER.info("DELETE /firestation fireStation not found {}.", address + " " + station );

                return ResponseEntity.notFound().build();
            }

            fireStationService.remove(fireStationToDelete);

            if (fireStationService.getFireStation(address, station) == null) {
                LOGGER.info("DELETE /firestation {} removed.", fireStationToDelete.toString());

                return ResponseEntity.ok(fireStationToDelete.toString());
            }

            throw new RuntimeException("DELETE /firestation fireStation not deleted");
        } catch (Exception e) {
            LOGGER.error("DELETE /firestation Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
