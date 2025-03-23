package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationJsonRepository;
import org.safetynet.alerts.service.FireStationServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Tag("FireStation")
public class FireStationServiceImplTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.FireStationServiceImpl";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @InjectMocks
    private FireStationServiceImpl fireStationService;

    @Mock
    private FireStationJsonRepository fireStationRepository;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("FireStationServiceTest");
    }

    @BeforeEach
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

    @Test
    public void getFireStationAtAddressTestSuccessfully() {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.findFireStationAtAddress(any(String.class)))
                .thenReturn(Optional.of(mockFireStation));

        FireStation fireStation = fireStationService.getFireStationAtAddress("21 jump street");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Get fire station at address "+ mockFireStation.getAddress() +" success", Level.DEBUG)).hasSize(1);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getFireStationWithBlankAddressShouldReturnException(String address) {
        assertThrows(IllegalArgumentException.class, () -> fireStationService.getFireStationAtAddress(address));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Address is blank", Level.ERROR)).hasSize(1);
    }

    @Test
    public void getFireStationAtAddressNotFound() {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.findFireStationAtAddress(any(String.class)))
                .thenReturn(Optional.empty());

        FireStation result = fireStationService.getFireStationAtAddress("21 jump street");

        assertThat(result).isNull();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Get fire station at address", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void createFireStationSuccessfully() throws InstanceAlreadyExistsException {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.create(any(FireStation.class))).thenReturn(mockFireStation);

        FireStation fireStation = fireStationService.create(mockFireStation);

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(fireStation.getStation()).isEqualTo("2");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation created successfully", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void createFireStationAlreadyExists() throws InstanceAlreadyExistsException {
        List<FireStation> mockFireStationList = new ArrayList<>();
        FireStation mockFireStation = new FireStation() {{
            setAddress("1509 Culver St");
            setStation("3");
        }};

        when(fireStationRepository.create(any(FireStation.class))).thenThrow(new InstanceAlreadyExistsException());

        assertThrows(InstanceAlreadyExistsException.class, () -> fireStationService.create(mockFireStation));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(0);
    }

    @Test
    public void updateSuccess() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("address", "21 jump street");
        params.put("station", "2");
        params.put("new_station", "15");
        FireStation mockFireStation = new FireStation() {{
            setAddress("21 jump street");
            setStation("15");
        }};

        when(fireStationRepository.update(any(String.class), any(String.class), any(String.class)))
                .thenReturn(mockFireStation);

        FireStation fireStation = fireStationService.update(params);

        Pattern msgPattern = Pattern.compile("FireStation \\d+ updated to \\d+ successfully");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation).isInstanceOf(FireStation.class);
        assertThat(fireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(fireStation.getStation()).isEqualTo("15");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.containsPattern(msgPattern, Level.DEBUG)).isTrue();
    }

    @Test
    public void validatePatchParamsIsFail() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("address", "21 jump street");
        params.put("station", "2");

        assertThrows(IllegalArgumentException.class, () -> fireStationService.update(params));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Update fire station patch failed with invalid parameters.", Level.ERROR)).hasSize(1);
        assertThat(memoryAppender.search("is missing", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void removeSuccess() {
        FireStation mockFireStation = new FireStation() {{
            setAddress("21 jump street");
            setStation("2");
        }};

        when(fireStationRepository.remove(any(FireStation.class))).thenReturn(true);

        boolean result = fireStationService.remove(mockFireStation);

        assertThat(result).isTrue();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation removed: success", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void removeFail() {
        FireStation mockFireStation = new FireStation() {{
            setAddress("21 jump street");
            setStation("2");
        }};

        when(fireStationRepository.remove(any(FireStation.class))).thenReturn(false);

        boolean result = fireStationService.remove(mockFireStation);

        assertThat(result).isFalse();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation removed: failure", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getAllSuccess() {
        FireStation mockFireStation = new FireStation() {{
            setAddress("21 jump street");
            setStation("2");
        }};
        List<FireStation> mockedFireStations = new ArrayList<>();
        mockedFireStations.add(mockFireStation);

        when(fireStationRepository.findAll()).thenReturn(mockedFireStations);
        List<FireStation> fireStations = fireStationService.getAll();

        assertThat(fireStations).isNotNull();
        assertThat(fireStations).isNotEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("getAll fire stations", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getAllWithEmptyData() {
        List<FireStation> mockedFireStations = new ArrayList<>();

        when(fireStationRepository.findAll()).thenReturn(mockedFireStations);

        List<FireStation> result = fireStationService.getAll();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("getAll fire stations", Level.DEBUG)).hasSize(1);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAddressesForOneFireStationWithBlankStationsShouldReturnException(String stations) {
        assertThrows(IllegalArgumentException.class, () -> fireStationService.getAddressesForFireStation(stations));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Station is blank", Level.ERROR)).hasSize(1);
    }

    @Test
    public void getAddressesForOneFireStationWithOneStationSuccess() {
        String station = "2";
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        when(fireStationRepository.findAllAddressForOneStation(any(String.class)))
                .thenReturn(addresses);

        List<String> result = fireStationService.getAddressesForFireStation(station);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly("21 jump street", "1 sesame street");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses found from FireStation", Level.DEBUG)).hasSize(1);
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAddressesForFireStationsWithBlankStationsShouldReturnException(String stations) {
        assertThatThrownBy(() -> fireStationService.getAddressesForFireStations(stations))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Stations is blank", Level.ERROR)).hasSize(1);
    }

    @Test
    public void getAddressesForFireStationsCheckLogs() {
        String stations = "2,3";
        String[] stationNumbers = stations.split(",");
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        when(fireStationRepository.findAddressesForStations(stationNumbers))
                .thenReturn(addresses);

        fireStationService.getAddressesForFireStations(stations);

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses found from FireStation", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getOneByNameShouldReturnMedicalRecord() {
        FireStation fireStation = new FireStation();
        fireStation.setAddress("21 jump street");
        fireStation.setStation("3");

        when(fireStationRepository.findOneFireStation(anyString(), anyString())).thenReturn(Optional.of(fireStation));

        FireStation result = fireStationService.getOneFireStation("John", "Doe");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(FireStation.class);
        assertThat(result.getAddress()).isEqualTo("21 jump street");
        assertThat(result.getStation()).isEqualTo("3");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation found by one name: true", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getOneByNameShouldReturnNull() {
        FireStation fireStation = new FireStation();
        fireStation.setAddress("21 jump street");
        fireStation.setStation("3");

        when(fireStationRepository.findOneFireStation(anyString(), anyString())).thenReturn(Optional.ofNullable(null));

        FireStation result = fireStationService.getOneFireStation("21 jump street", "3");

        assertThat(result).isNull();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation found by one name: false", Level.DEBUG)).hasSize(1);
    }

}
