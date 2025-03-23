package org.safetynet.alerts.service;

import org.safetynet.alerts.model.FireStation;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

/**
 * Fire stations management service.
 * This service allows you to retrieve, add, update and delete fire stations
 */
public interface FireStationService {

    /**
     * Retrieves the fire station associated with a given address.
     *
     * @param address the address for which to find a fire station
     * @return the {@code FireStation} found for the specified address
     * @throws NoSuchElementException if no fire station is found for the given address
     * @throws IllegalArgumentException if {@code address} is null or blank
     */
    public FireStation getFireStationAtAddress(String address);

    /**
     * Create a new fire station.
     *
     * @param fireStation The new fire station to create
     * @return the created {@code fireStation}
     * @throws InstanceAlreadyExistsException if the fire station is already exists
     */
    public FireStation create(FireStation fireStation) throws InstanceAlreadyExistsException;

    /**
     * Update a fire station.
     *
     * @param params Params for update fire station. Ex: {address: "21 jump street", station: "2" , new_station: "3"}
     * @return the updated {@code FireStation}
     * @throws IllegalArgumentException if the params are invalid
     */
    public FireStation update(Map<String, Object> params);

    /**
     * Remove a fire station.
     *
     * @param fireStation The FireStation to remove.
     * @return {@code true} if FireStation removed successfully, {@code false} otherwise
     */
    public boolean remove(FireStation fireStation);

    /**
     * Retrieves all FireStation from the repository.
     *
     * @return A List of all {@code FireStation} entities;
     *        Return an empty list if no FireStation found.
     */
    public List<FireStation> getAll();

    /**
     * Checks if all parameters are valid.
     *
     * @param params the map containing the parameters to validate.
     * @return {@code true} if parameters are present and valid, {@code false} otherwise.
     */
    public boolean validatePatchParams(Map<String, Object> params);

    /**
     * Retrieves all addresses for station number.
     *
     * @param stationNumber The station number for which to retrieve addresses
     * @return A list of addresses corresponding to {@code stationNumber}.
     * @throws IllegalArgumentException If {@code stationNumber} is null or blank
     */
    public List<String> getAddressesForFireStation(String stationNumber);

    /**
     * Retrieves all addresses associated with the given fire station numbers.
     *
     * @param stations stations a comma-separated list of fire station numbers.
     * @return A list of addresses corresponding to the given {@code stations}.
     * @throws IllegalArgumentException If {@code stations} is null or blank
     */
    public List<String> getAddressesForFireStations(String stations);

    /**
     * Retrieves the FireStation by its address and station.
     *
     * @param address The address of the FireStation
     * @param station The FiresStation number
     * @return The {@code stations} found, or {@code null} if no match is found.
     */
    public FireStation getOneFireStation(String address, String station);
}
