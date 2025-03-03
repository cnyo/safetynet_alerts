
package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.FireStationService;
import org.safetynet.alerts.service.JsonDataService;
import org.safetynet.alerts.service.MedicalRecordService;
import org.safetynet.alerts.service.PersonService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Tag("FireStation")
public class PersonServiceTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.PersonService";
    private final String MSG = "Mon message de test";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Mock
    private JsonDataService jsonDataService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private FireStationService fireStationService;

    @Mock
    private PersonDtoMapper personDtoMapper;

    @Mock
    private PersonRepository personRepository;

    private PersonService personService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("PersonServiceTest");
    }

    @BeforeEach
    public void setUp() {
        doNothing().when(jsonDataService).init(anyString());

        personService = new PersonService(personRepository, fireStationService, medicalRecordService, personDtoMapper);

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);

        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        memoryAppender.start();
    }

    @Tag("Create")
    @DisplayName("Try to create one person success")
    @Test
    public void createSuccess() throws IllegalArgumentException, InstanceAlreadyExistsException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        when(personRepository.create(any(Person.class))).thenReturn(mockPerson);

        Person result = personService.create(mockPerson);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Person created", Level.DEBUG)).hasSize(1);
    }

    @Tag("Create")
    @DisplayName("Try to create one person successfully")
    @Test
    public void createReturnIllegalArgumentException() throws IllegalArgumentException, InstanceAlreadyExistsException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        when(personRepository.create(any(Person.class))).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> personService.create(mockPerson));
    }

    @Tag("Create")
    @DisplayName("Try to create one person successfully")
    @Test
    public void createInstanceAlreadyExistsException() throws InstanceAlreadyExistsException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        when(personRepository.create(any(Person.class))).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> personService.create(mockPerson));
    }

    @Tag("Update")
    @DisplayName("Try to update one person success")
    @Test
    public void updateSuccess() throws InstanceNotFoundException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        when(personRepository.update(any(Person.class))).thenReturn(mockPerson);

        Person result = personService.update(mockPerson);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Person updated", Level.DEBUG)).hasSize(1);
    }

    @Tag("Update")
    @DisplayName("Try to update one person no exists")
    @Test
    public void updatePersonNotFound() throws InstanceNotFoundException {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");

        when(personRepository.update(any(Person.class))).thenThrow(new InstanceNotFoundException());

        assertThrows(InstanceNotFoundException.class, () -> personService.update(mockPerson));
    }

    @Tag("Remove")
    @DisplayName("Try to update one person success")
    @Test
    public void removeSuccess() {
        when(personRepository.remove(anyString())).thenReturn(true);

        boolean result = personService.remove("John", "Doe");

        assertThat(result).isTrue();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Removed person successfully", Level.DEBUG)).hasSize(1);
    }

    @Tag("Remove")
    @DisplayName("Try to update one person success")
    @Test
    public void removePersonNotFound() {
        when(personRepository.remove(anyString())).thenReturn(false);

        boolean result = personService.remove("John", "Doe");

        assertThat(result).isFalse();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Removed person failed", Level.DEBUG)).hasSize(1);
    }

    @Tag("Get")
    @DisplayName("Try to get all phone number at address")
    @Test
    public void getAllPhoneNumberFromAddressesSuccess() {
        List<String> phones = Arrays.asList("101-854-7794", "842-574-7754");
        List<String> addresses = Arrays.asList("1 rue sesame", "21 jump street");
        String phonePattern = "\\d{10}|(?:\\d{3}-){2}\\d{4}";

        when(personRepository.findPhoneNumbersFromAddresses(any())).thenReturn(phones);

        List<String> result = personService.getAllPhoneNumberFromAddresses(addresses);

        assertThat(result.getFirst().matches(phonePattern)).isTrue();
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst()).isEqualTo("101-854-7794");
        assertThat(result.getLast()).isEqualTo("842-574-7754");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search(result.size() + " phone numbers found", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all phone number with null address")
    @Test
    public void getAllPhoneNumberFromAddressesWithNullAddress() {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPhoneNumberFromAddresses(null));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No addresses provided", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all phone number with empty address")
    @Test
    public void getAllPhoneNumberFromAddressesWithEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPhoneNumberFromAddresses(Collections.emptyList()));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No addresses provided", Level.DEBUG)).hasSize(1);
    }


    @Tag("GetTest")
    @DisplayName("Try to get person with lastName success")
    @Test
    public void getAllPersonByLastNameSuccess() {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);

        when(personRepository.findAllPersonByLastName(anyString())).thenReturn(mockPersons);

        List<Person> result = personService.getAllPersonByLastName("21 jump street");

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getFullName()).isEqualTo(mockPerson.getFullName());
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 1 persons by lastname", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try not found person with lastName success")
    @Test
    public void getAllPersonByLastNameNotFoundSuccess() {
        when(personRepository.findAllPersonByLastName(anyString())).thenReturn(new ArrayList<>());

        List<Person> result = personService.getAllPersonByLastName("21 jump street");

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 0 persons by lastname", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to found person with lastName fail")
    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAllPersonByLastNameWithNullFullNamesFail(String fullName) {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPersonByLastName(fullName));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Last name cannot be null or empty", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons at address success")
    @Test
    public void getAllPersonAtAddressSuccess() {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);

        when(personRepository.findAllPersonAtAddress(anyString())).thenReturn(mockPersons);

        List<Person> result = personService.getAllPersonAtAddress("21 jump street");

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getFullName()).isEqualTo(mockPerson.getFullName());
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("1 person(s) found at address", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons with empty address fail")
    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAllPersonAtAddressWithEmptyAddressFail(String address) {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPersonAtAddress(address));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Address cannot be empty", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons from fire station success")
    @Test
    public void getAllPersonFromFireStationSuccess() {
        List<String> addresses = new ArrayList<>();
        addresses.add("21 jump street");
        addresses.add("1 sesame street");

        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);

        when(fireStationService.getAddressesForOneFireStation(anyString())).thenReturn(addresses);
        when(personRepository.findAllPersonFromAddresses(any())).thenReturn(mockPersons);

        List<Person> result = personService.getAllPersonFromFireStation("2");

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getFullName()).isEqualTo(mockPerson.getFullName());
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Found 2 addresses for station 2", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("Found 1 persons for station 2", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons with station not found")
    @Test
    public void getAllPersonFromFireStationWithStationNotFound() {
        when(fireStationService.getAddressesForOneFireStation(anyString())).thenReturn(Collections.emptyList());
        when(personRepository.findAllPersonFromAddresses(any())).thenReturn(Collections.emptyList());

        List<Person> result = personService.getAllPersonFromFireStation("2");

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("Found 0 addresses for station 2", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("Found 0 persons for station 2", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons with empty station fail")
    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAllPersonFromFireStationWithEmptyStation(String stationNumber) {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPersonFromFireStation(stationNumber));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("stationNumber cannot be null or empty", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons from address success")
    @Test
    public void getAllPersonFromAddressesSuccess() {
        List<String> addresses = Arrays.asList("1 rue sesame", "21 jump street");
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);

        when(personRepository.findAllPersonFromAddresses(any())).thenReturn(mockPersons);

        List<Person> result = personService.getAllPersonFromAddresses(addresses);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getFullName()).isEqualTo(mockPerson.getFullName());
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 1 persons from addresses", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons from empty address fail")
    @Test
    public void getAllPersonFromAddressesWithNullAddresses() {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPersonFromAddresses(null));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("addresses cannot be null", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all persons success")
    @Test
    public void getAllSuccess() {
        List<Person> mockPersons = Arrays.asList(new Person(), new Person());

        when(personRepository.findAll()).thenReturn(mockPersons);

        List<Person> result = personService.getAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 2 persons", Level.DEBUG)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to find none persons success")
    @Test
    public void getAllPersonFromAddressesNobodyFoundSuccess() {
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        List<Person> result = personService.getAll();

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 0 persons", Level.DEBUG)).hasSize(1);
    }

    @Tag("Other")
    @DisplayName("Try to attach person to ChildAlertDto success")
    @Test
    public void attachOtherPersonToChildAlertDtoSuccess() {
        String address = "1 rue sesame";
        int age = 15;
        Person mockPerson = new Person();
        Person mockOtherPerson = new Person();
        mockPerson
                .setFirstName("John")
                .setLastName("Doe")
                .setAddress(address)
                .setCity("San Francisco")
                .setZip("97451")
                .setEmail("john@doe.com")
                .setPhone("841-874-7784");
        mockOtherPerson
                .setFirstName("Leonard")
                .setLastName("Doe")
                .setAddress(address)
                .setCity("San Francisco")
                .setZip("97451")
                .setEmail("john@doe.com")
                .setPhone("841-874-7784");

        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        medicalRecordMap.put("John Doe", new MedicalRecord());

        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);
        mockPersons.add(mockOtherPerson);

        Map<String, ChildAlertDto> childAlerts = new HashMap<>();
        childAlerts.put(mockPerson.getFullName(), new ChildAlertDto(mockPerson, age));

        when(medicalRecordService.getAllByFullName()).thenReturn(medicalRecordMap);
        when(personRepository.findAllPersonAtAddress(anyString())).thenReturn(mockPersons);
        when(personDtoMapper.toChildAlertDto(anyList(), anyString(), anyMap())).thenReturn(childAlerts);

        List<ChildAlertDto> result = personService.attachOtherPersonToChildAlertDto(address);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().address).isEqualTo(address);
        assertThat(result.getFirst().age).isEqualTo(age);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(mockPersons.size() + 2);
        assertThat(memoryAppender.search("0 other person(s) household added for ChildAlertDto at address " + address, Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("1 other person(s) household added for ChildAlertDto at address " + address, Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("ChildPersonDto mapped for 1 children at address " + address, Level.DEBUG)).hasSize(1);
    }

    @Tag("Other")
    @DisplayName("Try to attach with no children found at this address")
    @Test
    public void attachOtherPersonToChildAlertDtoNoChildrenFoundAtAddress() {
        String address = "1 rue sesame";
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        List<Person> mockPersons = new ArrayList<>();
        Map<String, ChildAlertDto> mockChildAlerts = new HashMap<>();

        when(medicalRecordService.getAllByFullName()).thenReturn(medicalRecordMap);
        when(personRepository.findAllPersonAtAddress(anyString())).thenReturn(mockPersons);
        when(personDtoMapper.toChildAlertDto(anyList(), anyString(), anyMap())).thenReturn(mockChildAlerts);

        List<ChildAlertDto> result = personService.attachOtherPersonToChildAlertDto(address);

        MemoryAppender memoryAppender1 = memoryAppender;

        assertThat(result).isEmpty();
        assertThat(result).size().isEqualTo(0);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(2);
        assertThat(memoryAppender.search("0 person(s) found at address", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("No children found at this address", Level.DEBUG)).hasSize(1);
    }

    @Tag("Other")
    @DisplayName("Try to get all emails at city success")
    @Test
    public void getAllEmailsAtCitySuccess() {
        List<String> fullNames = Arrays.asList("John Doe", "Leonard Doe");

        when(personRepository.findAllEmailsAtCity(anyString())).thenReturn(fullNames);
        List<String> result = personService.getAllEmailsAtCity("Culver");

        assertThat(result).isNotEmpty();
        assertThat(result).size().isEqualTo(2);
        assertThat(result.getFirst()).isEqualTo("John Doe");
        assertThat(result.getLast()).isEqualTo("Leonard Doe");
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 2 full names from city Culver", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to get all emails with empty city fail")
    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getAllEmailsAtCityWithEmptyCityFail(String city) {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllEmailsAtCity(city));

        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("City name cannot be null or empty", Level.DEBUG)).hasSize(1);
    }

    @Tag("Other")
    @DisplayName("Try to get all fullNames from persons success")
    @Test
    public void getFullNamesFromPersonsSuccess() {
        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("Doe");
        List<Person> mockPersons = new ArrayList<>();
        mockPersons.add(mockPerson);

        List<String> result = personService.getFullNamesFromPersons(mockPersons);

        assertThat(result).isNotEmpty();
        assertThat(result).size().isEqualTo(1);
        assertThat(result.getFirst()).isEqualTo(mockPerson.getFullName());
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Found 1 full names", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to get all fullNames with empty persons fail")
    @Test
    public void getFullNamesFromPersonsWithEmptyPersonsFail() {
        List<String> result = personService.getFullNamesFromPersons(new ArrayList<>());

        assertThat(result).isEmpty();
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No persons found", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to get all fullNames with null persons fail")
    @Test
    public void getFullNamesFromPersonsWithNullPersonsFail() {
        assertThrows(IllegalArgumentException.class, () -> personService.getFullNamesFromPersons(null));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Null argument is invalid", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult from fullNames success")
    @Test
    public void countAdultFromPersonsSuccess() {
        when(medicalRecordService.countAdultFromFullName(anyList())).thenReturn(5);

        int result = personService.countAdultFromPersons(anyList());

        assertThat(result).isEqualTo(5);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 5 adults", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count adult with null fullNames fail")
    @Test
    public void countAdultFromPersonsWithNullFullNamesFail() {
        assertThrows(IllegalArgumentException.class, () -> personService.countAdultFromPersons(null));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("fullNames adults cannot be null", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count children from fullNames success")
    @Test
    public void countChildrenFromPersonsSuccess() {
        when(medicalRecordService.countChildrenFromFullName(anyList())).thenReturn(5);

        int result = personService.countChildrenFromPersons(anyList());

        assertThat(result).isEqualTo(5);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("Count 5 children", Level.DEBUG)).hasSize(1);
    }

    @Tag("OtherTest")
    @DisplayName("Try to count children with null fullNames fail")
    @Test
    public void countChildrenFromPersonsWithNullFullNamesFail() {
        assertThrows(IllegalArgumentException.class, () -> personService.countChildrenFromPersons(null));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("fullNames children cannot be null", Level.DEBUG)).hasSize(1);
    }

}
