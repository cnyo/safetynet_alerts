package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class FireStationService {
    private static final String ADDRESS_PARAM = "address";
    private static final String STATION_PARAM = "station";
    private static final String NEW_STATION_PARAM = "new_station";
    private static final List<String> PATCH_PARAMS = List.of(ADDRESS_PARAM, STATION_PARAM, NEW_STATION_PARAM);

    @Autowired
    private FireStationRepository fireStationRepository;

    public FireStation getFireStationAtAddress(String address) {
        FireStation fireStation = fireStationRepository.findFireStationAtAddress(address)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
        log.debug("getFireStationAtAddress: {}", fireStation);

        if (fireStation == null) {
            throw new NoSuchElementException("No fire station found");
        }
        return fireStation;
    }

    public FireStation createFireStation(FireStation fireStation) {
        FireStation savedFireStation = fireStationRepository.create(fireStation);
        log.info("FireStation created success");

        return savedFireStation;
    }

    public FireStation update(Map<String, Object> params) {
        return fireStationRepository.update(
                params.get("address").toString(),
                params.get("station").toString(),
                params.get("new_station").toString()
        );
    }

    public boolean remove(FireStation fireStation) {
        return fireStationRepository.remove(fireStation);
    }

    public List<FireStation> getAll() {
        return fireStationRepository.findAll();
    }

    public void checkPatchParamsIsOk(Map<String, Object> params) {
        for (String param : PATCH_PARAMS) {
            if (params.get(param) == null) {
                String message = String.format("Parameter '%s' is missing.", param);
                log.debug(message);

                throw new IllegalArgumentException(message);
            }
        }
    }

    public List<String> getAddressesForOneFireStation(String stationNumber) {
        String[] stationNumbers = stationNumber.split(",");
        List<String> addresses = fireStationRepository.findAllAddressForOneStation(stationNumber);
        log.debug("{} addresses found from FireStation {}", addresses.size(), stationNumber);

        return addresses;
    }

    public List<String> getAddressesForFireStations(String stations) {
        String[] stationNumbers = stations.split(",");
        List<String> addresses = fireStationRepository.findAddressesForStations(stationNumbers);
        log.debug("{} addresses found from FireStations {}", addresses.size(), stations);

        return addresses;
    }
}
