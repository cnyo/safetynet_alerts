package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiPersonControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private PersonDtoMapper personDtoMapper;

    private Person mockNonExistentPerson;
    private Person mockExistingPerson;

    @BeforeEach
    void setUp() {
        mockNonExistentPerson = new Person();
        mockNonExistentPerson.setFirstName("John");
        mockNonExistentPerson.setLastName("Doe");
        mockNonExistentPerson.setEmail("jdoe@doe.com");
        mockNonExistentPerson.setAddress("21 jump street");
        mockNonExistentPerson.setCity("Culver");
        mockNonExistentPerson.setZip("97451");
        mockNonExistentPerson.setPhone("841-874-7458");

        mockExistingPerson = new Person();
        mockExistingPerson.setFirstName("John");
        mockExistingPerson.setLastName("Boyd");
        mockExistingPerson.setEmail("jaboyd@email.com");
        mockExistingPerson.setAddress("1509 Culver St");
        mockExistingPerson.setCity("Culver");
        mockExistingPerson.setZip("97451");
        mockExistingPerson.setPhone("841-874-6512");
    }

    @Test
    public void getAllPersonsTest_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Boyd")
                .contains("\"address\":\"1509 Culver St");
    }

    @Test
    public void postPersonTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockNonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe");
    }

    @Test
    public void postPersonTest_alreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockExistingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person already exists");
    }

    @Test
    public void postPersonTest_badArgumentException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockNonExistentPerson.setFirstName("");
        mockNonExistentPerson.setLastName("");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockNonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid person data");
    }

    @Test
    public void patchPersonTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockExistingPerson.setPhone("841-500-6512");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockExistingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Boyd")
                .contains("\"phone\":\"841-500-6512");
    }

    @Test
    public void patchPersonTest_notFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockNonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person not found");
    }

    @Test
    public void patchPersonTest_invalidArgumentError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockNonExistentPerson.setLastName("");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockNonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid person data");
    }

    @Test
    public void deletePersonTest_success() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", mockExistingPerson.getFirstName())
                        .param("lastName", mockExistingPerson.getLastName())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person removed successfully");
    }

    @Test
    public void deletePersonTest_failed() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", mockNonExistentPerson.getFirstName())
                        .param("lastName", mockNonExistentPerson.getLastName())
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("person not removed");
    }
}
