package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.FireStationDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.FireStationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controller for handling CRUD operations related to FireStation resources.
 * Provides endpoints for retrieving, creating, updating, and deleting fire station data.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiFireStationController {

    private final FireStationService fireStationService;

    /**
     * Retrieves the list of all fire stations.
     *
     * @return a ResponseEntity containing a list of FireStation objects with HTTP status 200 if successful.
     */
    @GetMapping("/firestation/all")
    public ResponseEntity<List<FireStation>> getFireStation() {
        log.info("GET /firestation/all");
        List<FireStation> fireStations = fireStationService.getAll();
        log.info("GET /firestation/all return fire stations success");

        return ResponseEntity.ok(fireStations);
    }

    /**
     * Creates a new FireStation entry using the provided details and returns the created FireStation data.
     * Handles conflicts when a FireStation already exists at the specified address.
     * Provides internal server error response in case of unexpected errors.
     *
     * @param fireStation The FireStation object containing the details of the FireStation to be created.
     * @return ResponseEntity containing the created FireStation as a FireStationDto object if successful,
     *         a conflict response if a FireStation already exists at the provided address,
     *         or an internal server error response in case of failure.
     */
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

    /**
     * Updates an existing FireStation resource with the provided parameters.
     * The parameters are expected to provide the necessary data for identifying and updating the FireStation.
     * Handles exceptions for cases such as non-existing FireStation, invalid data, or server errors.
     *
     * @param params a map containing the parameters to update the FireStation resource, such as identifier or fields to be modified
     * @return a ResponseEntity containing the updated FireStation data wrapped in a FireStationDto if successful,
     *         or an error message and appropriate HTTP status in case of failure
     */
    @PatchMapping("/firestation")
    public ResponseEntity<?> patchFireStation(@RequestBody Map<String, Object> params) {
        log.info("PATCH /firestation FireStation update success");

        try {
            FireStation updatedFireStation = fireStationService.update(params);
            log.info("PATCH /firestation Firestation updated success");

            return ResponseEntity.ok(new FireStationDto(updatedFireStation));
        } catch (NoSuchElementException e) {
            log.info("FireStation to update not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FireStation to update not found");
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
        }
        catch (Exception e) {
            log.error("Error updating FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deletes a FireStation entity from the system.
     *
     * @param fireStation the FireStation entity to be deleted, provided in the request body.
     * @return a ResponseEntity containing a success message if the deletion was successful,
     *         a BAD_REQUEST status if the entity was not found or deletion failed, or
     *         an INTERNAL_SERVER_ERROR status if an unexpected error occurred.
     */
    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFireStation(@RequestBody FireStation fireStation) {
        log.info("DELETE /firestation");

        try {
            boolean removed = fireStationService.remove(fireStation);

            if (!removed) {
                log.error("DELETE /firestation fireStation not found");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fire station not removed.");
            }

            log.info("DELETE /firestation removed success");

            return ResponseEntity.ok("FireStation removed successfully.");
        } catch (Exception e) {
            log.error("Error deleting FireStation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
