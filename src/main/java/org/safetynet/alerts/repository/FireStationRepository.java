package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Repository;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Slf4j
public class FireStationRepository {

    public Optional<FireStation> findFireStationAtAddress(String address) {
        return JsonDataService.getJsonData().getFirestations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    public FireStation create(FireStation fireStation) throws InstanceAlreadyExistsException {
        if (findOneFireStation(fireStation.getAddress(), fireStation.getStation()).isPresent()) {
            throw new InstanceAlreadyExistsException("FireStation already exists at address");
        }

        JsonDataService.getJsonData().getFirestations().add(fireStation);

        return fireStation;
    }

    public Optional<FireStation> findOneFireStation(String address, String station) {
        return JsonDataService.getJsonData()
                .getFirestations()
                .stream()
                .filter(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station))
                .findFirst();
    }

    public FireStation update(String address, String station, String newStation) {
        FireStation fireStationToUpdate = findOneFireStation(address, station)
                .orElseThrow(() -> new NoSuchElementException("No fire station to update found"));

        fireStationToUpdate.setStation(newStation);

        return fireStationToUpdate;
    }

    public boolean remove(FireStation fireStationToDelete) {
        return JsonDataService.getJsonData().getFirestations()
                .removeIf(fireStation -> fireStation.equals(fireStationToDelete));
    }

    public List<FireStation> findAll() {
        return JsonDataService.getJsonData().getFirestations();
    }

    public List<String> findAllAddressForOneStation(String stationNumber) {
        return JsonDataService.getJsonData().getFirestations().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .map(FireStation::getAddress)
                .toList();
    }

    public List<String> findAddressesForStations(String[] stations) {
        return JsonDataService.getJsonData().getFirestations()
                .stream()
                .filter(fireStation -> List.of(stations).contains(fireStation.getStation()))
                .map(FireStation::getAddress)
                .toList();
    }
}
