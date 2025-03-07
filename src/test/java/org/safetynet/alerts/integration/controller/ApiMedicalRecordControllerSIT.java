package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiMedicalRecordControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicalRecordService medicalRecordService;

    private JsonData jsonData;

    @BeforeEach
    void setUp() {
        jsonData = JsonDataService.getJsonData();
    }

    @AfterEach
    public void tearDown() {
        JsonDataService.init("data.json");
    }

    @Test
    public void getAllMedicalRecordsTest_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.size()").value(23))
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"birthdate\":\"03/06/1984")))
                .andReturn();
    }

    @Test
    public void postMedicalRecordTest_success() throws Exception {
        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(23);

        // Add person for new MedicalRecord
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        jsonData.getPersons().add(person);

        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1985");
        mockMedicalRecord.setMedications(Collections.emptyList());
        mockMedicalRecord.setAllergies(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Doe")))
                .andExpect(content().string(containsString("\"birthdate\":\"03/06/1985")))
                .andReturn();

        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(24);
    }

    @Test
    public void postMedicalRecordTest_alreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Boyd");
        mockMedicalRecord.setBirthdate("03/06/1994");
        mockMedicalRecord.setMedications(Collections.emptyList());
        mockMedicalRecord.setAllergies(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("MedicalRecord already exists for person")))
                .andReturn();
    }

    @Test
    public void postMedicalRecordTest_personNotExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("Jane");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("09/05/1980");
        mockMedicalRecord.setMedications(Collections.emptyList());
        mockMedicalRecord.setAllergies(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Person for new medical record not exists")))
                .andReturn();
    }

    @Test
    public void patchMedicalRecordTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        List<String> medications = List.of("doliprane:1000mg");
        List<String> allergies = List.of("Chat");

        params.put("firstName", "John");
        params.put("lastName", "Boyd");
        params.put("birthdate", "01/06/1995");
        params.put("medications", medications);
        params.put("allergies", allergies);

        mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"John")))
                .andExpect(content().string(containsString("\"lastName\":\"Boyd")))
                .andExpect(content().string(containsString("\"birthdate\":\"01/06/1995")))
                .andExpect(content().string(containsString("[\"doliprane:1000mg\"]")))
                .andExpect(content().string(containsString("[\"Chat\"]")))
                .andReturn();

        MedicalRecord updatedMedicalRecord = medicalRecordService.getOneByName("John", "Boyd");

        // Check if post medicalRecord list is updated success
        assertThat(updatedMedicalRecord).isNotNull();
        assertThat(updatedMedicalRecord.getBirthdate()).isEqualTo("01/06/1995");
        assertThat(updatedMedicalRecord.getAllergies().getFirst()).isEqualTo("Chat");
        assertThat(updatedMedicalRecord.getMedications().getFirst()).isEqualTo("doliprane:1000mg");
    }

    @Test
    public void patchMedicalRecordTestNotFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        List<String> medications = List.of("doliprane:1000mg");
        List<String> allergies = List.of("Chat");

        params.put("firstName", "John");
        params.put("lastName", "Doe");
        params.put("birthdate", "03/06/1994");
        params.put("medications", medications);
        params.put("allergies", allergies);

        mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("MedicalRecord to update not found")))
                .andReturn();
    }

    @Test
    public void deleteMedicalRecordTest_success() throws Exception {
        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(23);

        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Boyd")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("medicalRecord removed success")))
                .andReturn();

        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(22);
    }

    @Test
    public void deleteMedicalRecordTest_notDeletedError() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("MedicalRecord not deleted")))
                .andReturn();
    }

}
