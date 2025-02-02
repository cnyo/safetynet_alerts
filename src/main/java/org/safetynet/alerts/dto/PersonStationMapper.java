package org.safetynet.alerts.dto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonStationMapper {

    public StationPersonDto personStationToDto(List<PersonDto> persons, String stationNumber) {
        return new StationPersonDto(persons, stationNumber, 0, 0);
    }
}
