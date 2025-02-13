package org.safetynet.alerts.model;

import java.util.List;

public class JsonData {
    List<Person> persons;
    List<FireStation> firestations;
    List<MedicalRecord> medicalrecords;

    public List<Person> getPersons() {
        return persons;
    }

    public JsonData setPersons(List<Person> persons) {
        this.persons = persons;

        return this;
    }

    public List<FireStation> getFirestations() {
        return firestations;
    }

    public void setFirestations(List<FireStation> firestations) {
        this.firestations = firestations;
    }

    public List<MedicalRecord> getMedicalrecords() {
        return medicalrecords;
    }

    public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
        this.medicalrecords = medicalrecords;
    }
}
