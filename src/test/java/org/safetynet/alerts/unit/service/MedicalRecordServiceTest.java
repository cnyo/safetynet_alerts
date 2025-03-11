
package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Tag("FireStation")
public class MedicalRecordServiceTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.MedicalRecordService";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Mock
    MedicalRecordRepository medicalRecordRepository;

    private MedicalRecordService medicalRecordService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("MedicalRecordServiceTest");
    }

    @BeforeEach
    public void setUp() {
        medicalRecordService = new MedicalRecordService(medicalRecordRepository);

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

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
        assertThat(medicalRecord.getBirthdate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).isEqualTo("08/08/1988");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord created successfully", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void createWithBirthdateInFutureShouldThrowException() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/2030");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        assertThrows(DateTimeException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }

    @Test
    public void createWithPersonNotFound() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }

    @Test
    public void createWithMedicalRecordAlreadyExists() throws InstanceAlreadyExistsException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.create(any(MedicalRecord.class))).thenThrow(new InstanceAlreadyExistsException());

        assertThrows(InstanceAlreadyExistsException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }

    @Test
    public void updateSuccess() throws InstanceNotFoundException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("12/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        MedicalRecord medicalRecord = medicalRecordService.update(mockMedicalRecord);

        assertThat(medicalRecord).isNotNull();
        assertThat(medicalRecord.getFullName()).isEqualTo("John Doe");
        assertThat(medicalRecord.getBirthdate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).isEqualTo("12/08/1988");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord updated successfully", Level.DEBUG)).hasSize(1);
    }

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

    @Test
    public void updateMedicalRecordWithBirthdateInFutureShouldThrowException() throws NoSuchElementException {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/2030");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(medicalRecordRepository.update(any(MedicalRecord.class))).thenReturn(mockMedicalRecord);

        assertThrows(DateTimeException.class, () -> medicalRecordService.update(mockMedicalRecord));
    }

    @Test
    public void removeSuccess() {
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(true);

        boolean result = medicalRecordService.remove(anyString(), anyString());

        assertThat(result).isTrue();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord removed: success", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void removeWithNoMedicalRecordFound() {
        when(medicalRecordRepository.remove(anyString(), anyString())).thenReturn(false);

        boolean result = medicalRecordService.remove(anyString(), anyString());

        assertThat(result).isFalse();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord removed: failure", Level.DEBUG)).hasSize(1);
    }

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

    @Test
    public void getAllByFullNameNoMedicalRecordsFound() {
        Map<String, MedicalRecord> medicalRecordByFullName = new HashMap<>();

        when(medicalRecordRepository.getAllByFullName()).thenReturn(medicalRecordByFullName);

        Map<String, MedicalRecord> result = medicalRecordService.getAllByFullName();

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Medical records ordered by fullName found: 0", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countAdultFromFullNameSuccess() {
        List<String> fullNames = Arrays.asList("Diane Doe", "Bob Doe", "John Doe");

        when(medicalRecordRepository.countAdultFromFullName(anyList())).thenReturn(2);

        int result = medicalRecordService.countAdultFromFullName(fullNames);

        assertThat(result).isEqualTo(2);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 2 adult from fullNames", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countAdultFromFullNameWithEmptyFullNames() {
        int result = medicalRecordService.countAdultFromFullName(Collections.emptyList());

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count adults, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countAdultFromFullNameWithNullFullNames() {
        int result = medicalRecordService.countAdultFromFullName(null);

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count adults, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countChildrenFromFullNameSuccess() {
        List<String> fullNames = Arrays.asList("Diane Doe", "Bob Doe", "John Doe");

        when(medicalRecordRepository.countChildrenFromFullName(anyList())).thenReturn(1);

        int result = medicalRecordService.countChildrenFromFullName(fullNames);

        assertThat(result).isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 1 children from fullNames", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countChildrenFromFullNameWithEmptyFullNames() {
        int result = medicalRecordService.countChildrenFromFullName(Collections.emptyList());

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count children, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void countChildrenFromFullNameWithNullFullNames() {
        int result = medicalRecordService.countChildrenFromFullName(null);

        assertThat(result).isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No fullNames provided for count children, returning 0.", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getOneByNameShouldReturnMedicalRecord() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");

        when(medicalRecordRepository.findOneByFullName(anyString())).thenReturn(Optional.of(medicalRecord));

        MedicalRecord result = medicalRecordService.getOneByName("John", "Doe");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MedicalRecord.class);
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord found by one name: true", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getOneByNameShouldReturnNull() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");

        when(medicalRecordRepository.findOneByFullName(anyString())).thenReturn(Optional.ofNullable(null));

        MedicalRecord result = medicalRecordService.getOneByName("John", "Doe");

        assertThat(result).isNull();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("MedicalRecord found by one name: false", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void validateBirthdateShouldBeValid() {
        LocalDate birthdate = LocalDate.parse("02/18/2001", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        boolean result = medicalRecordService.validateBirthdate(birthdate);

        assertThat(result).isTrue();
    }

    @Test
    public void validateBirthdateInFutureShouldDateTimeParseException() {
        LocalDate birthdate = LocalDate.parse("02/12/2030", DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        boolean result = medicalRecordService.validateBirthdate(birthdate);

        assertThat(result).isFalse();
    }
}
