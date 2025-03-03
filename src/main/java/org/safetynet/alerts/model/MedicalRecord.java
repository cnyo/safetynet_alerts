package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MedicalRecord {
    private final int MAJORITY_AGE = 18;

    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthdayDate = LocalDate.parse(birthdate, formatter);;
        LocalDate now = LocalDate.now();
        Period period = Period.between(birthdayDate, now);

        return period.getYears();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public MedicalRecord setFirstName(String firstName) {
        this.firstName = firstName;

        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public MedicalRecord setLastName(String lastName) {
        this.lastName = lastName;

        return this;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public MedicalRecord setBirthdate(String birthdate) {
        this.birthdate = birthdate;

        return this;
    }

    public List<String> getMedications() {
        return medications;
    }

    public MedicalRecord setMedications(List<String> medications) {
        this.medications = medications;

        return this;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public MedicalRecord setAllergies(List<String> allergies) {
        this.allergies = allergies;

        return this;
    }

    public boolean isChild() {
        return getAge() <= MAJORITY_AGE;
    }

    public boolean isAdult() {
        return getAge() > MAJORITY_AGE;
    }
}
