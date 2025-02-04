package org.safetynet.alerts.dto;

import java.util.List;

//@Getter
public class StationPersonsDto {

    public List<PersonAtStationDto> personDtoList;
    public String station;
    public int adultNumber;
    public int childrenNumber;

    public StationPersonsDto(
            List<PersonAtStationDto> persons, String station, int adultNumber, int childrenNumber
    ) {
        this.personDtoList = persons;
        this.station = station;
        this.adultNumber = adultNumber;
        this.childrenNumber = childrenNumber;
    }

    public List<PersonAtStationDto> getPersonDtoList() {
        return personDtoList;
    }

    public void setPersonDtoList(List<PersonAtStationDto> personDtoList) {
        this.personDtoList = personDtoList;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getAdultNumber() {
        return adultNumber;
    }

    public void setAdultNumber(int adultNumber) {
        this.adultNumber = adultNumber;
    }

    public int getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(int childrenNumber) {
        this.childrenNumber = childrenNumber;
    }
}
