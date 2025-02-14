package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.dto.fireStation.FireStationToPatchDto;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FireStationService {
    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private PersonRepository personRepository;

    public FireStation getFireStationAtAddress(String address) {
        return fireStationRepository.findFireStationAtAddress(address)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
    }

    public FireStation createFireStation(FireStation fireStation) {
        FireStation savedFireStation = fireStationRepository.create(fireStation);
        log.info("FireStation {} saved", savedFireStation.getStation());
        personRepository.attachFireStation(savedFireStation);
        log.info("FireStation {} attached to persons with same address.", savedFireStation.getStation());

        return savedFireStation;
    }

    public FireStation update(FireStationToPatchDto fireStationToPatchDto) {
        return fireStationRepository.update(fireStationToPatchDto);
    }

    public boolean remove(FireStation fireStation) {
        return fireStationRepository.remove(fireStation);
    }

    public List<FireStation> getAll() {
        return fireStationRepository.findAll();
    }
}
