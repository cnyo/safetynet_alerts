
package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Tag("FireStation")
public class MedicalRecordServiceTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.MedicalRecordService";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Mock
    MedicalRecordRepository medicalRecordRepository;

    @Mock
    private JsonDataService jsonDataService;

    private MedicalRecordService medicalRecordService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("MedicalRecordServiceTest");
    }

    @BeforeEach
    public void setUp() {
        doNothing().when(jsonDataService).init(anyString());

        medicalRecordService = new MedicalRecordService(medicalRecordRepository);

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

    @Tag("Create")
    @DisplayName("Try to create one medical record successfully")
    @Test
    public void createSuccessfully() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        MedicalRecord medicalRecord = medicalRecordService.create(mockMedicalRecord);

        assertThat(medicalRecord).isNotNull();
        assertThat(medicalRecord.getFullName()).isEqualTo("John Doe");
        assertThat(medicalRecord.getBirthdate()).isEqualTo("08/08/1988");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord created successfully", Level.DEBUG)).hasSize(1);
    }

    @Tag("Create")
    @DisplayName("Try to create with person not exists")
    @Test
    public void createWithPersonNotFound() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }


    @Tag("Create")
    @DisplayName("Try to create with medical record already exists")
    @Test
    public void createWithMedicalRecordAlreadyExists() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new InstanceAlreadyExistsException());

        assertThrows(InstanceAlreadyExistsException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }

    @Tag("Update")
    @DisplayName("Try to update medical success")
    @Test
    public void updateSuccess() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        MedicalRecord medicalRecord = medicalRecordService.update(mockMedicalRecord);

        assertThat(medicalRecord).isNotNull();
        assertThat(medicalRecord.getFullName()).isEqualTo("John Doe");
        assertThat(medicalRecord.getBirthdate()).isEqualTo("08/08/1988");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord updated successfully", Level.DEBUG)).hasSize(1);
    }

    @Tag("Update")
    @DisplayName("Try to update with medical record not exists")
    @Test
    public void updateWithMedicalRecordNotExists() throws NoSuchElementException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> medicalRecordService.update(mockMedicalRecord));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(0);
    }

    @Tag("Remove")
    @DisplayName("Try to remove medical record success")
    @Test
    public void removeSuccess() {
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(true);

        boolean result = medicalRecordService.remove(anyString(), anyString());

        assertThat(result).isTrue();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord removed: success", Level.DEBUG)).hasSize(1);
    }

    @Tag("Remove")
    @DisplayName("Try to remove medical record not found")
    @Test
    public void removeWithNoMedicalRecordFound() {
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(false);

        boolean result = medicalRecordService.remove(anyString(), anyString());

        assertThat(result).isFalse();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord removed: failure", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to remove medical record not found")
    @Test
    public void getAllSuccess() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.findAll()).thenReturn(List.of(mockMedicalRecord));

        List<MedicalRecord> result = medicalRecordService.getAll();

        assertThat(result).isNotEmpty();
        assertThat(result).size().isGreaterThan(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("getAll medical records: 1", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to remove medical record not found")
    @Test
    public void getAllNoMedicalRecordsFound() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.findAll()).thenReturn(Collections.emptyList());

        List<MedicalRecord> result = medicalRecordService.getAll();

        assertThat(result).isEmpty();
        assertThat(result).size().isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("getAll medical records: 0", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get medical record by fullName success")
    @Test
    public void getAllByFullNameSuccess() {
        Map<String, MedicalRecord> medicalRecordByFullName = new HashMap<>();
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        medicalRecordByFullName.put(mockMedicalRecord.getFullName(), mockMedicalRecord);

        when(medicalRecordRepository.getAllByFullName()).thenReturn(medicalRecordByFullName);

        Map<String, MedicalRecord> result = medicalRecordService.getAllByFullName();

        assertThat(result).containsKey(mockMedicalRecord.getFullName());
        assertThat(result.get(mockMedicalRecord.getFullName())).isEqualTo(mockMedicalRecord);
        assertThat(result).size().isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Medical records ordered by fullName found: 1", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get none medical record by fullName")
    @Test
    public void getAllByFullNameNoMedicalRecordsFound() {
        Map<String, MedicalRecord> medicalRecordByFullName = new HashMap<>();

        when(medicalRecordRepository.getAllByFullName()).thenReturn(medicalRecordByFullName);

        Map<String, MedicalRecord> result = medicalRecordService.getAllByFullName();

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Medical records ordered by fullName found: 0", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult from fullNames success")
    @Test
    public void countAdultFromFullNameSuccess() {
        List<String> fullNames = Arrays.asList("Diane Doe", "Bob Doe", "John Doe");

        when(medicalRecordRepository.countAdultFromFullName(anyList())).thenReturn(2);

        int result = medicalRecordService.countAdultFromFullName(fullNames);

        assertThat(result).isEqualTo(2);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 2 adult from fullNames", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult with empty fullNames")
    @Test
    public void countAdultFromFullNameWithEmptyFullNames() {
        int result = medicalRecordService.countAdultFromFullName(Collections.emptyList());

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count adults, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult with null fullNames")
    @Test
    public void countAdultFromFullNameWithNullFullNames() {
        int result = medicalRecordService.countAdultFromFullName(null);

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count adults, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count children from fullNames success")
    @Test
    public void countChildrenFromFullNameSuccess() {
        List<String> fullNames = Arrays.asList("Diane Doe", "Bob Doe", "John Doe");

        when(medicalRecordRepository.countChildrenFromFullName(anyList())).thenReturn(1);

        int result = medicalRecordService.countChildrenFromFullName(fullNames);

        assertThat(result).isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 1 children from fullNames", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult from empty fullNames")
    @Test
    public void countChildrenFromFullNameWithEmptyFullNames() {
        int result = medicalRecordService.countChildrenFromFullName(Collections.emptyList());

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count children, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult from null fullNames")
    @Test
    public void countChildrenFromFullNameWithNullFullNames() {
        int result = medicalRecordService.countChildrenFromFullName(null);

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count children, returning 0.", Level.DEBUG)).hasSize(1);
    }
}
