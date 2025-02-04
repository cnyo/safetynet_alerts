package org.safetynet.alerts.dto;

import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressChildrenMapper {

    public AddressChildrenDto toDto(List<Person> children, List<Person> adult) {
        List<ResidentChildDto> childrenDto = children.stream().map(ResidentChildDto::new).collect(Collectors.toList());
        List<ResidentAdultDto> adultDto = adult.stream().map(ResidentAdultDto::new).collect(Collectors.toList());

        return new AddressChildrenDto(childrenDto, adultDto);
    }
}
