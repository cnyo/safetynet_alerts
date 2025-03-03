package org.safetynet.alerts.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.FireStationService;
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

@SpringBootTest
@AutoConfigureMockMvc
public class ApiFireStationControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FireStationService fireStationService;

    @Test
    public void getFireStationSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/firestation/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"3")
                .contains("\"address\":\"1509 Culver St");
    }

    @Test
    public void postFireStationSuccess() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("10");
        mockFireStation.setAddress("21 jump street");
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
        ).andReturn();
        FireStation newFireStation = fireStationService.getOneFireStation("21 jump street", "10");

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"10")
                .contains("\"address\":\"21 jump street");
        assertThat(newFireStation).isNotNull();
        assertThat(newFireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(newFireStation.getStation()).isEqualTo("10");
    }

    @Test
    public void postFireStationAlreadyExists() throws Exception {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setStation("2");
        mockFireStation.setAddress("29 15th St");
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mockFireStation))
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation already exists at address.");
    }

    @Test
    public void patchFireStationSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "834 Binoc Ave");
        params.put("station", "3");
        params.put("new_station", "15");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        FireStation newFireStation = fireStationService.getOneFireStation("834 Binoc Ave", "15");

        assertThat(result.getResponse().getContentAsString())
                .contains("\"station\":\"15")
                .contains("\"address\":\"834 Binoc Ave");
        assertThat(newFireStation).isNotNull();
        assertThat(newFireStation.getAddress()).isEqualTo("834 Binoc Ave");
        assertThat(newFireStation.getStation()).isEqualTo("15");
    }

    @Test
    public void patchFireStationNotExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "21 jump street");
        params.put("station", "3");
        params.put("new_station", "15");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params))
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation to update not found");
    }

    @Test
    public void patchFireStationBadArgument() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> params = new HashMap<>();
        params.put("address", "644 Gershwin Cir");
        params.put("state", "3");
        params.put("new_station", "");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Invalid parameters");
    }

    @Test
    public void deleteFireStationSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("748 Townings Dr");
        mockFireStation.setStation("3");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        FireStation newFireStation = fireStationService.getOneFireStation("748 Townings Dr", "3");

        assertThat(result.getResponse().getContentAsString())
                .contains("FireStation removed successfully");
        assertThat(newFireStation).isNull();
    }

    @Test
    public void deleteFireStationError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("892 Downing Ct");
        mockFireStation.setStation("1");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mockFireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("Fire station not removed");
    }
}
