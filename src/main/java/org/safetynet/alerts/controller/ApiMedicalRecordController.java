package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.medicalRecord.MedicalRecordDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiMedicalRecordController {

    private final MedicalRecordService medicalRecordService;

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

    @PostMapping("/medicalRecord")
    public ResponseEntity<?> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("POST /medicalRecord");

        try {
            MedicalRecord medicalRecordToSave = medicalRecordService.create(medicalRecord);
            log.info("POST /medicalRecord MedicalRecord created success");

            return ResponseEntity.ok(new MedicalRecordDto(medicalRecordToSave));
        } catch (InstanceAlreadyExistsException e) {
            log.error("POST /medicalRecord MedicalRecord already exists for person");

            return ResponseEntity.badRequest().body("MedicalRecord already exists for person.");
        } catch (Exception e) {
            log.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/medicalRecord")
    public ResponseEntity<?> patchMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("PATCH /medicalRecord");

        try {
            MedicalRecord updatedMedicalRecord = medicalRecordService.update(medicalRecord);
            log.info("PATCH /medicalRecord MedicalRecord updated success");

            return ResponseEntity.ok(new MedicalRecordDto(updatedMedicalRecord));
        } catch (NoSuchElementException e) {
            log.info("MedicalRecord to update not found");

            return ResponseEntity.badRequest().body("MedicalRecord to update not found.");
        } catch (Exception e) {
            log.error("PATCH /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("DELETE /medicalRecord");

        try {
            boolean removed = medicalRecordService.remove(firstName, lastName);

            if (!removed) {
                log.info("DELETE /medicalRecord MedicalRecord not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MedicalRecord not found.");
            }

            log.info("DELETE /medicalRecord removed.");

            return ResponseEntity.ok("medicalRecord removed success.");
        } catch (NoSuchElementException e) {
            log.info("MedicalRecord to delete not found");

            return ResponseEntity.badRequest().body("MedicalRecord to delete not found.");
        } catch (Exception e) {
            log.error("DELETE /medicalRecord Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
