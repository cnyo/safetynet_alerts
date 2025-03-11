package org.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class MedicalRecord {
    private final int MAJORITY_AGE = 18;

    private String firstName;
    private String lastName;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonSerialize(using = com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer.class)
    private LocalDate birthdate;

    private List<String> medications;
    private List<String> allergies;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        MedicalRecord other = (MedicalRecord) object;

        return firstName.equals(other.getFirstName()) && lastName.equals(other.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthdayDate = birthdate;
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public MedicalRecord setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;

        return this;
    }

    public MedicalRecord setBirthdate(String birthdateStr) {
        this.birthdate = LocalDate.parse(birthdateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

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
