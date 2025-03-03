package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.MedicalRecord;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiMedicalRecordControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Test
    public void getAllMedicalRecordsTest_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.size()").value(23))
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"Warren")
                .contains("\"lastName\":\"Zemicks")
                .contains("\"birthdate\":\"03/06/1985");

    }

    @Test
    public void postMedicalRecordTest_success() throws Exception {
        // Deletes medical record to be able to create an existing person
        medicalRecordService.remove("Eric", "Cadigan");

        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("Eric");
        mockMedicalRecord.setLastName("Cadigan");
        mockMedicalRecord.setBirthdate("08/05/1950");
        mockMedicalRecord.setMedications(Collections.emptyList());
        mockMedicalRecord.setAllergies(Collections.emptyList());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"Eric")
                .contains("\"lastName\":\"Cadigan")
                .contains("\"birthdate\":\"08/05/1950");

        // Check if post medicalRecord list is updated success
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(getResult.getResponse().getContentAsString())
                .contains("\"firstName\":\"Eric")
                .contains("\"lastName\":\"Cadigan")
                .contains("\"birthdate\":\"08/05/1950");
    }

    @Test
    public void postMedicalRecordTest_alreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("Clive");
        mockMedicalRecord.setLastName("Ferguson");
        mockMedicalRecord.setBirthdate("03/06/1994");
        mockMedicalRecord.setMedications(Collections.emptyList());
        mockMedicalRecord.setAllergies(Collections.emptyList());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("MedicalRecord already exists for person");
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Person for new medical record not exists");
    }

    @Test
    public void patchMedicalRecordTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        List<String> medications = List.of("doliprane:1000mg");
        List<String> allergies = List.of("Chat");

        params.put("firstName", "John");
        params.put("lastName", "Boyd");
        params.put("birthdate", "01/06/1994");
        params.put("medications", medications);
        params.put("allergies", allergies);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Boyd")
                .contains("\"birthdate\":\"03/06/1994")
                .contains("[\"doliprane:1000mg\"]")
                .contains("[\"Chat\"]");

        // Check if post medicalRecord list is updated success
        MedicalRecord updatedMedicalRecord = medicalRecordService.getOneByName("John", "Boyd");

        assertThat(updatedMedicalRecord).isNotNull();
        assertThat(updatedMedicalRecord.getBirthdate()).isEqualTo("01/06/1994");
        assertThat(updatedMedicalRecord.getAllergies().getFirst()).isEqualTo("Chat");
        assertThat(updatedMedicalRecord.getMedications().getFirst()).isEqualTo("doliprane:1000mg");
    }

    @Test
    public void patchMedicalRecordTest_notFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        List<String> medications = List.of("doliprane:1000mg");
        List<String> allergies = List.of("Chat");

        params.put("firstName", "John");
        params.put("lastName", "Doe");
        params.put("birthdate", "03/06/1994");
        params.put("medications", medications);
        params.put("allergies", allergies);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("MedicalRecord to update not found");
    }

    @Test
    public void deleteMedicalRecordTest_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Kendrik")
                        .param("lastName", "Stelzer")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("medicalRecord removed success");

        // Check that medicalRecord not existing
        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(getResult.getResponse().getContentAsString())
                .doesNotContain("\"firstName\":\"Kendrik\",\"lastName\":\"Stelzer")
                .contains("\"firstName\":\"Brian\",\"lastName\":\"Stelzer");
    }

    @Test
    public void deleteMedicalRecordTest_notDeletedError() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("MedicalRecord not deleted");
    }

}
