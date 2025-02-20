package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

@Component
public class Person {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String email;
    private String phone;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;

        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Person setLastName(String lastName) {
        this.lastName = lastName;

        return this;
    }

    public String getAddress() {
        return address;
    }

    public Person setAddress(String address) {
        this.address = address;

        return this;
    }

    public String getCity() {
        return city;
    }

    public Person setCity(String city) {
        this.city = city;

        return this;
    }

    public String getZip() {
        return zip;
    }

    public Person setZip(String zip) {
        this.zip = zip;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;

        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Person setPhone(String phone) {
        this.phone = phone;

        return this;
    }
}
