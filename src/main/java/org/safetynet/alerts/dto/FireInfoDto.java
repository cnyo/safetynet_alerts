package org.safetynet.alerts.dto;

import org.safetynet.alerts.dto.person.AddressPersonDto;
import org.safetynet.alerts.model.FireStation;

import java.util.List;

public class FireInfoDto {

    public List<AddressPersonDto> persons;
    public String address;
    public String stationNumber;

    public FireInfoDto(List<AddressPersonDto> persons, FireStation fireStation) {
        this.persons = persons;
        this.address = fireStation.getAddress();
        this.stationNumber = fireStation.getStation();
    }
}
