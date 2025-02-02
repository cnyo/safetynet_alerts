package org.safetynet.alerts.service;

import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FireStationService {
    FireStationRepository fireStationRepository = new FireStationRepository();

    public List<String> getAllAddressFromStationNumber(String stationNumber) throws IOException {
        return fireStationRepository.findAllAddressByStation(stationNumber);
    }
}
