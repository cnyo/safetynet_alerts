package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.medicalRecord.MedicalRecordDto;
import org.safetynet.alerts.dto.medicalRecord.MedicalRecordToDeleteDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiMedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final PersonService personService;

    @GetMapping("/medicalRecord/all")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        log.info("GET /medicalRecord/all Request Return all medical records.");

        try {
            List<MedicalRecord> medicalRecords = medicalRecordService.getAll();
            log.info("GET /medicalRecord/all Request Return {} medical records", medicalRecords.size());

            return ResponseEntity.ok(medicalRecords);
        } catch (Exception e) {
            log.error("GET /medicalRecord/all MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("POST /medicalRecord");

        try {
            MedicalRecord medicalRecordToSave = medicalRecordService.createMedicalRecord(medicalRecord);
//            personService.attachMedicalRecordToPersons(medicalRecordToSave);

            log.info("POST /medicalRecord MedicalRecord created");

            return ResponseEntity.ok(new MedicalRecordDto(medicalRecordToSave));
        } catch (Exception e) {
            log.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> patchMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("PATCH /medicalRecord");

        try {
            MedicalRecord updatedMedicalRecord = medicalRecordService.update(medicalRecord);
            log.info("PATCH /medicalRecord MedicalRecord successfully updated");

            return ResponseEntity.ok(new MedicalRecordDto(updatedMedicalRecord));
        } catch (NoSuchElementException e) {
            log.warn("MedicalRecord to update not found");

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("PATCH /medicalRecord MedicalRecord error: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestBody MedicalRecordToDeleteDto personToDeleteDto) {
        log.info("DELETE personToDeleteDto: {}", personToDeleteDto);

        try {
            boolean removed = medicalRecordService.remove(personToDeleteDto);

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
