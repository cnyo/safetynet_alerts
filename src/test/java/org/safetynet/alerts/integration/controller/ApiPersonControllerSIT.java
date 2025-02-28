package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiPersonController;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = { ApiPersonController.class, PersonService.class })
@ExtendWith(SpringExtension.class)
public class ApiPersonControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private FireStationService fireStationService;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @MockitoBean
    private PersonDtoMapper personDtoMapper;

    private Person mockPerson;

    @BeforeEach
    void setUp() {
        mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        mockPerson.setEmail("jdoe@doe.com");
        mockPerson.setAddress("21 jump street");
        mockPerson.setCity("Culver");
        mockPerson.setZip("97451");
        mockPerson.setPhone("841-874-7458");
    }

    @Test
    public void getAllPersonsTest_success() throws Exception {
        List<Person> mockPersons = List.of(mockPerson);

        when(personRepository.findAll()).thenReturn(mockPersons);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe")
                .contains("\"address\":\"21 jump street");
    }

    @Test
    public void postPersonTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        when(personRepository.create(any(Person.class))).thenReturn(mockPerson);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
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

        when(personRepository.create(any(Person.class))).thenThrow(new InstanceAlreadyExistsException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person already exists");
    }

    @Test
    public void postPersonTest_badArgumentException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mockPerson.setFirstName("");
        mockPerson.setLastName("");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid person data");
    }

    @Test
    public void patchPersonTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        when(personRepository.update(any(Person.class))).thenReturn(mockPerson);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe")
                .contains("\"phone\":\"841-874-7458");
    }

    @Test
    public void patchPersonTest_notFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        when(personRepository.update(any(Person.class))).thenThrow(new InstanceNotFoundException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person not found");
    }

    @Test
    public void patchPersonTest_invalidArgumentError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        when(personRepository.update(any(Person.class))).thenThrow(new IllegalArgumentException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid person data");
    }

    @Test
    public void deletePersonTest_success() throws Exception {
        when(personRepository.remove(anyString())).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person removed successfully");
    }

    @Test
    public void deletePersonTest_failed() throws Exception {
        when(personRepository.remove(anyString())).thenReturn(false);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("person not removed");
    }
}
