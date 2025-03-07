package org.safetynet.alerts.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiPersonController;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiPersonController.class)
public class ApiPersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    private Person nonExistentPerson;
    private Person existingPerson;

    @BeforeEach
    void setUp() {
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

    @Test
    public void getAllPersonsShouldReturnResponseWithPersons() throws Exception {
        List<Person> persons = Collections.singletonList(existingPerson);

        given(personService.getAll()).willReturn(persons);

        mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void getAllPersonsShouldReturnConflict() throws Exception {
        given(personService.getAll()).willThrow(new RuntimeException());

         mockMvc.perform(MockMvcRequestBuilders.get("/person/all"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();
    }

    @Test
    public void postPersonShouldReturnCreatedPerson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.create(any(Person.class))).willReturn(nonExistentPerson);

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(nonExistentPerson))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Doe")))
                .andReturn();
    }

    @Test
    public void postAlreadyExistsPersonShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.create(any(Person.class))).willThrow(new InstanceAlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn();
    }

    @Test
    public void postPersonWithBadArgumentShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.create(any(Person.class))).willThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void postPersonErrorShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.create(any(Person.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }


    @Test
    public void patchPersonShouldReturnPatchedPerson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        existingPerson.setPhone("841-500-6512");

        given(personService.update(any(Person.class))).willReturn(existingPerson);

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
    public void patchNotFoundPersonShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.update(any(Person.class))).willThrow(new InstanceNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    public void patchPersonWithBadArgumentShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.update(any(Person.class))).willThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void patchPersonInErrorShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(personService.update(any(Person.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(existingPerson))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void deletePersonShouldReturnSuccess() throws Exception {
        given(personService.remove(anyString(), anyString())).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", anyString())
                        .param("lastName", anyString())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("Person removed successfully")))
                .andReturn();
    }

    @Test
    public void deletePersonNotRemovedShouldReturnException() throws Exception {
        given(personService.remove(anyString(), anyString())).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", anyString())
                        .param("lastName", anyString())
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string(containsString("person not removed")))
                .andReturn();
    }

    @Test
    public void deletePersonInErrorShouldReturnException() throws Exception {
        given(personService.remove(anyString(), anyString())).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", anyString())
                        .param("lastName", anyString())
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }
}
