package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.MedicalRecordDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiMedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final PersonService personService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("POST /medicalRecord");

        try {
            MedicalRecord medicalRecordToSave = medicalRecordService.createMedicalRecord(medicalRecord);
            personService.attachMedicalRecordToPersons(medicalRecordToSave);

            log.info("POST /medicalRecord MedicalRecord created");

            return ResponseEntity.ok(new MedicalRecordDto(medicalRecordToSave));
        } catch (Exception e) {
            log.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> putMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("PUT /medicalRecord");

        try {
            MedicalRecord currentMedicalRecord = medicalRecordService.getByFullName(medicalRecord.getFullName());
            MedicalRecord updatedMedicalRecord = medicalRecordService.update(medicalRecord, currentMedicalRecord);
            log.info("PUT /medicalRecord MedicalRecord successfully updated");

            return ResponseEntity.ok(new MedicalRecordDto(updatedMedicalRecord));
        } catch (NoSuchElementException e) {
            log.warn("MedicalRecord to update not found");

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("PUT /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("DELETE /medicalRecord");

        try {
            MedicalRecord medicalRecord = medicalRecordService.getByFullName(firstName + " " + lastName);
            boolean removed = medicalRecordService.remove(medicalRecord);

            if (!removed) {
                log.info("DELETE /medicalRecord medicalRecord not found.");

                return ResponseEntity.notFound().build();
            }

            log.info("DELETE /medicalRecord removed.");

            return ResponseEntity.ok("medicalRecord removed successfully.");
        } catch (NoSuchElementException e) {
            log.warn("MedicalRecord to delete not found");

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("DELETE /medicalRecord Error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
