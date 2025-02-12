package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.FireStationDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiFireStationController {

    private final PersonService personService;
    private final FireStationService fireStationService;

    @PostMapping("/firestation")
    public ResponseEntity<FireStationDto> postFireStation(@RequestBody FireStation fireStation) {
        log.info("POST /firestation");

        try {
            FireStation createdFireStation = fireStationService.createFireStation(fireStation);

            log.info("POST /firestation FireStation created");

            return ResponseEntity.ok(new FireStationDto(createdFireStation));
        } catch (Exception e) {
            log.error("POST /firestation FireStation error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/firestation")
    public ResponseEntity<FireStationDto> putFireStation(@RequestParam String address, @RequestParam String oldStation, @RequestParam String station) {
        log.info("PUT /firestation - Address: {}, Old Station: {}, New Station: {}", address, oldStation, station);

        try {
            FireStation fireStationToUpdate = fireStationService.getFireStation(address, oldStation);
            FireStation updatedFireStation = fireStationService.update(fireStationToUpdate, station);

            log.info("PUT /firestation Firestation {} successfully updated", updatedFireStation.toString());

            return ResponseEntity.ok(new FireStationDto(updatedFireStation));

        } catch (NoSuchElementException e) {
            log.warn("FireStation to update not found: {} - {}", address, oldStation);

            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            log.error("Error updating FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<Void> deleteFireStation(@RequestParam String address, @RequestParam String station) {
        log.info("DELETE /firestation");

        try {
            FireStation fireStationToDelete = fireStationService.getFireStation(address, station);
            fireStationService.remove(fireStationToDelete);

            log.info("DELETE /firestation {} removed", fireStationToDelete.toString());

            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("FireStation to delete not found: {} - {}", address, station);

            return ResponseEntity.notFound().build();
        }  catch (Exception e) {
            log.error("Error deleting FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
