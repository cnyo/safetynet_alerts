package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.model.JsonData;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FireStationRepository {

    protected final JsonData jsonData;

    public FireStationRepository(JsonDataService jsonDataLoader) {
        this.jsonData = jsonDataLoader.getJsonData();
    }

    public List<FireStation> findAllFireStationByStation(String stationNumber) {

        return jsonData.getFirestations().stream().filter(f -> f.getStation().equals(stationNumber)).toList();
    }

    public Optional<FireStation> findFireStationAtAddress(String address) {

        return jsonData.getFirestations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    public List<FireStation> findAllFireStationForStations(String[] stationNumbers) {

        return jsonData.getFirestations()
                .stream()
                .filter(f -> List.of(stationNumbers).contains(f.getStation()))
                .collect(Collectors.toList());
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

    public FireStation update(FireStation fireStation, String station) {
        return fireStation.setStation(station);
    }

    public boolean remove(FireStation fireStationToDelete) {
        return jsonData.getFirestations().remove(fireStationToDelete);
    }
}
