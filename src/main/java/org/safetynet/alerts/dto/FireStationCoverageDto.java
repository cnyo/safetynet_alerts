package org.safetynet.alerts.dto;

import org.safetynet.alerts.dto.person.PersonBasicInfoDto;

import java.util.List;

//@Getter
public class FireStationCoverageDto {

    public List<PersonBasicInfoDto> persons;
    public String station;
    public int adultNumber;
    public int childrenNumber;

    public FireStationCoverageDto(
            List<PersonBasicInfoDto> persons, String station, int adultNumber, int childrenNumber
    ) {
        this.persons = persons;
        this.station = station;
        this.adultNumber = adultNumber;
        this.childrenNumber = childrenNumber;
    }

    public List<PersonBasicInfoDto> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonBasicInfoDto> persons) {
        this.persons = persons;
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
