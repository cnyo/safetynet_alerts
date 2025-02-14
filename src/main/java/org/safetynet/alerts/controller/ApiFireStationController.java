package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.fireStation.FireStationDto;
import org.safetynet.alerts.dto.fireStation.FireStationToPatchDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiFireStationController {

    private final PersonService personService;
    private final FireStationService fireStationService;

    @GetMapping("/firestation/all")
    public ResponseEntity<List<FireStation>> postFireStation() {
        log.info("GET /firestation/all Request Return all fires stations");

        try {
            List<FireStation> fireStations = fireStationService.getAll();
            log.info("GET /firestation/all Request Return {} fires stations", fireStations.size());

            return ResponseEntity.ok(fireStations);
        } catch (Exception e) {
            log.error("GET /firestation/all FireStation error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/firestation")
    public ResponseEntity<FireStationDto> postFireStation(@RequestBody FireStation fireStation) {
        log.info("Post /firestation Request postFireStation for firestation number {}", fireStation.getStation());

        try {
            FireStation createdFireStation = fireStationService.createFireStation(fireStation);

            log.info("POST /firestation FireStation created");

            return ResponseEntity.ok(new FireStationDto(createdFireStation));
        } catch (Exception e) {
            log.error("POST /firestation FireStation error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/firestation")
    public ResponseEntity<FireStationDto> patchFireStation(@RequestBody FireStationToPatchDto fireStationToPatchDto) {
        log.info("PATCH /firestation Request putFireStation update firestation {} to {} at address {}.",
                fireStationToPatchDto.station, fireStationToPatchDto.newStation, fireStationToPatchDto.getAddress());

        try {
            FireStation updatedFireStation = fireStationService.update(fireStationToPatchDto);
            log.info("PATCH /firestation Firestation {} successfully updated", updatedFireStation.toString());

            return ResponseEntity.ok(new FireStationDto(updatedFireStation));

        } catch (NoSuchElementException e) {
            log.warn("FireStation to update not found: {} - {}", fireStationToPatchDto.getAddress(), fireStationToPatchDto.station);

            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            log.error("Error updating FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestBody FireStation fireStation) {
        log.info("DELETE /firestation Request {}", fireStation.toString());

        try {
            boolean removed = fireStationService.remove(fireStation);

            if (!removed) {
                log.info("DELETE /firestation firestation {} not found.", fireStation);

                return ResponseEntity.notFound().build();
            }

            log.info("DELETE /firestation {} removed", fireStation);

            return ResponseEntity.ok("FireStation removed successfully.");
        } catch (NoSuchElementException e) {
            log.warn("FireStation {} to delete not found.", fireStation);

            return ResponseEntity.notFound().build();
        }  catch (Exception e) {
            log.error("Error deleting FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
