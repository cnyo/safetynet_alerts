package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.MedicalRecordDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import java.time.DateTimeException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST controller for managing medical record operations. This controller
 * provides endpoints for CRUD operations on medical records, including
 * functionalities for creating, reading, updating, and deleting records.
 *
 * Exception handling is included for specific scenarios such as invalid
 * data inputs, conflicts, and processing errors.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiMedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Handles exceptions related to HTTP message parsing errors, particularly focusing on date format issues.
     * Logs the root cause of the exception and returns an appropriate error message based on the type of the cause.
     *
     * @param e the exception thrown when an HTTP message is not readable, such as due to invalid input formats
     * @return a string message describing the specific error, with guidance for resolution if applicable
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public String handleException(HttpMessageNotReadableException e) {
        Throwable cause = findRootCause(e);
        log.error(cause.getMessage(), cause);

        if (cause instanceof DateTimeException) {
            return "Invalid date format. Please use MM/dd/yyyy.";
        }

        return "Bad request formated";
    }

    /**
     * Determines the root cause of the given throwable by traversing the exception chain.
     *
     * @param throwable the initial throwable whose root cause is to be located
     * @return the root cause throwable or the original throwable if no deeper cause exists
     */
    private Throwable findRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }

        return cause;
    }

    /**
     * Retrieves all medical records from the database.
     *
     * @return ResponseEntity containing a list of all MedicalRecord objects if successful,
     * or an error response with a 500 status code if an internal error occurs.
     */
    @GetMapping("/medicalRecord/all")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        log.info("GET /medicalRecord/all");

        try {
            List<MedicalRecord> medicalRecords = medicalRecordService.getAll();
            log.info("GET /medicalRecord/all Return medical records success");

            return ResponseEntity.ok(medicalRecords);
        } catch (Exception e) {
            log.error("GET /medicalRecord/all MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Handles the HTTP POST request to create a new medical record.
     *
     * @param medicalRecord the request body containing the medical record data to be created
     * @return ResponseEntity containing the created medical record in DTO format on success,
     *         or an error message with the appropriate HTTP status code on failure
     */
    @PostMapping("/medicalRecord")
    public ResponseEntity<?> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("POST /medicalRecord");

        try {
            MedicalRecord savedMedicalRecord = medicalRecordService.create(medicalRecord);
            log.info("POST /medicalRecord MedicalRecord created success");

            return ResponseEntity.ok(new MedicalRecordDto(savedMedicalRecord));
        } catch (DateTimeException e) {
            log.error("POST /medicalRecord Invalid birthdate: future date provided");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid birthdate: future date provided.");
        } catch (InstanceAlreadyExistsException e) {
            log.error("POST /medicalRecord MedicalRecord already exists for person");

            return ResponseEntity.status(HttpStatus.CONFLICT).body("MedicalRecord already exists for person.");
        } catch (NoSuchElementException e) {
            log.error("POST /medicalRecord MedicalRecord Person for new medical record not exists");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person for new medical record not exists.");
        } catch (Exception e) {
            log.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Updates an existing medical record with new data. The method handles the provided
     * medical record update request, verifies the data, and updates the respective record.
     *
     * @param medicalRecord the object containing updated medical record information
     * @return a {@code ResponseEntity} containing the updated medical record data wrapped
     *         in a DTO if the operation is successful, or an appropriate error response
     *         if the update fails due to invalid data, record not being found, or other errors
     */
    @PatchMapping("/medicalRecord")
    public ResponseEntity<?> patchMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("PATCH /medicalRecord");

        try {
            MedicalRecord updatedMedicalRecord = medicalRecordService.update(medicalRecord);
            log.info("PATCH /medicalRecord MedicalRecord updated success");

            return ResponseEntity.ok(new MedicalRecordDto(updatedMedicalRecord));
        }  catch (DateTimeException e) {
            log.error("PATCH /medicalRecord Invalid birthdate: future date provided");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid birthdate: future date provided.");
        } catch (NoSuchElementException e) {
            log.info("MedicalRecord to update not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MedicalRecord to update not found.");
        } catch (Exception e) {
            log.error("PATCH /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deletes a medical record based on the provided first name and last name.
     *
     * @param firstName the first name of the individual whose medical record is to be deleted
     * @param lastName the last name of the individual whose medical record is to be deleted
     * @return a ResponseEntity containing a success or error message, with the appropriate HTTP status code
     */
    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("DELETE /medicalRecord");

        try {
            boolean removed = medicalRecordService.remove(firstName, lastName);

            if (!removed) {
                log.info("DELETE /medicalRecord MedicalRecord not deleted");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MedicalRecord not deleted.");
            }

            log.info("DELETE /medicalRecord removed.");

            return ResponseEntity.ok("medicalRecord removed success.");
        } catch (Exception e) {
            log.error("DELETE /medicalRecord Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
