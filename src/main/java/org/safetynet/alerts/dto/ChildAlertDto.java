package org.safetynet.alerts.dto;

import org.safetynet.alerts.dto.person.AdultPersonDto;
import org.safetynet.alerts.dto.person.ChildPersonDto;

import java.util.List;

//@Data
public class ChildAlertDto {

    public List<ChildPersonDto> children;
    public List<AdultPersonDto> adultAtAdressDto;

    public ChildAlertDto(List<ChildPersonDto> children, List<AdultPersonDto> residentAdults) {
        this.children = children;
        this.adultAtAdressDto = residentAdults;
    }
}
