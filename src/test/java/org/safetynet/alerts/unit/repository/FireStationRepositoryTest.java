package org.safetynet.alerts.unit.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.management.InstanceAlreadyExistsException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("FireStationRepositoryTest")
public class FireStationRepositoryTest {

    @Autowired
    FireStationRepository fireStationRepository;

    private static final String jsonPath = "data.json";

    private static JsonData jsonData;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        JsonDataService.init(jsonPath);

        try (MockedStatic<JsonDataService> mockedStatic = Mockito.mockStatic(JsonDataService.class)) {
            mockedStatic.when(JsonDataService::getJsonData).thenReturn(jsonData);
        }
    }

    @AfterEach
    void tearDown() {
        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void createShouldReturnFireStationCreated() throws InstanceAlreadyExistsException {
        String address = "1509 Culver St";
        String station = "19";
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(station);

        Optional<FireStation> result = Optional.ofNullable(fireStationRepository.create(fireStation));

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getAddress()).isEqualTo(address);
        assertThat(result.get().getStation()).isEqualTo(station);
    }

    @Test
    public void createFireStationAlreadyExistsShouldReturnException() {
        String address = "1509 Culver St";
        String station = "3";
        FireStation fireStation = new FireStation();
        fireStation.setAddress(address);
        fireStation.setStation(station);

        assertThrows(InstanceAlreadyExistsException.class, () -> fireStationRepository.create(fireStation));
    }

    @Test
    public void updateShouldReturnFireStation() throws NoSuchElementException {
        String address = "1509 Culver St";
        String station = "3";
        String newStation = "5";

        Optional<FireStation> result = Optional.ofNullable(fireStationRepository.update(address, station, newStation));

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getAddress()).isEqualTo(address);
        assertThat(result.get().getStation()).isEqualTo(newStation);
    }

    @Test
    public void tryUpdateNoneExistingFireStationShouldReturnException() {
        String address = "1509 Culver St";
        String station = "10";
        String newStation = "5";

        assertThrows(NoSuchElementException.class, () -> fireStationRepository.update(address, station, newStation));
    }

    @Test
    public void removeShouldReturnTrue() {
        FireStation fireStation = new FireStation();
        fireStation.setAddress("1509 Culver St");
        fireStation.setStation("3");

        boolean result = fireStationRepository.remove(fireStation);

        assertThat(result).isTrue();
    }

    @Test
    public void tryRemoveNoneExistingFireStationShouldReturnFalse() {
        FireStation fireStation = new FireStation();
        fireStation.setAddress("1509 Culver St");
        fireStation.setStation("100");

        boolean result = fireStationRepository.remove(fireStation);

        assertThat(result).isFalse();
    }

    @Test
    public void findAtAddressShouldReturnFireStation() {
        String address = "1509 Culver St";

        Optional<FireStation> result = fireStationRepository.findFireStationAtAddress(address);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getAddress()).isEqualTo(address);
        assertThat(result.get().getStation()).isEqualTo("3");
    }

    @Test
    public void findNotExistingAtAddressShouldReturnNull() {
        String address = "21 jump street";

        Optional<FireStation> result = fireStationRepository.findFireStationAtAddress(address);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findOneShouldReturnFireStation() {
        String address = "1509 Culver St";
        String station = "3";

        Optional<FireStation> result = fireStationRepository.findOneFireStation(address, station);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getAddress()).isEqualTo(address);
        assertThat(result.get().getStation()).isEqualTo(station);
    }

    @Test
    public void findOneNoneExistsShouldReturnNull() {
        String address = "1509 Culver St";
        String station = "4";

        Optional<FireStation> result = fireStationRepository.findOneFireStation(address, station);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findAllShouldReturnFireStationList() {
        List<FireStation> result = fireStationRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(13);
    }

    @Test
    public void findAllAddressForOneStationShouldReturnAddresses() {
        String station = "3";

        List<String> result = fireStationRepository.findAllAddressForOneStation(station);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.getFirst()).isEqualTo("1509 Culver St");
    }

    @Test
    public void findAddressForNotExistingStationShouldReturnNoAddresses() {
        String station = "100";

        List<String> result = fireStationRepository.findAllAddressForOneStation(station);

        assertThat(result).isEmpty();
    }

    @Test
    public void findAddressesForStationsShouldReturnAddresses() {
        String stations = "2, 3";

        List<String> result = fireStationRepository.findAddressesForStations(stations.split(","));

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.getFirst()).isEqualTo("29 15th St");
    }

    @Test
    public void findAddressesForNotExistingStationsShouldReturnNoAddresses() {
        String stations = "2-3";

        List<String> result = fireStationRepository.findAddressesForStations(stations.split(","));

        assertThat(result).isEmpty();
    }
}
