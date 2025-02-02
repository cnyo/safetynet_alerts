package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.PersonDto;
import org.safetynet.alerts.dto.StationPersonDto;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class PersonController {
    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestation")
    public StationPersonDto getPersonByStationNumber(@RequestParam(required = false, defaultValue = "3") String station_number) throws IOException {
        StationPersonDto stationPersons = null;

        try {
            List<String> addresses = fireStationService.getAllAddressFromStationNumber(station_number);
            List<PersonDto> persons = personService.getAllByAddresses(addresses);
            stationPersons = personService.getStationPersonDto(persons, station_number);
        } catch (Exception e) {
            // todo : Log
        }

        return stationPersons;
    }
}
