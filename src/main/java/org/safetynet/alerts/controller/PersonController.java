package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.PersonDto;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping("/firestation")
    public PersonDto getPersonByStationNumber(@RequestParam(required = false, defaultValue = "3") int stationNumber) throws IOException {
        return personService.getPersonByStation(stationNumber);
    }
}
