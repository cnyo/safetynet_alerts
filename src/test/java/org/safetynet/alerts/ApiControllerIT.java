package org.safetynet.alerts;

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
    public void apiController_getPersonByStationNumber_forStationDoesNotExist_test() throws Exception {
        mockMvc.perform(get("/firestation?station_number=10"))
                .andExpect(status().isNoContent());
    }

}
