package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Slf4j
public class FireStationRepository {

    protected final JsonData jsonData;

    public FireStationRepository(JsonDataService jsonDataLoader) {
        this.jsonData = jsonDataLoader.getJsonData();
    }

    public Optional<FireStation> findFireStationAtAddress(String address) {

        return jsonData.getFirestations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    public FireStation create(FireStation fireStation) {
        if (findOneFireStation(fireStation.getAddress(), fireStation.getStation()).isPresent()) {
            throw new IllegalArgumentException("FireStation already exists");
        }

        jsonData.getFirestations().add(fireStation);

        return fireStation;
    }

    public Optional<FireStation> findOneFireStation(String address, String station) {
        return jsonData
                .getFirestations()
                .stream()
                .filter(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station))
                .findFirst();
    }

    public FireStation update(String address, String station, String newStation) {
        FireStation fireStationToUpdate = findOneFireStation(address, station)
                .orElseThrow(() -> new NoSuchElementException("No fire station to update found"));

        return fireStationToUpdate.setStation(newStation);
    }

    public boolean remove(FireStation fireStation) {
        FireStation fireStationToDelete = findOneFireStation(fireStation.getAddress(), fireStation.getStation())
                .orElseThrow(() -> new NoSuchElementException("No fire station to delete found"));

        return jsonData.getFirestations().remove(fireStationToDelete);
    }

    public List<FireStation> findAll() {
        return jsonData.getFirestations();
    }
}
