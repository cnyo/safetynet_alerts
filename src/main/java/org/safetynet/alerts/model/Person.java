package org.safetynet.alerts.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Person {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String email;
    private String phone;
}
