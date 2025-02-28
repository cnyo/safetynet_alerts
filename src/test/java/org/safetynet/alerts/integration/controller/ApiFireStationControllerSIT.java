package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiFireStationController;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.service.FireStationService;
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

@WebMvcTest(controllers = { ApiFireStationController.class, FireStationService.class })
@ExtendWith(SpringExtension.class)
public class ApiFireStationControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireStationRepository fireStationRepository;

    @Test
    public void getFireStationTest_success() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("3");
        mockFireStation.setAddress("21 jump street");
        List<FireStation> mockFireStations = List.of(mockFireStation);

        when(fireStationRepository.findAll()).thenReturn(mockFireStations);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/firestation/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"3")
                .contains("\"address\":\"21 jump street");
    }

    @Test
    public void postFireStationTest_success() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("10");
        mockFireStation.setAddress("21 jump street");
        ObjectMapper mapper = new ObjectMapper();

        when(fireStationRepository.create(any(FireStation.class))).thenReturn(mockFireStation);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
        ).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"10")
                .contains("\"address\":\"21 jump street");
    }

    @Test
    public void postFireStationTest_alreadyExists() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("10");
        mockFireStation.setAddress("21 jump street");
        ObjectMapper mapper = new ObjectMapper();

        when(fireStationRepository.create(any(FireStation.class))).thenThrow(new InstanceAlreadyExistsException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mockFireStation))
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation already exists at address.");
    }

    @Test
    public void patchFireStationTest_success() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("15");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("address", "1509 Culver St");
        params.put("station", "3");
        params.put("new_station", "15");

        when(fireStationRepository.update(anyString(), anyString(), anyString())).thenReturn(mockFireStation);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"15")
                .contains("\"address\":\"1509 Culver St");
    }

    @Test
    public void patchFireStationTest_notExists() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("15");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("address", "1509 Culver St");
        params.put("station", "3");
        params.put("new_station", "15");

        when(fireStationRepository.update(anyString(), anyString(), anyString())).thenThrow(new NoSuchElementException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation to update not found");
    }

    @Test
    public void patchFireStationTest_badArgument() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("15");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<>();
        params.put("address", "1509 Culver St");
        params.put("state", "3");
        params.put("new_station", "15");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid parameters");
    }

    @Test
    public void deleteFireStationTest_success() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("2");

        when(fireStationRepository.remove(any(FireStation.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation removed successfully");
    }

    @Test
    public void deleteFireStationTest_error() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("2");

        when(fireStationRepository.remove(any(FireStation.class))).thenReturn(false);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Fire station not removed");
    }

    @Test
    public void deleteFireStationTest_notExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("2");

        when(fireStationRepository.remove(any(FireStation.class))).thenThrow(new NoSuchElementException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Firestation to delete not found");
    }

}
