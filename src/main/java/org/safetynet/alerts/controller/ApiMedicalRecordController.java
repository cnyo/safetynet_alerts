package org.safetynet.alerts.controller;

import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiMedicalRecordController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApiMedicalRecordController.class);

    @Autowired
    MedicalRecordService medicalRecordService;

    @Autowired
    private PersonService personService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        LOGGER.info("POST /medicalRecord");

        try {
            MedicalRecord createdMedicalRecord = medicalRecordService.createMedicalRecord(medicalRecord);
            personService.attachMedicalRecordToPersons(createdMedicalRecord);

            LOGGER.info("POST /medicalRecord MedicalRecord created");

            return ResponseEntity.ok(createdMedicalRecord);
        } catch (Exception e) {
            LOGGER.error("POST /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> putMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        LOGGER.info("PUT /medicalRecord");

        try {
            MedicalRecord currentMedicalRecord = medicalRecordService.getByFullName(medicalRecord.getFullName());
            if (currentMedicalRecord == null) {
                LOGGER.info("PUT /medicalRecord MedicalRecord not found");

                return ResponseEntity.notFound().build();
            }

            medicalRecordService.update(medicalRecord, currentMedicalRecord);
            LOGGER.info("PUT /medicalRecord MedicalRecord successfully updated");

            return ResponseEntity.ok(currentMedicalRecord);
        } catch (Exception e) {
            LOGGER.error("PUT /medicalRecord MedicalRecord error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<String> deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        LOGGER.info("DELETE /medicalRecord");

        try {
            MedicalRecord medicalRecord = medicalRecordService.getByFullName(firstName + " " + lastName);
            medicalRecordService.remove(medicalRecord);

            if (medicalRecordService.getByFullName(firstName + " " + lastName) == null) {
                LOGGER.info("DELETE /medicalRecord removed.");

                return ResponseEntity.ok(medicalRecord.getFullName());
            }

            throw new RuntimeException("DELETE /medicalRecord medicalRecord not deleted");
        } catch (Exception e) {
            LOGGER.error("DELETE /medicalRecord Error: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(null);
        }
    }
}
