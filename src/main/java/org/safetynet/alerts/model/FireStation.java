package org.safetynet.alerts.model;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FireStation {
    private String address;
    private String station;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        FireStation other = (FireStation) object;

        return address.equals(other.getAddress()) && station.equals(other.getStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, station);
    }

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
