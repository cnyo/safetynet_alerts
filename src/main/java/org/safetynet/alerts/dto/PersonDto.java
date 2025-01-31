package org.safetynet.alerts.dto;

import lombok.Data;

@Data
public class PersonDto {

    String firstName;
    String lastName;
    String address;
    String city;
    String zip;
    String phone;
    String email;
//    int age;
//    String medications;
//    String allergies;

    public PersonDto(String firstName, String lastName, String address, String city, String zip, String phone, String email, int age, String medications, String allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
//        this.age = age;
//        this.medications = medications;
//        this.allergies = allergies;
    }

}
