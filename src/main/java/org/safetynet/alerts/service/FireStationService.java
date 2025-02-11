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
}
