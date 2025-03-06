package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mock;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataService;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Tag("FireStation")
public class JsonDataServiceTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.JsonDataService";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Mock
    ObjectMapper objectMapper;

    @Mock
    ClassPathResource classPathResource;

    private final String jsonPath = "data.json";

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("JsonDataServiceTest");
    }

    @BeforeEach
    public void setUp() throws IOException {
        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();

        JsonData jsonData = new JsonData();

        when(objectMapper.readValue(any(InputStream.class), eq(JsonData.class))).thenReturn(jsonData);
    }

    @Test
    public void getJsonDataSuccessShouldReturnJsonData() throws IOException {
        String fakeJson = "{}";
        InputStream fakeInputStream = new ByteArrayInputStream(fakeJson.getBytes());

        when(classPathResource.getInputStream()).thenReturn(fakeInputStream);
        JsonDataService.init(jsonPath);

        JsonData result = JsonDataService.getJsonData();

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(JsonData.class);

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);
        assertThat(memoryAppender.search("Data loaded successfully", Level.INFO)).hasSize(1);
    }

    @Test
    public void getJsonDataThrowFileNotFoundException() {
        try (MockedConstruction<ClassPathResource> dummy = mockConstruction(ClassPathResource.class,
                (mock, context) -> {
                    when(mock.getInputStream()).thenThrow(new FileNotFoundException());
                })) {

            assertThrows(RuntimeException.class, () -> JsonDataService.init(jsonPath));
            assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
            assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);
            assertThat(memoryAppender.search("JSON file not found at path", Level.ERROR)).hasSize(1);
        }
    }

    @Test
    public void getJsonDataThrowUnrecognizedPropertyException() {
        try (MockedConstruction<ClassPathResource> dummy = mockConstruction(ClassPathResource.class,
                (mock, context) -> {
                    when(mock.getInputStream()).thenThrow(new IOException());
                })) {

            assertThrows(RuntimeException.class, () -> JsonDataService.init(jsonPath));
            assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
            assertThat(memoryAppender.search("Initializing JSON data from path", Level.INFO)).hasSize(1);
            assertThat(memoryAppender.search("I/O error while loading JSON data", Level.ERROR)).hasSize(1);
        }
    }
}
