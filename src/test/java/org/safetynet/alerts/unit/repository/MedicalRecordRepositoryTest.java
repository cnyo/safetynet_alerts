package org.safetynet.alerts.unit.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.MedicalRecordRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.management.InstanceAlreadyExistsException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Tag("MedicalRecordRepositoryTest")
public class MedicalRecordRepositoryTest {

    private static MedicalRecordRepository medicalRecordRepository;

    @Mock
    private static PersonRepository personRepository;

    private static final String jsonPath = "data.json";

    private JsonData jsonData;

    MockedStatic<JsonDataService> jsonDataServiceMock;

    @BeforeEach
    public void init() {
        medicalRecordRepository = new MedicalRecordRepository(personRepository);
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStreamJson = new ClassPathResource(jsonPath).getInputStream()) {
            jsonData = objectMapper.readValue(inputStreamJson, JsonData.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        jsonDataServiceMock = Mockito.mockStatic(JsonDataService.class);
        jsonDataServiceMock.when(JsonDataService::getJsonData).thenReturn(jsonData);

    }

    @AfterEach
    public void tearDown() {
        jsonDataServiceMock.close();
    }

    @Test
    public void createForExistingPersonShouldReturnCreatedMedicalRecord() throws InstanceAlreadyExistsException {
        String firstname = "John";
        String lastname = "Doe";
        String birthdate = LocalDate.now().minusYears(25).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(firstname);
        medicalRecord.setLastName(lastname);
        medicalRecord.setBirthdate(birthdate);

        Person person = new Person();
        person.setFirstName(firstname);
        person.setLastName(lastname);

        when(personRepository.findOneByFullName(anyString())).thenReturn(Optional.of(person));

        Optional<MedicalRecord> result = Optional.ofNullable(medicalRecordRepository.create(medicalRecord));

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getFirstName()).isEqualTo(firstname);
        assertThat(result.get().getLastName()).isEqualTo(lastname);
        assertThat(result.get().getBirthdate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).isEqualTo(birthdate);
        assertThat(result.get().getAge()).isEqualTo(24);
    }

    @Test
    public void createForNotExistingPersonShouldReturnException() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName( "Doe");

        when(personRepository.findOneByFullName(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> medicalRecordRepository.create(medicalRecord));

        verify(personRepository, times(1)).findOneByFullName(anyString());
    }

    @Test
    public void createAlreadyExistingMedicalRecordShouldReturnException() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName( "Boyd");
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");

        when(personRepository.findOneByFullName(anyString())).thenReturn(Optional.of(person));

        assertThrows(InstanceAlreadyExistsException.class, () -> medicalRecordRepository.create(medicalRecord));
        verify(personRepository, times(1)).findOneByFullName(anyString());
    }

    @Test
    public void updateMedicalRecordShouldReturnMedicalRecordUpdated() {
        String firstname = "John";
        String lastname = "Boyd";
        String birthdate = LocalDate.now().minusYears(25).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName(firstname);
        medicalRecord.setLastName(lastname);
        medicalRecord.setBirthdate(birthdate);
        medicalRecord.setMedications(List.of("Doliprane"));

        MedicalRecord result = medicalRecordRepository.update(medicalRecord);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(firstname);
        assertThat(result.getLastName()).isEqualTo(lastname);
        assertThat(result.getBirthdate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).isEqualTo(birthdate);
        assertThat(result.getAge()).isEqualTo(24);
    }

    @Test
    public void updateNotFoundMedicalRecordShouldReturnException() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");

        assertThrows(NoSuchElementException.class, () -> medicalRecordRepository.update(medicalRecord));
    }

    @Test
    public void removeMedicalRecordShouldReturnTrue() {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Boyd");

        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(23);

        boolean result = medicalRecordRepository.remove("John", "Boyd");
        Optional<MedicalRecord> emptyMedicalRecord = jsonData.getMedicalrecords().stream()
                .filter(mr -> mr.equals(medicalRecord)).findFirst();

        assertThat(result).isTrue();
        assertThat(jsonData.getMedicalrecords().size()).isEqualTo(22);
        assertThat(emptyMedicalRecord).isEmpty();
    }

    @Test
    public void removeNotFoundMedicalRecordShouldReturnFalse() {
        boolean result = medicalRecordRepository.remove("John", "Doe");

        assertThat(result).isFalse();
    }

    @Test
    public void findOneByFullNameShouldReturnMedicalRecord() {
        Optional<MedicalRecord> result = medicalRecordRepository.findOneByFullName("John Boyd");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getFullName()).isEqualTo("John Boyd");
        assertThat(result.get()).isInstanceOf(MedicalRecord.class);
    }

    @Test
    public void findOneByFullNameShouldReturnNoResult() {
        Optional<MedicalRecord> result = medicalRecordRepository.findOneByFullName("John Doe");

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findAllShouldReturnMedicalRecords() {
        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(23);
    }

    @Test
    public void countAdultFromFullNameShouldReturnInteger() {
        List<String> fullnames = new ArrayList<>();
        fullnames.add("John Boyd");
        fullnames.add("Zach Zemicks");
        fullnames.add("Peter Duncan");

        int result = medicalRecordRepository.countAdultFromFullName(fullnames);

        assertThat(result).isEqualTo(2);
    }

    @Test
    public void countAdultFromFullNameShouldReturn0() {
        List<String> fullnames = new ArrayList<>();
        fullnames.add("Zach Zemicks");
        fullnames.add("Abcd");

        int result = medicalRecordRepository.countAdultFromFullName(fullnames);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void countChildrenFromFullNameShouldReturnInteger() {
        List<String> fullnames = new ArrayList<>();
        fullnames.add("John Boyd");
        fullnames.add("Zach Zemicks");

        int result = medicalRecordRepository.countChildrenFromFullName(fullnames);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void countChildrenFromFullNameShouldReturn0() {
        List<String> fullnames = new ArrayList<>();
        fullnames.add("John Boyd");
        fullnames.add("Abcd");

        int result = medicalRecordRepository.countChildrenFromFullName(fullnames);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void getAllByFullNameShouldReturnInteger() {
        Map<String, MedicalRecord> result = medicalRecordRepository.getAllByFullName();

        assertThat(result.size()).isEqualTo(23);
        assertThat(result.containsKey("Zach Zemicks")).isTrue();
        assertThat(result.get("Zach Zemicks").getFullName()).isEqualTo("Zach Zemicks");
    }
}
