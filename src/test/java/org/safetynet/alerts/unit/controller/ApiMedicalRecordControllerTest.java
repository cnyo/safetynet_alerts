package org.safetynet.alerts.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiMedicalRecordController;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiMedicalRecordController.class)
public class ApiMedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("Warren");
        medicalRecord.setLastName("Zemicks");
        medicalRecord.setBirthdate("03/06/1985");
        medicalRecord.setMedications(Collections.emptyList());
        medicalRecord.setAllergies(Collections.emptyList());
    }

    @Test
    public void getAllMedicalRecordShouldReturnResponseWithMedicalRecords() throws Exception {
        List<MedicalRecord> medicalRecords = Collections.singletonList(medicalRecord);

        given(medicalRecordService.getAll()).willReturn(medicalRecords);

        mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"Warren")))
                .andExpect(content().string(containsString("\"lastName\":\"Zemicks")))
                .andExpect(content().string(containsString("\"birthdate\":\"03/06/1985")))
                .andReturn();
    }

    @Test
    public void getAllMedicalRecordInternalErrorShouldReturnException() throws Exception {
        given(medicalRecordService.getAll()).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.get("/medicalRecord/all"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void postMedicalRecordShouldReturnCreatedMedicalRecord() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(medicalRecordService.create(any(MedicalRecord.class))).willReturn(medicalRecord);

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(medicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"Warren")))
                .andExpect(content().string(containsString("\"lastName\":\"Zemicks")))
                .andExpect(content().string(containsString("\"birthdate\":\"03/06/1985")))
                .andReturn();
    }

    @Test
    public void postAlreadyExistsMedicalRecordShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(medicalRecordService.create(any(MedicalRecord.class))).willThrow(new InstanceAlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(medicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(content().string(containsString("MedicalRecord already exists for person")))
                .andReturn();
    }

    @Test
    public void postMedicalRecordForPersonNotFoundShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(medicalRecordService.create(any(MedicalRecord.class))).willThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(medicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(containsString("Person for new medical record not exists")))
                .andReturn();
    }

    @Test
    public void postMedicalRecordInErrorShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(medicalRecordService.create(any(MedicalRecord.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalRecord")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(medicalRecord))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void patchMedicalRecordTestShouldReturnPatchedMedicalRecord() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", "Boyd");
        params.put("birthdate", "01/06/1994");
        params.put("medications", List.of("doliprane:1000mg"));
        params.put("allergies", List.of("Chat"));
        medicalRecord.setMedications(List.of("doliprane:1000mg"));
        medicalRecord.setAllergies(List.of("Chat"));

        given(medicalRecordService.update(any(MedicalRecord.class))).willReturn(medicalRecord);

        mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"firstName\":\"Warren")))
                .andExpect(content().string(containsString("\"lastName\":\"Zemicks")))
                .andExpect(content().string(containsString("\"birthdate\":\"03/06/1985")))
                .andExpect(content().string(containsString("[\"doliprane:1000mg\"]")))
                .andExpect(content().string(containsString("[\"Chat\"]")))
                .andReturn();
    }

    @Test
    public void patchNotFoundMedicalRecordTestShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", "Boyd");
        params.put("birthdate", "01/06/1994");
        params.put("medications", List.of("doliprane:1000mg"));
        params.put("allergies", List.of("Chat"));

        given(medicalRecordService.update(any(MedicalRecord.class))).willThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(containsString("MedicalRecord to update not found")))
                .andReturn();
    }

    @Test
    public void patchInErrorMedicalRecordTestShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", "Boyd");
        params.put("birthdate", "01/06/1994");
        params.put("medications", List.of("doliprane:1000mg"));
        params.put("allergies", List.of("Chat"));

        given(medicalRecordService.update(any(MedicalRecord.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void deleteMedicalRecordTestShouldReturnSuccess() throws Exception {
        given(medicalRecordService.remove(anyString(), anyString())).willReturn(true);

       mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Warren")
                        .param("lastName", "Zemicks")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("medicalRecord removed success")))
                .andReturn();
    }

    @Test
    public void notDeletedMedicalRecordShouldReturnException() throws Exception {
        given(medicalRecordService.remove(anyString(), anyString())).willReturn(false);

       mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Warren")
                        .param("lastName", "Zemicks")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string(containsString("MedicalRecord not deleted")))
                .andReturn();
    }

    @Test
    public void deleteInErrorMedicalRecordShouldReturnException() throws Exception {
        given(medicalRecordService.remove(anyString(), anyString())).willThrow(new RuntimeException());

       mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Warren")
                        .param("lastName", "Zemicks")
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }
}
