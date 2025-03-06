package org.safetynet.alerts.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.dto.person.PersonInfoDto;
import org.safetynet.alerts.dto.person.PersonMedicalInfoDto;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class PersonDtoMapperTest {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonDtoMapper personDtoMapper;

    private static JsonData jsonData;

    @BeforeAll
    static void setUpClass() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPath = "data.json";

        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void toPersonByStationNumberDtoShouldReturnDto() {
        int adultNbr = 4;
        int childrenNbr = 2;
        String station = "3";

        PersonByStationNumberDto dto = personDtoMapper.toPersonByStationNumberDto(jsonData.getPersons(), station, adultNbr, childrenNbr);

        assertThat(dto).isNotNull();
        assertThat(dto).isInstanceOf(PersonByStationNumberDto.class);
        assertThat(dto.persons.size()).isEqualTo(23);
        assertThat(dto.adultNumber).isEqualTo(adultNbr);
        assertThat(dto.childrenNumber).isEqualTo(childrenNbr);
        assertThat(dto.station).isEqualTo(station);
    }

    @Test
    public void toFloodStationDtoShouldReturnDto() {
        Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();

        Map<String, List<PersonMedicalInfoDto>> dtoByAddress = personDtoMapper.toFloodStationDto(jsonData.getPersons(), medicalRecordMap);

        assertThat(dtoByAddress).isNotNull();
        assertThat(dtoByAddress.size()).isEqualTo(11);
        assertThat(dtoByAddress.containsKey("748 Townings Dr")).isTrue();
        assertThat(dtoByAddress.get("748 Townings Dr").size()).isEqualTo(2);
        assertThat(dtoByAddress.get("748 Townings Dr").getFirst()).isInstanceOf(PersonMedicalInfoDto.class);
        assertThat(dtoByAddress.get("748 Townings Dr").getFirst().lastName).isEqualTo("Shepard");
    }

    @Test
    public void toPersonInfoLastNameDtoShouldReturnDto() {
        Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();

        List<PersonInfoDto> dtoList = personDtoMapper.toPersonInfoLastNameDto(jsonData.getPersons(), medicalRecordMap);

        assertThat(dtoList).isNotNull();
        assertThat(dtoList.size()).isEqualTo(23);
        assertThat(dtoList.getFirst()).isInstanceOf(PersonInfoDto.class);
        assertThat(dtoList.getFirst().age).isEqualTo(41);
        assertThat(dtoList.getFirst().medications.size()).isEqualTo(2);
        assertThat(dtoList.getFirst().allergies.size()).isEqualTo(1);
    }

    @Test
    public void toChildAlertDtoShouldReturnDto() {
        Map<String, MedicalRecord> medicalRecordMap = medicalRecordService.getAllByFullName();
        List<Person> persons = personService.getAllPersonAtAddress("1509 Culver St");

        Map<String, ChildAlertDto> childAlerts = personDtoMapper.toChildAlertDto(persons, medicalRecordMap);

        assertThat(childAlerts).isNotNull();
        assertThat(childAlerts.size()).isEqualTo(2);
        assertThat(childAlerts.containsKey("Tenley Boyd")).isTrue();
        assertThat(childAlerts.get("Tenley Boyd").address).isEqualTo("1509 Culver St");
        assertThat(childAlerts.get("Tenley Boyd").age).isEqualTo(13);
        assertThat(childAlerts.containsKey("Roger Boyd")).isTrue();
        assertThat(childAlerts.get("Roger Boyd").address).isEqualTo("1509 Culver St");
        assertThat(childAlerts.get("Roger Boyd").age).isEqualTo(7);
    }

    @Test
    public void withSameChildToChildAlertDtoShouldReturnDtoWithOnceChild() {
        Person child = new Person();
        child.setFirstName("John");
        child.setLastName("Child");
        child.setCity("Paris");
        child.setEmail("john.doe@gmail.com");
        child.setAddress("21 jump street");;
        child.setZip("75019");
        child.setPhone("841-874-9888");

        MedicalRecord medicalRecord1 = new MedicalRecord();
        String birthdate = LocalDate.now().minusYears(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        medicalRecord1.setFirstName(child.getFirstName());
        medicalRecord1.setLastName(child.getLastName());
        medicalRecord1.setBirthdate(birthdate);

        List<Person> persons = List.of(child, child);
        Map<String, MedicalRecord> medicalRecords = Map.of(child.getFullName(), medicalRecord1);

        Map<String, ChildAlertDto> childAlerts = personDtoMapper.toChildAlertDto(persons, medicalRecords);

        assertThat(childAlerts).isNotNull();
        assertThat(childAlerts.size()).isEqualTo(1);
    }

}
