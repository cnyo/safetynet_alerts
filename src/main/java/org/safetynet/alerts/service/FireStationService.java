package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
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
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address is null or empty");
        }
        FireStation fireStation = fireStationRepository.findFireStationAtAddress(address)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
        log.debug("Get fire station at address {} success", fireStation.getAddress());

        return fireStation;
    }

    public FireStation create(FireStation fireStation) throws InstanceAlreadyExistsException {
        FireStation savedFireStation = fireStationRepository.create(fireStation);
        log.debug("FireStation created successfully");

        return savedFireStation;
    }

    public FireStation update(Map<String, Object> params) {
        if (!validatePatchParams(params)) {
            log.debug("Update fire station patch failed with invalid parameters.");

            throw new IllegalArgumentException("Invalid parameters");
        }

        FireStation fireStation = fireStationRepository.update(
                params.get("address").toString(),
                params.get("station").toString(),
                params.get("new_station").toString()
        );
        log.debug("FireStation {} updated to {} successfully", params.get("station"), fireStation.getStation());

        return fireStation;
    }

    public boolean remove(FireStation fireStation) {
        boolean removed = fireStationRepository.remove(fireStation);
        log.debug("FireStation removed: {}", removed ? "success" : "failure");

        return removed;
    }

    public List<FireStation> getAll() {
        List<FireStation> fireStations = fireStationRepository.findAll();
        log.debug("getAll fire stations: {}", fireStations.size());

        return fireStations;
    }

    public boolean validatePatchParams(Map<String, Object> params) {
        for (String param : PATCH_PARAMS) {
            if (params.get(param) == null) {
                String message = String.format("Parameter '%s' is missing.", param);
                log.debug("Parameter {} is missing.", param);

                return false;
            }
        }

        return true;
    }

    public List<String> getAddressesForOneFireStation(String stationNumber) {
        if (stationNumber.isEmpty()) {
            throw new IllegalArgumentException("Station must not be empty");
        }

        List<String> addresses = fireStationRepository.findAllAddressForOneStation(stationNumber);
        log.debug("{} addresses found from FireStation {}", addresses.size(), stationNumber);

        return addresses;
    }

    public List<String> getAddressesForFireStations(String stations) {
        if (stations == null || stations.trim().isEmpty()) {
            throw new IllegalArgumentException("Stations must not be empty");
        }
        String[] stationNumbers = stations.split(",");
        List<String> addresses = fireStationRepository.findAddressesForStations(stationNumbers);
        log.debug("{} addresses found from FireStations {}", addresses.size(), stations);

        return addresses;
    }

    public FireStation getOneFireStation(String address, String station) {
        return fireStationRepository.findOneFireStation(address, station).orElse(null);
    }
}
