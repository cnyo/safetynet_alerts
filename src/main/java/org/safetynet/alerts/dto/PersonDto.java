package org.safetynet.alerts.dto;

import lombok.Getter;
import org.safetynet.alerts.model.Person;

@Getter
public class PersonDto {

    public String firstName;
    public String lastName;
    public String address;
    public String city;
    public String zip;
    public String email;
    public String phone;

    public PersonDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.address = person.getAddress();
        this.city = person.getCity();
        this.zip = person.getZip();
        this.email = person.getEmail();
        this.phone = person.getPhone();
    }

}
