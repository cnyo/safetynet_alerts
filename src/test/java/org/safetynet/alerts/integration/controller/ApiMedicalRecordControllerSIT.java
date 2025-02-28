package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiMedicalRecordController;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.service.MedicalRecordService;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = { ApiMedicalRecordController.class, MedicalRecordService.class })
@ExtendWith(SpringExtension.class)
public class ApiMedicalRecordControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    @Test
    public void getAllMedicalRecordsTest_success() throws Exception {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1994");
        List<MedicalRecord> mockMedicalRecords = List.of(mockMedicalRecord);

        when(medicalRecordRepository.findAll()).thenReturn(mockMedicalRecords);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe")
                .contains("\"birthdate\":\"03/06/1994");
    }

    @Test
    public void postMedicalRecordTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1994");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mockMedicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe")
                .contains("\"birthdate\":\"03/06/1994");
    }

    @Test
    public void postMedicalRecordTest_alreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1994");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new InstanceAlreadyExistsException());

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
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1994");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new NoSuchElementException());

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
        params.put("lastName", "Doe");
        params.put("birthdate", "03/06/1994");
        params.put("medications", medications);
        params.put("allergies", allergies);

        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");
        mockMedicalRecord.setBirthdate("03/06/1994");
        mockMedicalRecord.setMedications(medications);
        mockMedicalRecord.setAllergies(allergies);

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"firstName\":\"John")
                .contains("\"lastName\":\"Doe")
                .contains("\"birthdate\":\"03/06/1994")
                .contains("[\"doliprane:1000mg\"]")
                .contains("[\"Chat\"]");
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

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenThrow(new NoSuchElementException());

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
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("medicalRecord removed success");
    }

    @Test
    public void deleteMedicalRecordTest_notDeletedError() throws Exception {
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(false);

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
