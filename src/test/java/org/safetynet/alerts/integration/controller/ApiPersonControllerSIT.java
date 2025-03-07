package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiPersonControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    private JsonData jsonData;
    private Person nonExistentPerson;
    private Person existingPerson;

    @BeforeEach
    void setUp() {
        jsonData = JsonDataService.getJsonData();

        nonExistentPerson = new Person();
        nonExistentPerson.setFirstName("John");
        nonExistentPerson.setLastName("Doe");
        nonExistentPerson.setEmail("jdoe@doe.com");
        nonExistentPerson.setAddress("21 jump street");
        nonExistentPerson.setCity("Culver");
        nonExistentPerson.setZip("97451");
        nonExistentPerson.setPhone("841-874-7458");

        existingPerson = new Person();
        existingPerson.setFirstName("John");
        existingPerson.setLastName("Boyd");
        existingPerson.setEmail("jaboyd@email.com");
        existingPerson.setAddress("1509 Culver St");
        existingPerson.setCity("Culver");
        existingPerson.setZip("97451");
        existingPerson.setPhone("841-874-6512");
    }

    @AfterEach
    public void tearDown() {
        JsonDataService.init("data.json");
    }

    @Test
    public void getAllPersonsSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void postPersonSuccess() throws Exception {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Doe")))
                .andReturn();

        assertThat(jsonData.getPersons().size()).isEqualTo(24);
    }

    @Test
    public void postPersonAlreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Person already exists")))
                .andReturn();
    }

    @Test
    public void postPersonBadArgumentException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        nonExistentPerson.setFirstName("");
        nonExistentPerson.setLastName("");

         mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Invalid person data")))
                .andReturn();
    }

    @Test
    public void patchPersonSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        existingPerson.setPhone("841-500-6512");

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"phone\":\"841-500-6512")))
                .andReturn();
    }

    @Test
    public void patchPersonNotFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Person not found")))
                .andReturn();
    }

    @Test
    public void patchPersonInvalidArgumentError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        nonExistentPerson.setLastName("");

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Invalid person data")))
                .andReturn();
    }

    @Test
    public void deletePersonSuccess() throws Exception {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", existingPerson.getFirstName())
                        .param("lastName", existingPerson.getLastName())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("Person removed successfully")))
                .andReturn();

        assertThat(jsonData.getPersons().size()).isEqualTo(22);
    }

    @Test
    public void deletePersonFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", nonExistentPerson.getFirstName())
                        .param("lastName", nonExistentPerson.getLastName())
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("person not removed")))
                .andReturn();
    }
}
