package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.ChildAlertDto;
import org.safetynet.alerts.dto.person.AdultPersonDto;
import org.safetynet.alerts.dto.person.ChildPersonDto;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChildAlertDtoMapper {

    public ChildAlertDto toDto(List<Person> children, List<Person> adult) {
        List<ChildPersonDto> childrenDto = children.stream().map(ChildPersonDto::new).collect(Collectors.toList());
        List<AdultPersonDto> adultDto = adult.stream().map(AdultPersonDto::new).collect(Collectors.toList());

        return new ChildAlertDto(childrenDto, adultDto);
    }
}
