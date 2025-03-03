package org.safetynet.alerts.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("FireStation")
public class JsonDataServiceIT {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.JsonDataService";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Autowired
    private JsonDataService jsonDataService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("JsonDataServiceTest");
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
    public void getJsonDataSuccess() {
        jsonDataService.init("test-data.json");
        JsonData result = JsonDataService.getJsonData();

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(JsonData.class);
        assertThat(result.getMedicalrecords().size()).isEqualTo(1);
        assertThat(result.getFirestations().size()).isEqualTo(1);
        assertThat(result.getPersons().size()).isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);
        assertThat(memoryAppender.search("Data loaded successfully !", Level.INFO)).hasSize(1);
    }

    @Test
    public void initWithBadPath() {
        assertThrows(RuntimeException.class, () -> jsonDataService.init("bad_path"));

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);
        assertThat(memoryAppender.search("I/O error while loading JSON data", Level.ERROR)).hasSize(1);
    }

    // todo: test à revoir
    @ParameterizedTest
    @ValueSource(strings = {
            "test-bad-structured-data.json",
            "test-wrong-data-type-data.json"
    })
    public void getJsonDataWithWrongDataType(String jsonPath) {
        assertThrows(RuntimeException.class, () -> jsonDataService.init(jsonPath));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);

        assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);

        if (jsonPath.equals("test-bad-structured-data.json")) {
            assertThat(memoryAppender.search("JSON mapping error in file", Level.ERROR)).hasSize(1);
        } else {
            assertThat(memoryAppender.search("JSON mapping error in file", Level.ERROR)).hasSize(1);
        }
    }

}
