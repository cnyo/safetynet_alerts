package org.safetynet.alerts.controller;

import lombok.RequiredArgsConstructor;
import org.safetynet.alerts.dto.MedicalRecordDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ApiMedicalRecordController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApiMedicalRecordController.class);
    private final MedicalRecordService medicalRecordService;
    private final PersonService personService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        LOGGER.info("POST /medicalRecord");

        try {
            MedicalRecord createdMedicalRecord = medicalRecordService.createMedicalRecord(medicalRecord);
            personService.attachMedicalRecordToPersons(createdMedicalRecord);

            LOGGER.info("POST /medicalRecord MedicalRecord created");

            return ResponseEntity.ok(new MedicalRecordDto(createdMedicalRecord));
        } catch (Exception e) {
            LOGGER.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDto> putMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        LOGGER.info("PUT /medicalRecord");

        try {
            MedicalRecord currentMedicalRecord = medicalRecordService.getByFullName(medicalRecord.getFullName());
            if (currentMedicalRecord == null) {
                LOGGER.info("PUT /medicalRecord MedicalRecord not found");

                return ResponseEntity.notFound().build();
            }

            MedicalRecord updatedMedicalRecord = medicalRecordService.update(medicalRecord, currentMedicalRecord);
            LOGGER.info("PUT /medicalRecord MedicalRecord successfully updated");

            return ResponseEntity.ok(new MedicalRecordDto(updatedMedicalRecord));
        } catch (Exception e) {
            LOGGER.error("PUT /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        LOGGER.info("DELETE /medicalRecord");

        try {
            MedicalRecord medicalRecord = medicalRecordService.getByFullName(firstName + " " + lastName);
            medicalRecordService.remove(medicalRecord);

            LOGGER.info("DELETE /medicalRecord removed.");

            return ResponseEntity.ok(medicalRecord.getFullName());
        } catch (NoSuchElementException e) {
            LOGGER.error("DELETE /medicalRecord Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
