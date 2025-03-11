package org.safetynet.alerts.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

/**
 * Fire stations management service.
 * This service allows you to retrieve, add, update and delete fire stations
 */
@Slf4j
@Service
public class FireStationService {
    private static final String ADDRESS_PARAM = "address";
    private static final String STATION_PARAM = "station";
    private static final String NEW_STATION_PARAM = "new_station";
    private static final List<String> PATCH_PARAMS = List.of(ADDRESS_PARAM, STATION_PARAM, NEW_STATION_PARAM);

    @Autowired
    private FireStationRepository fireStationRepository;

    /**
     * Retrieves the fire station associated with a given address.
     *
     * @param address the address for which to find a fire station
     * @return the {@code FireStation} found for the specified address
     * @throws NoSuchElementException if no fire station is found for the given address
     * @throws IllegalArgumentException if {@code address} is null or blank
     */
    public FireStation getFireStationAtAddress(String address) {
        if (Strings.isBlank(address)) {
            log.debug("Address is blank");
            throw new IllegalArgumentException("address is null or empty");
        }
        FireStation fireStation = fireStationRepository.findFireStationAtAddress(address)
                .orElseThrow(() -> new NoSuchElementException("No fire station found"));
        log.debug("Get fire station at address {} success", fireStation.getAddress());

        return fireStation;
    }

    /**
     * Create a new fire station.
     *
     * @param fireStation The new fire station to create
     * @return the created {@code fireStation}
     * @throws InstanceAlreadyExistsException if the fire station is already exists
     */
    public FireStation create(FireStation fireStation) throws InstanceAlreadyExistsException {
        FireStation savedFireStation = fireStationRepository.create(fireStation);
        log.debug("FireStation created successfully");

        return savedFireStation;
    }

    /**
     * Update a fire station.
     *
     * @param params Params for update fire station. Ex: {address: "21 jump street", station: "2" , new_station: "3"}
     * @return the updated {@code FireStation}
     * @throws IllegalArgumentException if the params are invalid
     */
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

    /**
     * Remove a fire station.
     *
     * @param fireStation The FireStation to remove.
     * @return {@code true} if FireStation removed successfully, {@code false} otherwise
     */
    public boolean remove(FireStation fireStation) {
        boolean removed = fireStationRepository.remove(fireStation);
        log.debug("FireStation removed: {}", removed ? "success" : "failure");

        return removed;
    }

    /**
     * Retrieves all FireStation from the repository.
     *
     * @return A List of all {@code FireStation} entities;
     *        Return an empty list if no FireStation found.
     */
    public List<FireStation> getAll() {
        List<FireStation> fireStations = fireStationRepository.findAll();
        log.debug("getAll fire stations: {}", fireStations.size());

        return fireStations;
    }

    /**
     * Checks if all parameters are valid.
     *
     * @param params the map containing the parameters to validate.
     * @return {@code true} if parameters are present and valid, {@code false} otherwise.
     */
    public boolean validatePatchParams(Map<String, Object> params) {
        for (String param : PATCH_PARAMS) {
            if (params.get(param) == null) {
                log.debug("Parameter {} is missing.", param);

                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves all addresses for station number.
     *
     * @param stationNumber The station number for which to retrieve addresses
     * @return A list of addresses corresponding to {@code stationNumber}.
     * @throws IllegalArgumentException If {@code stationNumber} is null or blank
     */
    public List<String> getAddressesForFireStation(String stationNumber) {
        if (Strings.isBlank(stationNumber)) {
            log.debug("Station is blank");
            throw new IllegalArgumentException("Station must not be empty");
        }

        List<String> addresses = fireStationRepository.findAllAddressForOneStation(stationNumber);
        log.debug("{} addresses found from FireStation {}", addresses.size(), stationNumber);

        return addresses;
    }

    /**
     * Retrieves all addresses associated with the given fire station numbers.
     *
     * @param stations stations a comma-separated list of fire station numbers.
     * @return A list of addresses corresponding to the given {@code stations}.
     * @throws IllegalArgumentException If {@code stations} is null or blank
     */
    public List<String> getAddressesForFireStations(String stations) {
        if (Strings.isBlank(stations)) {
            log.debug("Stations is blank");
            throw new IllegalArgumentException("Stations must not be empty");
        }

        String[] stationNumbers = stations.split(",");
        List<String> addresses = fireStationRepository.findAddressesForStations(stationNumbers);
        log.debug("{} addresses found from FireStations {}", addresses.size(), stations);

        return addresses;
    }

    /**
     * Retrieves the FireStation by its address and station.
     *
     * @param address The address of the FireStation
     * @param station The FiresStation number
     * @return The {@code stations} found, or {@code null} if no match is found.
     */
    public FireStation getOneFireStation(String address, String station) {
        FireStation fireStation = fireStationRepository.findOneFireStation(address, station).orElse(null);
        log.debug("FireStation found by one name: {}", fireStation != null);

        return fireStation;
    }
}
