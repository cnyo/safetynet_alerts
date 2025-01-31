package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

@Component
public class FireStation {
    private String address;
    private int station;

    public String getAddress() {
        return address;
    }

    public int getStation() {
        return station;
    }

    public boolean isStation(int stationNumber) {
        return this.station == stationNumber;
    }
}
