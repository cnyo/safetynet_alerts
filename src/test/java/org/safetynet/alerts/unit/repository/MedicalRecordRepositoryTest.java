
package org.safetynet.alerts.unit.repository;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@Tag("FireStation")
public class MedicalRecordRepositoryTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.MedicalRecordService";
    private final String MSG = "Mon message de test";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @InjectMocks
    MedicalRecordRepository medicalRecordRepository;

    @Spy
    private JsonDataService jsonDataService;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

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

        when(jsonDataService.getJsonData()).thenReturn(jsonData);
        when(jsonDataService.getJsonData().getMedicalrecords()).thenReturn(new ArrayList<>());

        medicalRecordRepository = new MedicalRecordRepository(jsonDataService, personRepository);
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
    public void test_create_successfully() throws InstanceAlreadyExistsException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(personRepository.findOneByFullName(any(String.class))).thenReturn(Optional.of(mockPerson));

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
    public void test_create_withPersonNotFound() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(personRepository.findOneByFullName(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }


    @Tag("Create")
    @DisplayName("Try to create with medical record already exists")
    @Test
    public void test_create_withMedicalRecordAlreadyExists() {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(personRepository.findOneByFullName(any(String.class))).thenReturn(Optional.of(mockPerson));
        when(jsonData.getMedicalrecords()).thenReturn(List.of(mockMedicalRecord));

        assertThrows(InstanceAlreadyExistsException.class, () -> medicalRecordService.create(mockMedicalRecord));
    }

    @Tag("Update")
    @DisplayName("Try to update medical success")
    @Test
    public void test_update_success() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(jsonData.getMedicalrecords()).thenReturn(List.of(mockMedicalRecord));

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
    public void test_update_withMedicalRecordNotExists() {
        MedicalRecord mockMedicalRecord = new MedicalRecord();
        mockMedicalRecord.setBirthdate("08/08/1988");
        mockMedicalRecord.setFirstName("John");
        mockMedicalRecord.setLastName("Doe");

        when(jsonData.getMedicalrecords()).thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> medicalRecordService.update(mockMedicalRecord));
    }

    @Tag("Remove")
    @DisplayName("Try to remove medical record success")
    @Test
    public void test_remove_success() {
    }

    @Tag("Remove")
    @DisplayName("Try to remove medical record not found")
    @Test
    public void test_remove_notExisting() {
    }
}
