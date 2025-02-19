package org.safetynet.alerts.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Slf4j
@Tag("Initialization")
public class JsonDataServiceTest {
    @InjectMocks
    private JsonDataService jsonDataService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClassPathResource classPathResource;

    @BeforeEach
    public void beforeEach() throws IOException {
    }

//    @DisplayName("Data JSON initialization successful")
//    @Test
//    public void test_jsonLoading_successfully() throws IOException {
//        ReflectionTestUtils.setField(jsonDataService, "jsonPath", "json/data.json");
//
//        when(classPathResource.getInputStream()).thenReturn(any(InputStream.class));
//        when(objectMapper.readValue(any(InputStream.class), JsonData.class)).thenReturn(any(JsonData.class));
//
//        jsonDataService.init();
//
//        assertThat(jsonDataService.getJsonData()).isNotNull();
//    }

//    @DisplayName("Reading JSON path fail")
//    @Test
//    public void test_jsonLoading_wrongJsonPath() throws IOException {
//        ReflectionTestUtils.setField(jsonDataService, "jsonPath", "bad_path/data.json");
//
//        when(classPathResource.getInputStream()).thenThrow(new IOException());
//
//        assertThrows(RuntimeException.class, () -> jsonDataService.init());
//    }

//    @Tag("Initialization")
//    @DisplayName("Reading the JSON stream fail")
//    @Test
//    public void test_jsonLoading_wrongParsingJsonPath() throws IOException {
//        ReflectionTestUtils.setField(jsonDataService, "jsonPath", "json/data.json");
//
//        when(objectMapper.readValue(any(InputStream.class), eq(JsonData.class))).thenThrow(new IOException());
//
//        assertThrows(JsonStreamReadException.class, () -> jsonDataService.init());
//    }
//
//    @Tag("Initialization")
//    @DisplayName("JSON binding to Object fail")
//    @Test
//    public void test_jsonLoading_wrongDataBindingJsonData() throws IOException {
//        ReflectionTestUtils.setField(jsonDataService, "jsonPath", "json/data.json");
//
//        when(objectMapper.readValue(any(InputStream.class), eq(JsonData.class))).thenThrow(new IOException());
//
//        assertThrows(JsonDatabindException.class, () -> jsonDataService.init());
//    }
}
