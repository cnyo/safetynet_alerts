package org.safetynet.alerts.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
@Tag("FireStation")
public class FireStationServiceTest {

    @InjectMocks
    private FireStationService fireStationService;

    @Mock
    FireStationRepository fireStationRepository;

    @Spy
    private JsonDataService jsonDataService;

    @Mock
    private JsonData jsonData;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClassPathResource classPathResource;
    @Autowired
    private FireStation fireStation;

    @BeforeEach
    public void setUp() {
        doNothing().when(jsonDataService).init();
        JsonData mockJsonData = new JsonData();
        when(jsonDataService.getJsonData()).thenReturn(mockJsonData);
    }

    @DisplayName("Get one fire station by address successfully")
    @Test
    public void test_getFireStationAtAddress_successfully() {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.findFireStationAtAddress(any(String.class)))
                .thenReturn(Optional.of(mockFireStation));

        FireStation fireStation = fireStationService.getFireStationAtAddress("21 jump street");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation).isInstanceOf(FireStation.class);
    }

    @DisplayName("Get not found fire station by address")
    @Test
    public void test_getFireStationAtAddress_notFound() {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.findFireStationAtAddress(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fireStationService.getFireStationAtAddress("21 jump street"));
    }

    @DisplayName("Get one fire station by address successfully")
    @Test
    public void test_createFireStation_successfully() {
        List<FireStation> mockFireStationList = mock(List.class);
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(jsonData.getFirestations()).thenReturn(mockFireStationList);
        when(fireStationRepository.create(any(FireStation.class))).thenReturn(mockFireStation);
        when(mockFireStationList.add(mockFireStation)).thenReturn(true);

        FireStation fireStation = fireStationService.createFireStation(mockFireStation);

        assertThat(fireStation).isNotNull();
    }
}
