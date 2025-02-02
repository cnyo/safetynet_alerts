package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.PersonDto;
import org.safetynet.alerts.dto.StationPersonDto;
import org.safetynet.alerts.dto.PersonMapper;
import org.safetynet.alerts.dto.PersonStationMapper;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonStationMapper personStationMapper;

    public List<PersonDto> getAllByAddresses(List<String> addresses) throws IOException {
        List<Person> persons = personRepository.findAllByStationNumber(addresses);

        return persons.stream().map(personMapper::personToDto).toList();
    }

    public StationPersonDto getStationPersonDto(List<PersonDto> persons, String stationNumber) {
        return personStationMapper.personStationToDto(persons, stationNumber);
    }

}
