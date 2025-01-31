package org.safetynet.alerts.service;

import org.safetynet.alerts.dto.PersonDto;
import org.safetynet.alerts.dto.PersonMapper;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public PersonDto getPersonByStation(int stationNumber) throws IOException {
        List<Person> persons = personRepository.findAllByStationNumber(stationNumber);
        PersonDto personDto = null;

        for (Person person : persons) {
//            PersonMapper.INSTANCE.personToDto(person);
        }

        return personDto;
    }
}
