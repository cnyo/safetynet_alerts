package org.safetynet.alerts.controller;

import org.safetynet.alerts.dto.StationPersonsDto;
import org.safetynet.alerts.dto.StationPersonsMapper;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {

    private final JsonDataService jsonDataService;

    @Autowired
    private StationPersonsMapper stationPersonsMapper;

    public PersonController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @GetMapping("/firestation")
    public StationPersonsDto getPersonByStationNumber(@RequestParam(required = false, defaultValue = "3") String station_number) {
        List<FireStation> fireStations = jsonDataService.getAllFireStationByStation(station_number);
        List<Person> persons = jsonDataService.getAllPersonFromFireStation(fireStations);

        return stationPersonsMapper.toDto(persons, station_number);
    }
}
