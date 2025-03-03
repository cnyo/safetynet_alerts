package org.safetynet.alerts.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonDtoMapper personDtoMapper;

    @Autowired
    PersonService personService;

    @Autowired
    FireStationService fireStationService;

    @Autowired
    MedicalRecordService medicalRecordService;

    @Test
    public void getPersonByStationNumberTest_checkAdultNumber_success() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultNumber").value(8))
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andReturn();
    }

    @Test
    public void getPersonByStationNumberTest_checkAdultNumber_throwBadArgument() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultNumber").value(8))
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andReturn();
    }

    @Test
    public void apiController_getPersonByStationNumber_stationDoesNotExist_test() throws Exception {
        MvcResult result = mockMvc.perform(get("/firestation")
                        .param("stationNumber", "10"))
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("None person found");
    }

    @Test
    public void getChildAlertTest_success() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.[0].otherPersons", hasSize(4)))
                .andExpect(jsonPath("$.[1].otherPersons", hasSize(4)))
                .andExpect(jsonPath("$.[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$.[1].address").value("1509 Culver St"))
                .andReturn();
    }

    @Test
    public void getChildAlertTest_noResult() throws Exception {
        mockMvc.perform(get("/childAlert")
                        .param("address", "15 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getChildAlertTest_noResult(String address) throws Exception {
        MvcResult result = mockMvc.perform(get("/childAlert")
                        .param("address", address))
                .andExpect(status().is4xxClientError())
                .andReturn();

        if (address == null) {
            assertThat(result.getResponse().getContentAsString()).isEmpty();
        } else {
            assertThat(result.getResponse().getContentAsString()).isEqualTo("Address cannot be null or empty");
        }
    }

    @Test
    public void getAllPhoneNumberByStationTest_success() throws Exception {
        String PHONE_PATTERN = "\\d{10}|(?:\\d{3}-){2}\\d{4}";
        mockMvc.perform(get("/phoneAlert")
                        .param("fireStation", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(11)))
                .andExpect(jsonPath("$.[*]", everyItem(matchesRegex(PHONE_PATTERN))))
                .andReturn();
    }

    @Test
    public void getAllPhoneNumberByStationTest_stationNotFound() throws Exception {
        mockMvc.perform(get("/phoneAlert")
                        .param("fireStation", "10"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @Test
    public void getAddressPersonsTest_success() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(5)))
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.stationNumber").value(3))
                .andReturn();
    }

    @Test
    public void getAddressPersonsTest_stationNotFoundAtAddress() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "15 Cuver"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsStringIgnoringCase("Fire station not found")))
                .andReturn();
    }

    @Test
    public void getFloodStationTest_success() throws Exception {
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(7)))
                .andExpect(jsonPath("$.[\"748 Townings Dr\"]").exists())
                .andExpect(jsonPath("$.[\"748 Townings Dr\"]", hasSize(2)))
                .andExpect(jsonPath("$.[\"748 Townings Dr\"][0].lastName", containsStringIgnoringCase("Shepard")))
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @ValueSource(strings = {"", " ", "2-3", "15,110"})
    public void getFloodStationTest_stationNotFound(String stations) throws Exception {
        mockMvc.perform(get("/flood/stations")
                        .param("stations", stations))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty())
                .andReturn();
    }

    @Test
    public void getPersonInfoLastNameTest_success() throws Exception {
        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(6)))
                .andExpect(jsonPath("$.[0].address", containsString("1509 Culver St")))
                .andExpect(jsonPath("$.[0].medications.size()", is(2)))
                .andReturn();
    }



    @Test
    public void getPersonInfoLastNameTest_notFound() throws Exception {
        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Boryd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty())
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @ValueSource(strings = {"", " "})
    public void getPersonInfoLastNameTest_badArgument(String lastname) throws Exception {
        mockMvc.perform(get("/personInfo")
                        .param("lastName", lastname))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", containsString("Last name cannot be null or empty")))
                .andReturn();
    }

    @Test
    public void getCommunityEmailTest_success() throws Exception {
        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(23)))
                .andExpect(jsonPath("$.[0]", containsString("jaboyd@email.com")))
                .andReturn();
    }

    @Test
    public void getCommunityEmailTest_notFound() throws Exception {
        mockMvc.perform(get("/communityEmail")
                        .param("city", "Culer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @ValueSource(strings = {"", " "})
    public void getCommunityEmailTest_notFound(String city) throws Exception {
        mockMvc.perform(get("/communityEmail")
                        .param("city", city))
                .andExpect(status().is4xxClientError())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", containsString("City cannot be null or empty")))
                .andReturn();
    }

}
