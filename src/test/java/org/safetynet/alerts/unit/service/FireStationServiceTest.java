package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.JsonDataService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Tag("FireStation")
public class FireStationServiceTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.FireStationService";
    private final String MSG = "Mon message de test";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @InjectMocks
    private FireStationService fireStationService;

    @Mock
    FireStationRepository fireStationRepository;

    @Spy
    private JsonDataService jsonDataService;

    @Mock
    private JsonData jsonData;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("FireStationServiceTest");
    }

    @BeforeEach
    public void setUp() {
        doNothing().when(jsonDataService).init(anyString());
        JsonData mockJsonData = new JsonData();
        when(jsonDataService.getJsonData()).thenReturn(mockJsonData);

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

    @Tag("Get")
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
        assertThat(fireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Get fire station at address "+ mockFireStation.getAddress() +" success", Level.DEBUG)).hasSize(1);
    }

    @Tag("Get")
    @DisplayName("Get not found fire station by address")
    @Test
    public void test_getFireStationAtAddress_notFound() {
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.findFireStationAtAddress(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fireStationService.getFireStationAtAddress("21 jump street"));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(0);

    }

    @Tag("Create")
    @DisplayName("Get one fire station by address successfully")
    @Test
    public void test_createFireStation_successfully() throws InstanceAlreadyExistsException {
//        List<FireStation> mockFireStationList = mock(List.class);
        FireStation mockFireStation = new FireStation();
        mockFireStation.setAddress("21 jump street");
        mockFireStation.setStation("2");

        when(fireStationRepository.create(any(FireStation.class))).thenReturn(mockFireStation);
//        when(mockFireStationList.add(mockFireStation)).thenReturn(true);

        FireStation fireStation = fireStationService.create(mockFireStation);

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddress()).isEqualTo("21 jump street");
        assertThat(fireStation.getStation()).isEqualTo("2");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("FireStation created successfully", Level.DEBUG)).hasSize(1);
    }

    @Tag("Create FireStation")
    @DisplayName("Try to create fireStation already existing")
    @Test
    public void test_createFireStation_alreadyExists() throws InstanceAlreadyExistsException {
        List<FireStation> mockFireStationList = new ArrayList<>();
        FireStation mockFireStation = new FireStation() {{
            setAddress("21 jump street");
            setStation("2");
        }};

        when(jsonData.getFirestations()).thenReturn(mockFireStationList);
        when(fireStationRepository.create(any(FireStation.class))).thenThrow(new InstanceAlreadyExistsException());

        assertThrows(InstanceAlreadyExistsException.class, () -> fireStationService.create(mockFireStation));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(0);
    }

    @Tag("Update")
    @DisplayName("Try to update fireStation success")
    @Test
    public void test_update_success() {
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

    @Tag("Update")
    @DisplayName("Try to update fireStation failed")
    @Test
    public void test_validatePatchParams_isFail() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("address", "21 jump street");
        params.put("station", "2");

        assertThrows(IllegalArgumentException.class, () -> fireStationService.update(params));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Update fire station patch failed with invalid parameters.", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("is missing", Level.DEBUG)).hasSize(1);
    }

    @Tag("Remove")
    @DisplayName("Try to remove fireStation success")
    @Test
    public void test_remove_success() {
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

    @Tag("Remove")
    @DisplayName("Try to remove fireStation success")
    @Test
    public void test_remove_fail() {
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

    @Tag("Get")
    @DisplayName("Try get all to to return fireStations")
    @Test
    public void test_getAll_success() {
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

    @Tag("Get")
    @DisplayName("Try get all to return no fireStation")
    @Test
    public void test_getAll_empty() {
        List<FireStation> mockedFireStations = new ArrayList<>();

        when(fireStationRepository.findAll()).thenReturn(mockedFireStations);

        List<FireStation> result = fireStationService.getAll();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("getAll fire stations", Level.DEBUG)).hasSize(1);
    }

    @Tag("Get")
    @DisplayName("Try get all addresses for many fireStations")
    @Test
    public void test_getAddressesForOneFireStation_withManyStationSuccess() {
        String stations = "2,3";
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        when(fireStationRepository.findAddressesForStations(any(String[].class)))
                .thenReturn(addresses);

        List<String> result = fireStationService.getAddressesForFireStations(stations);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly("21 jump street", "1 sesame street");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses found from FireStation", Level.DEBUG)).hasSize(1);

    }

    @Tag("Get")
    @DisplayName("Try get all addresses for one fireStation")
    @Test
    public void test_getAddressesForOneFireStation_withOneStationSuccess() {
        String station = "2";
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        when(fireStationRepository.findAllAddressForOneStation(any(String.class)))
                .thenReturn(addresses);

        List<String> result = fireStationService.getAddressesForOneFireStation(station);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly("21 jump street", "1 sesame street");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses found from FireStation", Level.DEBUG)).hasSize(1);

    }

    @Tag("Get")
    @DisplayName("Try get all addresses for fireStations with stations is null")
    @Test
    public void test_getAddressesForFireStations_whithNullStationsIsFail() {
        String stations = null;

        assertThatThrownBy(() -> fireStationService.getAddressesForFireStations(stations))
                .isInstanceOf(NullPointerException.class);

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(0);

    }

    @Tag("Get")
    @DisplayName("Try get all addresses for fireStations")
    @Test
    public void test_getAddressesForFireStations_checkLogs() {
        String stations = "2,3";
        String[] stationNumbers = stations.split(",");
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        when(fireStationRepository.findAddressesForStations(stationNumbers))
                .thenReturn(addresses);

        List<String> result = fireStationService.getAddressesForFireStations(stations);

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses found from FireStation", Level.DEBUG)).hasSize(1);
    }

}
