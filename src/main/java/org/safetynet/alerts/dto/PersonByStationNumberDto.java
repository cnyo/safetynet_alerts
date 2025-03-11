package org.safetynet.alerts.dto;

import org.safetynet.alerts.dto.person.PersonBasicInfoDto;

import java.util.List;

public class PersonByStationNumberDto {

    public List<PersonBasicInfoDto> persons;
    public String station;
    public int adultNumber;
    public int childrenNumber;

    public PersonByStationNumberDto(
            List<PersonBasicInfoDto> persons, String station, int adultNumber, int childrenNumber
    ) {
        this.persons = persons;
        this.station = station;
        this.adultNumber = adultNumber;
        this.childrenNumber = childrenNumber;
    }
}
