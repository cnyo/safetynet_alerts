package org.safetynet.alerts.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.safetynet.alerts.controller.ApiFireStationController;
import org.safetynet.alerts.model.FireStation;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiFireStationController.class)
public class ApiFireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireStationService fireStationService;

    private FireStation fireStation;

    @BeforeEach
    void setUp() {
        fireStation = new FireStation();
        fireStation.setStation("3");
        fireStation.setAddress("21 jump street");
    }

    @Test
    public void getAllFireStationShouldReturnResponseWithFireStations() throws Exception {
        List<FireStation> fireStations = Collections.singletonList(fireStation);

        given(fireStationService.getAll()).willReturn(fireStations);

        mockMvc.perform(MockMvcRequestBuilders.get("/firestation/all"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"station\":\"3")))
                .andExpect(content().string(containsString("21 jump street")))
                .andReturn();
    }

    @Test
    public void postFireStationShouldReturnCreatedFireStation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.create(any(FireStation.class))).willReturn(fireStation);

        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"station\":\"3")))
                .andExpect(content().string(containsString("21 jump street")))
                .andReturn();
    }

    @Test
    public void postAlreadyExistsFireStationShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.create(any(FireStation.class))).willThrow(new InstanceAlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(content().string(containsString("FireStation already exists at address")))
                .andReturn();
    }

    @Test
    public void postInErrorFireStationShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.create(any(FireStation.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void patchFireStationShouldReturnPatchedFireStation() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();
        params.put("station", "3");
        params.put("address", "21 jump street");
        params.put("new_station", "1");
        fireStation.setStation("1");

        given(fireStationService.update(anyMap())).willReturn(fireStation);

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("\"station\":\"1")))
                .andExpect(content().string(containsString("\"address\":\"21 jump street")))
                .andReturn();
    }

    @Test
    public void patchNotFoundFireStationShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();
        params.put("station", "3");
        params.put("address", "21 jump street");
        params.put("new_station", "1");

        given(fireStationService.update(anyMap())).willThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(containsString("FireStation to update not found")))
                .andReturn();
    }

    @Test
    public void patchIllegalArgumentFireStationShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();
        params.put("station", "3");
        params.put("address", "21 jump street");

        given(fireStationService.update(anyMap())).willThrow(new IllegalArgumentException());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string(containsString("Invalid parameters")))
                .andReturn();

        result.getResponse();
    }

    @Test
    public void patchInErrorFireStationShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = new HashMap<>();
        params.put("station", "3");
        params.put("address", "21 jump street");
        params.put("new_station", "1");

        given(fireStationService.update(anyMap())).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void deleteFireStationShouldReturnSuccess() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.remove(any(FireStation.class))).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(containsString("FireStation removed successfully")))
                .andReturn();
    }

    @Test
    public void deleteFireStationNotRemovedShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.remove(any(FireStation.class))).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string(containsString("Fire station not removed")))
                .andReturn();
    }

    @Test
    public void deleteFireStationInErrorShouldReturnException() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        given(fireStationService.remove(any(FireStation.class))).willThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fireStation))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }
}
