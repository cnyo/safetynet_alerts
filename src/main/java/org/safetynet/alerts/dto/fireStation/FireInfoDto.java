package org.safetynet.alerts.dto.fireStation;

import org.safetynet.alerts.dto.person.AddressPersonDto;
import org.safetynet.alerts.model.FireStation;

import java.util.List;

//@Data
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
