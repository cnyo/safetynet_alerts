package org.safetynet.alerts.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StationPersonDto {

    public List<PersonDto> personDtoList;
    public String station;
    public int adultNumber;
    public int childrenNumber;

    public StationPersonDto(
            List<PersonDto> persons, String station, int adultNumber, int childrenNumber
    ) {
        this.personDtoList = persons;
        this.station = station;
        this.adultNumber = adultNumber;
        this.childrenNumber = childrenNumber;
    }

}
