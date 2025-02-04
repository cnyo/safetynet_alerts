package org.safetynet.alerts.dto;

import java.util.List;

//@Data
public class AddressChildrenDto {

    public List<ResidentChildDto> children;
    public List<ResidentAdultDto> adultAtAdressDto;

    public AddressChildrenDto(List<ResidentChildDto> children, List<ResidentAdultDto> residentAdults) {
        this.children = children;
        this.adultAtAdressDto = residentAdults;
    }
}
