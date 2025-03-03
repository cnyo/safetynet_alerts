package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.Person;
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
    public void getAllPersonsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Boyd")
                .contains("\"address\":\"1509 Culver St");
    }

    @Test
    public void postPersonSuccess() throws Exception {
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
    public void postPersonAlreadyExists() throws Exception {
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
    public void postPersonBadArgumentException() throws Exception {
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
    public void patchPersonSuccess() throws Exception {
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
    public void patchPersonNotFound() throws Exception {
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
    public void patchPersonInvalidArgumentError() throws Exception {
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
    public void deletePersonSuccess() throws Exception {

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
    public void deletePersonFailed() throws Exception {

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
