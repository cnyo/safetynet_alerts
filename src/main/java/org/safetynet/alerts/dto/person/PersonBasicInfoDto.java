package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.Person;

import java.text.MessageFormat;

//@Data
public class PersonBasicInfoDto {

    public String firstName;
    public String lastName;
    public String address;
    public String phone;

    public PersonBasicInfoDto(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.address = MessageFormat.format("{0}, {1} {2}", person.getAddress(), person.getZip(), person.getCity());
        this.phone = person.getPhone();
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

    public String getPhone() {
        return phone;
    }
}
