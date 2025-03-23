
package org.safetynet.alerts.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.safetynet.alerts.LogWorker;
import org.safetynet.alerts.controller.PersonDtoMapper;
import org.safetynet.alerts.dto.FireInfoDto;
import org.safetynet.alerts.dto.person.ChildAlertDto;
import org.safetynet.alerts.logging.MemoryAppender;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.MedicalRecord;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonJsonRepository;
import org.safetynet.alerts.service.*;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Tag("FireStation")
public class PersonServiceImplTest {

    private final String LOGGER_NAME = "org.safetynet.alerts.service.PersonServiceImpl";

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @Mock
    private MedicalRecordServiceImpl medicalRecordService;

    @Mock
    private FireStationServiceImpl fireStationService;

    @Mock
    private PersonJsonRepository personRepository;

    @Mock
    private PersonDtoMapper personDtoMapper;

    @InjectMocks
    private PersonServiceImpl personService;

    @BeforeAll
    public static void beforeAll() {
        LogWorker worker = new LogWorker();
        worker.generateLogs("PersonServiceTest");
    }

    @BeforeEach
    public void setUp() {
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
    @DisplayName("Try to create one person success")
    @Test
    public void createWithNullPersonShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> personService.create(null));
    }

    @Tag("Create")
    @DisplayName("Try to create one person success")
    @Test
    public void createWithBlankFirstNameShouldThrowException() {
        Person person = new Person();
        person.setLastName("Doe");

        assertThrows(IllegalArgumentException.class, () -> personService.create(person));
    }

    @Tag("Create")
    @DisplayName("Try to create one person success")
    @Test
    public void createWithBlankLastNameShouldThrowException() {
        Person person = new Person();
        person.setFirstName("John");

        assertThrows(IllegalArgumentException.class, () -> personService.create(person));
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

    @Tag("Update")
    @DisplayName("Try to update one person no exists")
    @Test
    public void updateWithNullPersonShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> personService.update(null));
    }

    @Tag("Update")
    @DisplayName("Try to update one person no exists")
    @Test
    public void updateWithBlankFirstNameShouldThrowException() {
        Person person = new Person();
        person.setLastName("Doe");

        assertThrows(IllegalArgumentException.class, () -> personService.update(person));
    }

    @Tag("Update")
    @DisplayName("Try to update one person no exists")
    @Test
    public void updateWithBlankLastNameShouldThrowException() {
        Person person = new Person();
        person.setFirstName("John");

        assertThrows(IllegalArgumentException.class, () -> personService.update(person));
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
        assertThat(memoryAppender.search("No addresses provided", Level.ERROR)).hasSize(1);
    }

    @Tag("GetTest")
    @DisplayName("Try to get all phone number with empty address")
    @Test
    public void getAllPhoneNumberFromAddressesWithEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> personService.getAllPhoneNumberFromAddresses(Collections.emptyList()));
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
        assertThat(memoryAppender.search("No addresses provided", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("Last name cannot be null or empty", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("Address cannot be empty", Level.ERROR)).hasSize(1);
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

        when(fireStationService.getAddressesForFireStation(anyString())).thenReturn(addresses);
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
        when(fireStationService.getAddressesForFireStation(anyString())).thenReturn(Collections.emptyList());
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
        assertThat(memoryAppender.search("stationNumber cannot be null or empty", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("addresses cannot be null", Level.ERROR)).hasSize(1);
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
        // GIVEN
        String address = "1 rue sesame";
        int age = 15;

        Person child = new Person();
        child.setFirstName("John");
        child.setLastName("Doe");
        child.setAddress(address);
        child.setCity("San Francisco");
        child.setZip("97451");
        child.setEmail("john@doe.com");
        child.setPhone("841-874-7784");

        Person adult = new Person();
        adult.setFirstName("Leonard");
        adult.setLastName("Doe");
        adult.setAddress(address);
        adult.setCity("San Francisco");
        adult.setZip("97451");
        adult.setEmail("john@doe.com");
        adult.setPhone("841-874-7784");

        List<Person> persons = new ArrayList<>();
        persons.add(child);
        persons.add(adult);

        String childBirthdate = LocalDate.now().minusYears(5).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String adultBirthdate = LocalDate.now().minusYears(20).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        MedicalRecord childMedicalRecord = new MedicalRecord();
        childMedicalRecord.setFirstName(child.getFirstName());
        childMedicalRecord.setLastName(child.getLastName());
        childMedicalRecord.setBirthdate(childBirthdate);

        MedicalRecord adultMedicalRecord = new MedicalRecord();
        adultMedicalRecord.setFirstName(child.getFirstName());
        adultMedicalRecord.setLastName(child.getLastName());
        adultMedicalRecord.setBirthdate(adultBirthdate);

        Map<String, ChildAlertDto> childAlerts = new HashMap<>();
        childAlerts.put(child.getFullName(), new ChildAlertDto(child, age));

        // WHEN
        List<ChildAlertDto> result = personService.attachOtherPersonToChildAlertDto(childAlerts, persons);

        // THEN
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().address).isEqualTo(address);
        assertThat(result.getFirst().otherPersons.size()).isEqualTo(1);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(3);
        assertThat(memoryAppender.search("0 other person(s) household added for ChildAlertDto", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("1 other person(s) household added for ChildAlertDto", Level.DEBUG)).hasSize(1);
        assertThat(memoryAppender.search("ChildPersonDto mapped for 1 children at address", Level.DEBUG)).hasSize(1);
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
        assertThat(memoryAppender.search("City name cannot be null or empty", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("Null argument is invalid", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("fullNames adults cannot be null", Level.ERROR)).hasSize(1);
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
        assertThat(memoryAppender.search("fullNames children cannot be null", Level.ERROR)).hasSize(1);
    }

    @Test
    public void toFireInfoDtoShouldReturnDto() {
        //  GIVEN
        String address = "1 rue sesame";
        Person child = new Person();
        child.setFirstName("John");
        child.setLastName("Doe");
        child.setAddress(address);
        child.setCity("San Francisco");
        child.setZip("97451");
        child.setEmail("john@doe.com");
        child.setPhone("841-874-7784");

        Person adult = new Person();
        adult.setFirstName("Leonard");
        adult.setLastName("Doe");
        adult.setAddress(address);
        adult.setCity("San Francisco");
        adult.setZip("97451");
        adult.setEmail("john@doe.com");
        adult.setPhone("841-874-7784");

        List<Person> persons = new ArrayList<>();
        persons.add(child);
        persons.add(adult);

        String childBirthdate = LocalDate.now().minusYears(5).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String adultBirthdate = LocalDate.now().minusYears(20).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        MedicalRecord childMedicalRecord = new MedicalRecord();
        childMedicalRecord.setFirstName(child.getFirstName());
        childMedicalRecord.setLastName(child.getLastName());
        childMedicalRecord.setBirthdate(childBirthdate);
        childMedicalRecord.setMedications(Collections.emptyList());
        childMedicalRecord.setAllergies(Collections.emptyList());

        MedicalRecord adultMedicalRecord = new MedicalRecord();
        adultMedicalRecord.setFirstName(child.getFirstName());
        adultMedicalRecord.setLastName(child.getLastName());
        adultMedicalRecord.setBirthdate(adultBirthdate);
        adultMedicalRecord.setMedications(Collections.emptyList());
        adultMedicalRecord.setAllergies(Collections.emptyList());

        Map<String, MedicalRecord> medicalRecords = new HashMap<>();
        medicalRecords.put(child.getFullName(), childMedicalRecord);
        medicalRecords.put(adult.getFullName(), adultMedicalRecord);

        Map<String, ChildAlertDto> ChildAlerts = new HashMap<>();

        when(medicalRecordService.getAllByFullName()).thenReturn(medicalRecords);
        when(personDtoMapper.toChildAlertDto(anyList(), any())).thenReturn(ChildAlerts);

        FireStation fireStation = new FireStation();
        fireStation.setStation("3");
        fireStation.setAddress(address);

        // WHEN
        FireInfoDto result = personService.toFireInfoDto(persons, fireStation, medicalRecords);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(FireInfoDto.class);
        assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(3);
        assertThat(memoryAppender.search("person transformed to AddressPersonDto", Level.DEBUG)).hasSize(2);
        assertThat(memoryAppender.search("2 person(s) transformed to AddressPersonDto", Level.DEBUG)).hasSize(1);
    }

    @Test
    public void getChildAlertsAddressShouldReturnDtoList() {
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("12/28/1988");
        MedicalRecord childMedicalRecord = new MedicalRecord();
        childMedicalRecord.setFirstName("Jeanne");
        childMedicalRecord.setLastName("Doe");
        childMedicalRecord.setBirthdate("12/28/2016");
        medicalRecordMap.put("John Doe", medicalRecord);
        medicalRecordMap.put("Jeanne Doe", childMedicalRecord);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        Person child = new Person();
        child.setFirstName("Jeanne");
        child.setLastName("Doe");
        List<Person> persons = List.of(person, child);

        Map<String, ChildAlertDto> childAlerts = new HashMap<>();
        childAlerts.put("Jeanne Doe", new ChildAlertDto(child, 15));

        when(medicalRecordService.getAllByFullName()).thenReturn(medicalRecordMap);
        when(personRepository.findAllPersonAtAddress(anyString())).thenReturn(persons);
        when(personDtoMapper.toChildAlertDto(persons, medicalRecordMap)).thenReturn(childAlerts);

        List<ChildAlertDto> result = personService.getChildAlerts("21 jump street");

        assertThat(result).isNotNull();
        assertThat(result.getFirst().firstName).isEqualTo("Jeanne");
        assertThat(result.getFirst().otherPersons.size()).isEqualTo(1);
    }

    @Test
    public void getChildAlertsWhenNotFoundMedicalRecordShouldReturnDtoList() {
        Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("12/28/1988");
        MedicalRecord childMedicalRecord = new MedicalRecord();
        childMedicalRecord.setFirstName("Jeanne");
        childMedicalRecord.setLastName("Doe");
        childMedicalRecord.setBirthdate("12/28/2016");
        medicalRecordMap.put("John Doe", medicalRecord);
        medicalRecordMap.put("Jeanne Doe", childMedicalRecord);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        Person child = new Person();
        child.setFirstName("Jeanne");
        child.setLastName("Doe");
        List<Person> persons = List.of(person, child);

        Map<String, ChildAlertDto> childAlerts = new HashMap<>();
        childAlerts.put("Jeanne Doe", new ChildAlertDto(child, 15));

        when(medicalRecordService.getAllByFullName()).thenReturn(Collections.emptyMap());
        when(personRepository.findAllPersonAtAddress(anyString())).thenReturn(persons);
        when(personDtoMapper.toChildAlertDto(persons, medicalRecordMap)).thenReturn(childAlerts);

        List<ChildAlertDto> result = personService.getChildAlerts("21 jump street");

        assertThat(result).isEmpty();
    }

    @ParameterizedTest(name = "#{index} - Run test with args={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getChildAlertsWithEmptyAddressShouldThrowException(String address) {
        assertThrows(IllegalArgumentException.class, () -> personService.getChildAlerts(address));
    }
}
