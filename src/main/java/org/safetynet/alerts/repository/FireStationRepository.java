package org.safetynet.alerts.repository;

import lombok.extern.slf4j.Slf4j;
import org.safetynet.alerts.model.FireStation;
import org.safetynet.alerts.service.JsonDataService;
import org.springframework.stereotype.Repository;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Repository class for managing FireStation entities. This class provides methods to perform
 * Create, Read, Update, and Delete (CRUD) operations on FireStation objects in the underlying
 * data source. It also includes additional methods for querying fire stations by specific
 * criteria such as address or station number.
 *
 * This class relies on an external {@code JsonDataService} to access and update the fire station
 * data. FireStation equality is based on their address and station number.
 */
@Repository
@Slf4j
public class FireStationRepository {

    /**
     * Creates a new FireStation entry and adds it to the existing dataset.
     * If a FireStation with the same address and station already exists, an exception is thrown.
     *
     * @param fireStation the FireStation object to add
     * @return the created FireStation object
     * @throws InstanceAlreadyExistsException if a FireStation with the same address and station already exists
     */
    public FireStation create(FireStation fireStation) throws InstanceAlreadyExistsException {
        if (findOneFireStation(fireStation.getAddress(), fireStation.getStation()).isPresent()) {
            throw new InstanceAlreadyExistsException("FireStation already exists at address");
        }

        JsonDataService.getJsonData().getFirestations().add(fireStation);

        return fireStation;
    }

    /**
     * Updates the station number of a FireStation identified by its address and current station number.
     *
     * @param address the address of the FireStation to update
     * @param station the current station number of the FireStation to update
     * @param newStation the new station number to assign to the FireStation
     * @return the updated FireStation object with the new station number
     * @throws NoSuchElementException if no FireStation matching the given address and station is found
     */
    public FireStation update(String address, String station, String newStation) throws NoSuchElementException {
        FireStation fireStationToUpdate = findOneFireStation(address, station)
                .orElseThrow(() -> new NoSuchElementException("No fire station to update found"));

        fireStationToUpdate.setStation(newStation);

        return fireStationToUpdate;
    }

    /**
     * Removes the specified fire station from the list of fire stations.
     *
     * @param fireStationToDelete the fire station to be removed
     * @return {@code true} if the fire station was successfully removed, {@code false} otherwise
     */
    public boolean remove(FireStation fireStationToDelete) {
        return JsonDataService.getJsonData().getFirestations()
                .removeIf(fireStation -> fireStation.equals(fireStationToDelete));
    }

    /**
     * Finds a fire station associated with the specified address.
     *
     * @param address the address to search for a fire station.
     * @return an {@code Optional} containing the matching {@code FireStation} if found,
     *         or an empty {@code Optional} if no fire station matches the given address.
     */
    public Optional<FireStation> findFireStationAtAddress(String address) {
        return JsonDataService.getJsonData().getFirestations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    /**
     * Retrieves an Optional containing a FireStation object that matches the given address
     * and station details. Searches through the list of fire stations to find the first one
     * with a matching address and station.
     *
     * @param address the address of the fire station to be searched
     * @param station the station number of the fire station to be searched
     * @return an Optional containing the matching FireStation if found, or an empty Optional if no matches exist
     */
    public Optional<FireStation> findOneFireStation(String address, String station) {
        return JsonDataService.getJsonData()
                .getFirestations()
                .stream()
                .filter(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station))
                .findFirst();
    }

    /**
     * Retrieves a list of all fire stations.
     *
     * @return a list of {@code FireStation} objects representing all fire stations, or an empty list if no fire stations are available.
     */
    public List<FireStation> findAll() {
        return JsonDataService.getJsonData().getFirestations();
    }

    /**
     * Finds all addresses corresponding to a specific fire station number.
     * This method filters the list of fire stations by comparing the provided station number
     * with the station number of each fire station, and retrieves the associated addresses.
     *
     * @param stationNumber the fire station number to search for
     * @return a list of addresses corresponding to the given fire station number
     */
    public List<String> findAllAddressForOneStation(String stationNumber) {
        return JsonDataService.getJsonData().getFirestations().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .map(FireStation::getAddress)
                .toList();
    }

    /**
     * Finds and returns a list of addresses associated with the given fire station numbers.
     * The method filters the fire stations based on the provided station numbers and retrieves their corresponding addresses.
     *
     * @param stations an array of station numbers to search for
     * @return a list of addresses corresponding to the given station numbers
     */
    public List<String> findAddressesForStations(String[] stations) {
        return JsonDataService.getJsonData().getFirestations()
                .stream()
                .filter(fireStation -> List.of(stations).contains(fireStation.getStation()))
                .map(FireStation::getAddress)
                .toList();
    }
}
