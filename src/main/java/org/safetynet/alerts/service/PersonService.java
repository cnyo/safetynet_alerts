package org.safetynet.alerts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.constants.AgeGroupConstants;
import org.safetynet.alerts.dto.person.AdultPersonDto;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.dto.person.OtherPersonDto;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final FireStationRepository fireStationRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public Person createPerson(Person person) {

        return personRepository.createPerson(person);
    }

    public Person updatePerson(Person person, Person currentPerson) {
        return personRepository.updatePerson(person, currentPerson);
    }

    public List<String> getAllPhoneNumberFromAddresses(List<String> addresses) {
        List<String> phoneNumbers = personRepository.findPhoneNumbersFromAddresses(addresses);
        log.debug("{} phone numbers found", phoneNumbers.size());

        return phoneNumbers;
    }

    public Integer countAdultFromPersons(List<String> fullNames) {
        int adultNbr = medicalRecordRepository.countAdultFromFullName(fullNames);
        log.debug("Count {} adults", adultNbr);

        return adultNbr;
    }

    public int countChildrenFromPersons(List<String> fullNames) {
        int childrenNbr = medicalRecordRepository.countChildrenFromFullName(fullNames);
        log.debug("Count {} children", childrenNbr);

        return childrenNbr;
    }

    public List<Person> getAllPersonAtAddress(String address) {
        List<Person> persons = personRepository.findAllPersonAtAddress(address);
        log.debug("{} persons found at address {}", persons.size(), address);

        return persons;
    }

    public List<Person> getAllPersonByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        List<Person> persons = Optional.ofNullable(personRepository.findAllPersonByLastName(lastName))
                .orElse(Collections.emptyList());

        log.debug("Found {} persons", persons.size());

        return persons;
    }

    public Person getPersonByFullName(String fullName) {
        return personRepository.findOneByFullName(fullName);
    }

    public List<Person> getAllPersonFromFireStation(String stationNumber) {
        List <String> addresses = Optional.ofNullable(fireStationRepository.findAllAddressForOneStation(stationNumber))
                .orElse(Collections.emptyList());
        log.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

        List <Person> persons = Optional.ofNullable(personRepository.findAllPersonFromAddresses(addresses))
                .orElse(Collections.emptyList());
        log.debug("Found {} persons for station {}", persons.size(), stationNumber);

        return persons;
    }

    public List<Person> getAllPersonFromAddresses(List<String> addresses) {
        List<Person> persons = personRepository.findAllPersonFromAddresses(addresses);
        log.debug("Found {} persons for addresses {}", persons.size(), addresses);

        return persons;
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public boolean remove(String firstName, String lastName) {
        String fullName = String.format("%s %s", firstName, lastName);

        return personRepository.remove(fullName);
    }

    public List<String> getFullNamesFromPersons(List<Person> persons) {
        if (persons == null || persons.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> fullNames = persons.stream().map(Person::getFullName).toList();
        log.debug("Found {} full names", fullNames.size());

        return fullNames;
    }

//    public Map<String, List<Person>> getAdultsAndChildrenAtAddress(String address, Map<String, MedicalRecord> medicalRecordMap) {
//        List<Person> adults = new ArrayList<>();
//        List<Person> children = new ArrayList<>();
//
//        List<Person> persons = personRepository.findAllPersonAtAddress(address);
//
//        for (Person person : persons) {
//            MedicalRecord medicalRecord = medicalRecordMap.get(person.getFullName());
//            if (medicalRecord == null) {
//                continue;
//            }
//            if (medicalRecord.isAdult()) {
//                adults.add(person);
//            } else if (medicalRecord.isChild()) {
//                children.add(person);
//            }
//        }
//
//        Map<String, List<Person>> personByAgeGroup = new HashMap<>();
//        personByAgeGroup.put("adults", adults);
//        personByAgeGroup.put("children", children);
//        log.debug("Counted {} adults and {} children",
//                personByAgeGroup.get(AgeGroupConstants.ADULTS).size(), personByAgeGroup.get(AgeGroupConstants.CHILDREN).size());
//
//        return personByAgeGroup;
//    }

    public List<String> getAllEmailsAtCity(String city) {
        if (city == null || city.isEmpty()) {
            log.debug("City name cannot be null or empty");

            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        return personRepository.findAllEmailsAtCity(city);
    }

    public List<Person> getChildrenAtAddress(String address, Map<String, MedicalRecord> medicalRecordMap) {
        List<Person> persons = personRepository.findAllPersonAtAddress(address);
        log.debug("Found {} persons at address {}", persons.size(), address);

        return persons;
    }

    public List<ChildAlertDto> attachOtherPersonToChildAlertDto(List<Person> persons, Map<String, ChildAlertDto> childAlerts, String address) {
        Map<String, List<Person>> otherMap = new HashMap<>();
        List<AdultPersonDto> other = new ArrayList<>();

        persons.forEach(person -> {
            int otherPersonCount = 0;

            for (Map.Entry<String, ChildAlertDto> entry : childAlerts.entrySet()) {
                if (!entry.getKey().equals(person.getFullName())) {
                    entry.getValue().otherPersons.add(new OtherPersonDto(person));
                    otherPersonCount++;
                }
            }
            log.debug("{} other person(s) household added for ChildAlertDto at address {}", otherPersonCount, address);
        });
        log.debug("ChildPersonDto mapped for {} children at address {}", childAlerts.size(), address);

        return new ArrayList<>(childAlerts.values());
    }
}