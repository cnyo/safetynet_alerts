package org.safetynet.alerts.service;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FireStationService {
    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private PersonService personService;

    public List<FireStation> getAllFireStationByStation(String stationNumber) {
        return fireStationRepository.findAllFireStationByStation(stationNumber);
    }

    public FireStation getFireStationAtAddress(String address) {
        return fireStationRepository.findFireStationAtAddress(address)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
    }

    public List<FireStation> filterFireStationForStations(String stations) {
        String[] stationNumbers = stations.split(",");

        return fireStationRepository.findAllFireStationForStations(stationNumbers);
    }

    public FireStation createFireStation(FireStation fireStation) {
        FireStation savedFireStation = fireStationRepository.create(fireStation);
        personService.attachFireStationToPersons(savedFireStation);

        return savedFireStation;
    }

    public FireStation getFireStation(String address, String station) {
        return fireStationRepository.findOneFireStation(address, station)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
    }

    public FireStation update(FireStation fireStation, String station) {
        return fireStationRepository.update(fireStation, station);
    }

    public void remove(FireStation fireStation) {
        fireStationRepository.remove(fireStation);
    }
}
