package org.safetynet.alerts.unit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiController;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.PersonByStationNumberDto;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.*;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonDtoMapper personDtoMapper;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private FireStationService fireStationService;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void getPersonByStationNumberShouldReturnDto() throws Exception {
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Boyd");
        person1.setEmail("jboyd@doe.com");
        person1.setAddress("1509 Culver St");
        person1.setCity("Culver");
        person1.setZip("97451");
        person1.setPhone("841-874-7458");

        List<Person> persons = List.of(person1);
        List<String> fullNames = List.of("John Doe");

        PersonBasicInfoDto personBasicInfoDto = new PersonBasicInfoDto(person1);
        PersonByStationNumberDto PersonByStationNumberDto = new PersonByStationNumberDto(
                List.of(personBasicInfoDto),
                "3",
                1,
                1
        );

        given(personService.getAllPersonFromFireStation(anyString())).willReturn(persons);
        given(personService.getFullNamesFromPersons(anyList())).willReturn(fullNames);
        given(personService.countAdultFromPersons(anyList())).willReturn(1);
        given(personService.countChildrenFromPersons(anyList())).willReturn(1);
        given(personDtoMapper.toPersonByStationNumberDto(anyList(), anyString(), anyInt(), anyInt())).willReturn(PersonByStationNumberDto);

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void getPersonByStationNumberWhenNotFoundPersonShouldException() throws Exception {
        given(personService.getAllPersonFromFireStation(anyString())).willReturn(Collections.emptyList());

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("None person found")))
                .andReturn();
    }

    @Test
    public void getPersonByStationNumberWithBadArgumentShouldException() throws Exception {
        given(personService.getAllPersonFromFireStation(anyString())).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getPersonByStationNumberInErrorShouldException() throws Exception {
        given(personService.getAllPersonFromFireStation(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getChildAlertReturnChildAlertDto() throws Exception {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");
        person.setAddress("1509 Culver St");
        ChildAlertDto childALertDto = new ChildAlertDto(person, 15);
        List<ChildAlertDto> childAlerts = List.of(childALertDto);

        given(personService.getChildAlerts(anyString())).willReturn(childAlerts);

        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void getChildAlertReturnWithBadArgumentShouldReturnException() throws Exception {
        given(personService.getChildAlerts(anyString())).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Address cannot be null or empty")))
                .andReturn();
    }

    @Test
    public void getChildAlertReturnInErrorShouldReturnException() throws Exception {
        given(personService.getChildAlerts(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getAllPhoneNumberByStationShouldReturnListPhones() throws Exception {
        List<String> addresses = List.of("1509 Culver St");
        List<String> phones = List.of("841-874-7458");

        given(fireStationService.getAddressesForFireStation(anyString())).willReturn(addresses);
        given(personService.getAllPhoneNumberFromAddresses(anyList())).willReturn(phones);

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStation", "3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("841-874-7458")))
                .andReturn();
    }

    @Test
    public void getAllPhoneNumberByStationWithBadArgumentShouldReturnException() throws Exception {
        given(fireStationService.getAddressesForFireStation(anyString())).willThrow(new IllegalArgumentException("Bad argument"));

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStation", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Bad argument")))
                .andReturn();
    }

    @Test
    public void getAllPhoneNumberByStationInErrorShouldReturnException() throws Exception {
        given(fireStationService.getAddressesForFireStation(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStation", "3"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getAddressPersonsShouldReturnFireInfoDto() throws Exception {
        FireStation fireStation = new FireStation();
        fireStation.setStation("3");
        fireStation.setAddress("1509 Culver St");

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");
        person.setAddress("1509 Culver St");
        person.setPhone("841-874-7458");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Boyd");
        medicalRecord.setBirthdate("08/02/2008");
        medicalRecord.setMedications(Collections.emptyList());
        medicalRecord.setAllergies(Collections.emptyList());

        AddressPersonDto addressPersonDto = new AddressPersonDto(person, medicalRecord);
        List<AddressPersonDto> persons = List.of(addressPersonDto);

        FireInfoDto fireInfoDto = new FireInfoDto(persons, fireStation);

        given(fireStationService.getFireStationAtAddress(anyString())).willReturn(new FireStation());
        given(personService.getAllPersonAtAddress(anyString())).willReturn(new ArrayList<>());
        given(medicalRecordService.getAllByFullName()).willReturn(new HashMap<>());
        given(personService.toFireInfoDto(anyList(), any(FireStation.class), anyMap())).willReturn(fireInfoDto);

        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void getAddressPersonsNotFoundShouldReturnException() throws Exception {
        given(fireStationService.getFireStationAtAddress(anyString())).willThrow(new NoSuchElementException());

        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Fire station not found")))
                .andReturn();
    }

    @Test
    public void getAddressPersonsWithBadArgumentShouldReturnException() throws Exception {
        given(fireStationService.getFireStationAtAddress(anyString())).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getAddressPersonsInErrorShouldReturnException() throws Exception {
        given(fireStationService.getFireStationAtAddress(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getFloodStationShouldReturnFloodStationDto() throws Exception {
        List<String> addresses = List.of("1509 Culver St");
        List<Person> persons = List.of(new Person());

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");
        person.setAddress("1509 Culver St");

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Boyd");
        medicalRecord.setBirthdate("08/02/2008");

        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        medicalRecordMap.put("John Boyd", medicalRecord);

        PersonMedicalInfoDto PersonMedicalInfoDto = new PersonMedicalInfoDto(person, medicalRecord);
        Map<String, List<PersonMedicalInfoDto>> personMedicalInfoDtoMap = new HashMap<>();
        personMedicalInfoDtoMap.put("John Boyd", Collections.singletonList(PersonMedicalInfoDto));

        given(fireStationService.getAddressesForFireStations(anyString())).willReturn(addresses);
        given(personService.getAllPersonFromAddresses(anyList())).willReturn(persons);
        given(medicalRecordService.getAllByFullName()).willReturn(medicalRecordMap);
        given(personDtoMapper.toFloodStationDto(anyList(), anyMap())).willReturn(personMedicalInfoDtoMap);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "2,3"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("John Boyd")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd\"")))
                .andReturn();
    }

    @Test
    public void getFloodStationWithBadArgumentShouldReturnException() throws Exception {
        given(fireStationService.getAddressesForFireStations(anyString())).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "2,3"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getFloodStationInErrorShouldReturnException() throws Exception {
        given(fireStationService.getAddressesForFireStations(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "2,3"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getPersonInfoLastNameShouldReturnDto() throws Exception {
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");
        person.setAddress("1509 Culver St");

        List<Person> persons = List.of(person);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Boyd");
        medicalRecord.setBirthdate("08/02/2008");

        PersonInfoDto personInfoDto = new PersonInfoDto(person, medicalRecord);
        List<PersonInfoDto> personInfos = List.of(personInfoDto);

        given(medicalRecordService.getAllByFullName()).willReturn(medicalRecordMap);
        given(personService.getAllPersonByLastName(anyString())).willReturn(persons);
        given(personDtoMapper.toPersonInfoLastNameDto(anyList(), anyMap())).willReturn(personInfos);

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void getPersonInfoLastNameWithBadArgumentShouldReturnException() throws Exception {
        given(medicalRecordService.getAllByFullName()).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Last name cannot be null or empty")))
                .andReturn();
    }

    @Test
    public void getPersonInfoLastNameWithInErrorShouldReturnException() throws Exception {
        given(medicalRecordService.getAllByFullName()).willThrow(new RuntimeException());

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getCommunityEmailShouldReturnMails() throws Exception {
        given(personService.getAllEmailsAtCity(anyString())).willReturn(List.of("jboyd@email.com"));

        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("jboyd@email.com")))
                .andReturn();
    }

    @Test
    public void getCommunityEmailWithBadArgumentShouldReturnException() throws Exception {
        given(personService.getAllEmailsAtCity(anyString())).willThrow(new IllegalArgumentException());

        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getCommunityEmailInErrorShouldReturnException() throws Exception {
        given(personService.getAllEmailsAtCity(anyString())).willThrow(new RuntimeException());

        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver"))
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}
