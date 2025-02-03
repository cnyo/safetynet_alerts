package org.safetynet.alerts.dto;

import lombok.Data;
import org.safetynet.alerts.model.Person;

import java.util.List;

//@Data
public class PersonDto {

    public String firstName;
    public String lastName;
    public String address;
    public String city;
    public String zip;
    public String email;
    public String phone;
    public int age;
    public List<String> medications;
    public List<String> allergies;

    public PersonDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.city = person.getCity();
        this.zip = person.getZip();
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.age = person.getMedicalRecord().getAge();
        this.medications = person.getMedicalRecord().getMedications();
        this.allergies = person.getMedicalRecord().getAllergies();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    public List<String> getMedications() {
        return medications;
    }

    public List<String> getAllergies() {
        return allergies;
    }
}
