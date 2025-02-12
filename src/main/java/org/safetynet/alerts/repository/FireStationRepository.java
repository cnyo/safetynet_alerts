package org.safetynet.alerts.repository;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FireStationRepository {

    protected final JsonData jsonData;

    public FireStationRepository(JsonDataLoader jsonDataLoader) {
        this.jsonData = jsonDataLoader.getJsonData();
    }

    public List<FireStation> findAllFireStationByStation(String stationNumber) {

        return jsonData.getFireStations().stream().filter(f -> f.getStation().equals(stationNumber)).toList();
    }

    public Optional<FireStation> findFireStationAtAddress(String address) {

        return jsonData.getFireStations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    public List<FireStation> findAllFireStationForStations(String[] stationNumbers) {

        return jsonData.getFireStations()
                .stream()
                .filter(f -> List.of(stationNumbers).contains(f.getStation()))
                .collect(Collectors.toList());
    }

    public FireStation create(FireStation fireStation) {
        if (findOneFireStation(fireStation.getAddress(), fireStation.getStation()).isPresent()) {
            throw new IllegalArgumentException("FireStation already exists");
        }

        jsonData.getFireStations().add(fireStation);

        return fireStation;
    }

    public Optional<FireStation> findOneFireStation(String address, String station) {
        return jsonData
                .getFireStations()
                .stream()
                .filter(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station))
                .findFirst();
    }

    public FireStation update(FireStation fireStation, String station) {
        return fireStation.setStation(station);
    }

    public boolean remove(FireStation fireStationToDelete) {
        return jsonData.getFireStations().remove(fireStationToDelete);
    }
}
