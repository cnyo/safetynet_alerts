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

    public JsonData addPerson(Person person) {
        if (persons != null) {
            persons.add(person);
        }

        return this;
    }

    public JsonData removePerson(Person person) {
        if (persons != null) {
            persons.remove(person);
        }

        return this;
    }

    public JsonData addFireStation(FireStation fireStation) {
        if (fireStation != null) {
            firestations.add(fireStation);
        }

        return this;
    }

    public JsonData removeFireStation(FireStation fireStation) {
        if (firestations != null) {
            firestations.remove(fireStation);
        }

        return this;
    }

    public JsonData addMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalrecords != null) {
            medicalrecords.add(medicalRecord);
        }

        return this;
    }

    public JsonData removeMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalrecords != null) {
            medicalrecords.remove(medicalRecord);
        }

        return this;
    }
}
