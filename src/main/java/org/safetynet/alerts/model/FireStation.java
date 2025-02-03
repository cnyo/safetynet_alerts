package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

@Component
public class FireStation {
    private String address;
    private String station;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public boolean isStation(String stationNumber) {
        return this.station == stationNumber;
    }

}
