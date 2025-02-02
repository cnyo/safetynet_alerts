package org.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.safetynet.alerts.model.Person;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Repository
public class PersonRepository extends AbstractBaseRepository {

    public List<Person> findAllByStationNumber(List<String> addresses) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource(JSON_PATH).getFile();
        JsonNode jsonNodePersons = objectMapper.readTree(jsonFile).get("persons");

        List<Person> persons = new ArrayList<>();

        for (JsonNode jsonNodePerson : jsonNodePersons) {

            if (addresses.contains(jsonNodePerson.get("address").asText())) {
                Person person = objectMapper.treeToValue(jsonNodePerson, Person.class);
                persons.add(person);
            }
        };

        return persons;
    }
}
