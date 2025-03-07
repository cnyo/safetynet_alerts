package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiFireStationControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FireStationService fireStationService;

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
    public void getFireStationSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"station\":\"3")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();
    }

    @Test
    public void postFireStationSuccess() throws Exception {
        assertThat(jsonData.getFirestations().size()).isEqualTo(13);

        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("10");
        mockFireStation.setAddress("21 jump street");
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(content().string(containsString("\"station\":\"10")))
                .andExpect(content().string(containsString("\"address\":\"21 jump street")))
                .andReturn();

        // Check if fireStation was created
        FireStation newFireStation = fireStationService.getOneFireStation("21 jump street", "10");

        assertThat(newFireStation).isNotNull();
        assertThat(newFireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(newFireStation.getStation()).isEqualTo("10");
        assertThat(jsonData.getFirestations().size()).isEqualTo(14);
    }

    @Test
    public void postFireStationAlreadyExists() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("3");
        mockFireStation.setAddress("1509 Culver St");
        ObjectMapper mapper = new ObjectMapper();

         mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("FireStation already exists at address")))
                .andReturn();
    }

    @Test
    public void patchFireStationSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "1509 Culver St");
        params.put("station", "3");
        params.put("new_station", "15");

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"station\":\"15")))
                .andExpect(content().string(containsString("\"address\":\"1509 Culver St")))
                .andReturn();

        // Check if fireStation is updated
        FireStation fireStation = fireStationService.getOneFireStation("1509 Culver St", "15");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddress()).isEqualTo("1509 Culver St");
        assertThat(fireStation.getStation()).isEqualTo("15");
    }

    @Test
    public void patchFireStationNotExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "21 jump street");
        params.put("station", "10");
        params.put("new_station", "15");

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("FireStation to update not found")))
                .andReturn();
    }

    @Test
    public void patchFireStationBadArgument() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "1509 Culver St");
        params.put("state", "3");
        params.put("new_station", "");

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Invalid parameters")))
                .andReturn();
    }

    @Test
    public void deleteFireStationSuccess() throws Exception {
        assertThat(jsonData.getFirestations().size()).isEqualTo(13);

        ObjectMapper mapper = new ObjectMapper();

        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("1509 Culver St");
        mockFireStation.setStation("3");

        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("FireStation removed successfully")))
                .andReturn();

        FireStation newFireStation = fireStationService.getOneFireStation("1509 Culver St", "3");

        assertThat(newFireStation).isNull();
        assertThat(jsonData.getFirestations().size()).isEqualTo(12);
    }

    @Test
    public void deleteFireStationError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("892 Downing Ct");
        mockFireStation.setStation("1");

        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(containsString("Fire station not removed")))
                .andReturn();
    }
}
