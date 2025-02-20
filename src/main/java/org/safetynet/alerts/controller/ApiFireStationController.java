package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.fireStation.FireStationDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.FireStationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiFireStationController {

    private final FireStationService fireStationService;

    @GetMapping("/firestation/all")
    public ResponseEntity<List<FireStation>> postFireStation() {
        log.info("GET /firestation/all");

        try {
            List<FireStation> fireStations = fireStationService.getAll();
            log.info("GET /firestation/all return fire stations success");

            return ResponseEntity.ok(fireStations);
        } catch (Exception e) {
            log.error("GET /firestation/all FireStation error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/firestation")
    public ResponseEntity<?> postFireStation(@RequestBody FireStation fireStation) {
        log.info("Post /firestation");

        try {
            FireStation createdFireStation = fireStationService.create(fireStation);
            log.info("POST /firestation FireStation created success");

            return ResponseEntity.ok(new FireStationDto(createdFireStation));
        } catch (InstanceAlreadyExistsException e) {
            log.error("POST /firestation FireStation at address already exists {} : ", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.CONFLICT).body("FireStation already exists at address.");
        } catch (Exception e) {
            log.error("POST /firestation FireStation error: {} : ", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/firestation")
    public ResponseEntity<?> patchFireStation(@RequestBody Map<String, Object> params) {
        log.info("PATCH /firestation FireStation update success");

        try {
            fireStationService.checkPatchParamsIsOk(params);
            FireStation updatedFireStation = fireStationService.update(params);
            log.info("PATCH /firestation Firestation updated success");

            return ResponseEntity.ok(new FireStationDto(updatedFireStation));
        } catch (NoSuchElementException e) {
            log.info("FireStation to update not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FireStation to update not found");
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        catch (Exception e) {
            log.error("Error updating FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestBody FireStation fireStation) {
        log.info("DELETE /firestation");

        try {
            boolean removed = fireStationService.remove(fireStation);

            if (!removed) {
                log.error("DELETE /firestation fireStation not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fire station not found.");
            }

            log.info("DELETE /firestation removed success");

            return ResponseEntity.ok("FireStation removed successfully.");
        } catch (NoSuchElementException e) {
            log.info("FireStation to delete not found.");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Firestation to delete not found.");
        }  catch (Exception e) {
            log.error("Error deleting FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
