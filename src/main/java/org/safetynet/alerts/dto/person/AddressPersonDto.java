package org.safetynet.alerts.dto.person;

import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;

import java.util.ArrayList;
import java.util.List;

public class AddressPersonDto {

    public String lastName;
    public String phoneNumber;
    public int age;
    public List<String> medications;
    public List<String> allergies;

    public AddressPersonDto(Person person, MedicalRecord medicalRecord) {
        this.lastName = person.getLastName();
        this.phoneNumber = person.getPhone();
        this.age = medicalRecord.getAge();
        this.medications = new ArrayList<>(medicalRecord.getMedications());
        this.allergies = new ArrayList<>(medicalRecord.getAllergies());
    }
}
