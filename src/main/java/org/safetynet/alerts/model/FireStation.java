package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

@Component
public class FireStation {
    private String address;
    private String station;

    public String getAddress() {
        return address;
    }

    public FireStation setAddress(String address) {
        this.address = address;

        return this;
    }

    public String getStation() {
        return station;
    }

    public FireStation setStation(String station) {
        this.station = station;

        return this;
    }

    public String toString() {
        return "FireStation [address=" + address + ", station=" + station + "]";
    }
}
