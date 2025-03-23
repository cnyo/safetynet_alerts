package org.safetynet.alerts.unit.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.model.Person;
import org.safetynet.alerts.repository.PersonJsonRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@Tag("PersonJsonRepositoryTest")
public class PersonJsonRepositoryTest {

    private static PersonRepository personRepository;

    private static final String jsonPath = "data.json";

    private JsonData jsonData;

    private MockedStatic<JsonDataService> jsonDataServiceMock;

    @BeforeEach
    public void init() {
        personRepository = new PersonJsonRepository();
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
    public void createShouldReturnCreatedPerson() throws InstanceAlreadyExistsException {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        Person result = personRepository.create(person);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(jsonData.getPersons().size()).isEqualTo(24);
    }

    @Test
    public void createAlreadyExistsPersonShouldReturnException() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");

        assertThrows(InstanceAlreadyExistsException.class, () -> personRepository.create(person));

        assertThat(jsonData.getPersons().size()).isEqualTo(23);
    }

    @Test
    public void updateShouldReturnUpdatedPerson() throws InstanceNotFoundException {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");

        Person result = personRepository.update(person);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Boyd");
        assertThat(jsonData.getPersons().size()).isEqualTo(23);
    }

    @Test
    public void updateNotFoundPersonShouldReturnException() {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("updated-email@email.com");

        assertThrows(InstanceNotFoundException.class, () -> personRepository.update(person));
    }

    @Test
    public void removeShouldReturnTrue() {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Boyd");

        boolean result = personRepository.remove("John Boyd");

        assertThat(result).isTrue();
        assertThat(jsonData.getPersons().size()).isEqualTo(22);
    }

    @Test
    public void removeNotFoundPersonShouldReturnFalse() {
        assertThat(jsonData.getPersons().size()).isEqualTo(23);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        boolean result = personRepository.remove("John Doe");

        assertThat(result).isFalse();
        assertThat(jsonData.getPersons().size()).isEqualTo(23);
    }

    @Test
    public void findAllPersonAtAddressShouldReturnPersons() {
        List<Person> result = personRepository.findAllPersonAtAddress("1509 Culver St");

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    public void findAllPersonAtAddressShouldReturnEmptyList() {
        List<Person> result = personRepository.findAllPersonAtAddress("1510 Culver St");

        assertThat(result).isEmpty();
    }

    @Test
    public void findAllPersonByLastNameShouldReturnPersons() {
        List<Person> result = personRepository.findAllPersonByLastName("Boyd");

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(6);
    }

    @Test
    public void findAllPersonByLastNameShouldReturnEmptyList() {
        List<Person> result = personRepository.findAllPersonByLastName("Bad");

        assertThat(result).isEmpty();
    }

    @Test
    public void findOneByFullNameShouldReturnPerson() {
        Optional<Person> result = personRepository.findOneByFullName("John Boyd");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getFullName()).isEqualTo("John Boyd");
    }

    @Test
    public void findOneByFullNameShouldReturnEmpty() {
        Optional<Person> result = personRepository.findOneByFullName("John Bad");

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void findAllPersonFromAddressesShouldReturnPersons() {
        List<String> addresses = List.of("1509 Culver St", "29 15th St");

        List<Person> result = personRepository.findAllPersonFromAddresses(addresses);

        assertThat(result.size()).isEqualTo(6);
    }

    @Test
    public void findAllPersonFromAddressesShouldReturnEmptyList() {
        List<String> addresses = List.of("21 jump street");

        List<Person> result = personRepository.findAllPersonFromAddresses(addresses);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void findAllShouldReturnPersons() {
        List<Person> result = personRepository.findAll();

        assertThat(result.size()).isEqualTo(23);
    }

    @Test
    public void findPhoneNumbersFromAddressesShouldReturnPersons() {
        List<String> addresses = List.of("1509 Culver St", "29 15th St");

        List<String> result = personRepository.findPhoneNumbersFromAddresses(addresses);

        assertThat(result.size()).isEqualTo(6);
        assertThat(result.contains("841-874-6512")).isTrue();
    }

    @Test
    public void findPhoneNumbersFromAddressesShouldReturnEmptyList() {
        List<String> addresses = List.of("21 jump street");

        List<String> result = personRepository.findPhoneNumbersFromAddresses(addresses);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void findAllEmailsAtCityShouldReturnPersons() {
        List<String> result = personRepository.findAllEmailsAtCity("Culver");

        assertThat(result.size()).isEqualTo(23);
        assertThat(result.getFirst()).isEqualTo("jaboyd@email.com");
    }

    @Test
    public void findAllEmailsAtCityShouldReturnEmptyList() {
        List<String> result = personRepository.findAllEmailsAtCity("Paris");

        assertThat(result.isEmpty()).isTrue();
    }
}
