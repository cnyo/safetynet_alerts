package org.safetynet.alerts.model;

import java.util.List;

public class JsonData {
    List<Person> persons;
    List<FireStation> fireStations;
    List<MedicalRecord> medicalRecords;

    public List<Person> getPersons() {
        return persons;
    }

    public JsonData setPersons(List<Person> persons) {
        this.persons = persons;

        return this;
    }

    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public void setFireStations(List<FireStation> fireStations) {
        this.fireStations = fireStations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public JsonData removeMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecords != null) {
            medicalRecords.remove(medicalRecord);
        }

        return this;
    }
}
