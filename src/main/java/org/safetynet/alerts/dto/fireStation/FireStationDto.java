package org.safetynet.alerts.dto.fireStation;

import org.safetynet.alerts.model.FireStation;

public class FireStationDto {
    public String address;
    public String station;

    public FireStationDto(FireStation fireStation) {
        this.address = fireStation.getAddress();
        this.station = fireStation.getStation();
    }

}
