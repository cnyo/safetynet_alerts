package org.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Repository
public class PersonRepository {

    public List<Person> findAllByStationNumber(int stationNumber) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
//        File jsonFile = new ClassPathResource(properties.getJsonPath()).getFile();
//        JsonNode jsonNodeRoot = objectMapper.readTree(jsonFile);
//        JsonNode jsonNodeFireStation = jsonNodeRoot.get("persons");

//        CustomProperties jsonPath = properties;
//        String jsonPath2 = jsonPath;

        return new List<Person>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Person> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(Person person) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Person> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, Collection<? extends Person> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Person get(int index) {
                return null;
            }

            @Override
            public Person set(int index, Person element) {
                return null;
            }

            @Override
            public void add(int index, Person element) {

            }

            @Override
            public Person remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<Person> listIterator() {
                return null;
            }

            @Override
            public ListIterator<Person> listIterator(int index) {
                return null;
            }

            @Override
            public List<Person> subList(int fromIndex, int toIndex) {
                return List.of();
            }
        };
//        return objectMapper.readValue(jsonNodeFireStation.toString(), new TypeReference<List<Person>>() {});
    }
}
