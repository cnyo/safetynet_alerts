package org.safetynet.alerts.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("FireStation")
public class JsonDataServiceIT {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.JsonDataService";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    private JsonDataService jsonDataService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("JsonDataServiceTest");
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();

        jsonDataService = new JsonDataService();
        Field objectMapperField = JsonDataService.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(jsonDataService, new ObjectMapper());
    }

    @Test
    public void test_getJsonData_success() {
        jsonDataService.init("test-data.json");
        JsonData result = jsonDataService.getJsonData();

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(JsonData.class);
        assertThat(result.getMedicalrecords().size()).isEqualTo(1);
        assertThat(result.getFirestations().size()).isEqualTo(1);
        assertThat(result.getPersons().size()).isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Data loaded successfully !", Level.INFO)).hasSize(1);
    }

    @Test
    public void test_init_withBadPath() {
        assertThrows(RuntimeException.class, () -> jsonDataService.init("bad_path"));

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("I/O error while loading JSON data", Level.ERROR)).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-bad-data.json",
            "bad-structured-data.json",
            "wrong-data-type-data.json"
    })
    public void test_getJsonData_withWrongDataType(String jsonPath) {
        assertThrows(RuntimeException.class, () -> jsonDataService.init(jsonPath));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);

        if (jsonPath.equals("bad-structured-data.json")) {
            assertThat(memoryAppender.search("Unrecognized property", Level.ERROR)).hasSize(1);
        } else {
            assertThat(memoryAppender.search("JSON mapping error in file", Level.ERROR)).hasSize(1);
        }
    }

}
