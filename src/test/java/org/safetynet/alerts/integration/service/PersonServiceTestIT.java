package org.safetynet.alerts.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonServiceTestIT {
    @Autowired
    private PersonService personService;

    @BeforeEach
    void setUp() {
        JsonDataService.init("data.json");
    }

    @Test
    public void getChildAlertsShouldReturnChildrenAlerts() {
        List<ChildAlertDto> childAlerts = personService.getChildAlerts("1509 Culver St");

        assertThat(childAlerts).isNotEmpty();
        assertThat(childAlerts.size()).isEqualTo(2);
        assertThat(childAlerts.getFirst().address).isEqualTo("1509 Culver St");
        assertThat(childAlerts.getFirst().age).isEqualTo(13);
        assertThat(childAlerts.getFirst().otherPersons.size()).isEqualTo(4);
        assertThat(childAlerts.getLast().address).isEqualTo("1509 Culver St");
        assertThat(childAlerts.getLast().age).isEqualTo(7);
        assertThat(childAlerts.getLast().otherPersons.size()).isEqualTo(4);
    }
}
