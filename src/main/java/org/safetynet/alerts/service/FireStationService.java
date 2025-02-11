package org.safetynet.alerts.service;

import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireStationService {
    @Autowired
    private FireStationRepository fireStationRepository;

    public List<FireStation> getAllFireStationByStation(String stationNumber) {
        return fireStationRepository.findAllFireStationByStation(stationNumber);
    }

    public FireStation getFireStationAtAddress(String address) {
        return fireStationRepository.findFireStationAtAddress(address);
    }

    public List<FireStation> filterFireStationForStations(String stations) {
        String[] stationNumbers = stations.split(",");

        return fireStationRepository.findAllFireStationForStations(stationNumbers);
    }

    public FireStation createFireStation(FireStation fireStation) {
        return fireStationRepository.create(fireStation);
    }

    public FireStation getFireStation(String address, String station) {
        return fireStationRepository.findOneFireStation(address, station);
    }

    public FireStation update(FireStation fireStation, String station) {
        return fireStationRepository.update(fireStation, station);
    }

    public void remove(FireStation fireStation) {
        fireStationRepository.remove(fireStation);
    }
}
