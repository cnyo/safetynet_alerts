package org.safetynet.alerts.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Check adult number")
    public void apiController_getPersonByStationNumber_checkAdultNumber_test() throws Exception {
        mockMvc.perform(get("/firestation?station_number=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultNumber").value(8));
    }

    @Test
    public void apiController_getPersonByStationNumber_checkPersonFirstName_test() throws Exception {
        mockMvc.perform(get("/firestation?station_number=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"));
    }

    @Test
    public void apiController_getPersonByStationNumber_stationDoesNotExist_test() throws Exception {
        mockMvc.perform(get("/firestation?station_number=10"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void apiController_getChildAlert_withChildren_test() throws Exception {
        mockMvc.perform(get("/childAlert?address=834 Binoc Ave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children[0].firstName").exists());
    }

    @Test
    public void apiController_getChildAlert_noChildren_test() throws Exception {
        mockMvc.perform(get("/childAlert?address=29 15th St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty());
    }

    @Test
    public void apiController_getChildAlert_withAdults_test() throws Exception {
        mockMvc.perform(get("/childAlert?address=1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adults[0].firstName").value("John"));
    }

    @Test
    public void apiController_getChildAlert_withNobody_test() throws Exception {
        mockMvc.perform(get("/childAlert?address=1515 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isEmpty())
                .andExpect(jsonPath("$.adults").isEmpty());
    }

    @Test
    public void apiController_getChildAlert_withNoAddress_test() throws Exception {
        mockMvc.perform(get("/childAlert?address=1515 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isEmpty())
                .andExpect(jsonPath("$.adults").isEmpty());
    }

}
