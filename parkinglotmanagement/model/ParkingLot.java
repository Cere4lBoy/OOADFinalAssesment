package model;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private String name;
    private List<Floor> floors;

    public ParkingLot(String name, int numberOfFloors) {
        this.name = name;
        this.floors = new ArrayList<>();
        
        // Initialize floors
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new Floor(i));
        }
    }

    // Find all available spots suitable for a vehicle
    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        
        for (Floor floor : floors) {
            availableSpots.addAll(floor.getAvailableSpots(vehicle));
        }
        
        return availableSpots;
    }

    // Find a specific spot by ID
    public ParkingSpot findSpotById(String spotId) {
        for (Floor floor : floors) {
            ParkingSpot spot = floor.findSpotById(spotId);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    // Find vehicle by plate number
    public Vehicle findVehicleByPlate(String plateNumber) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isOccupied() && 
                    spot.getCurrentVehicle().getPlateNumber().equals(plateNumber)) {
                    return spot.getCurrentVehicle();
                }
            }
        }
        return null;
    }

    // Get all currently parked vehicles
    public List<Vehicle> getAllParkedVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isOccupied()) {
                    vehicles.add(spot.getCurrentVehicle());
                }
            }
        }
        
        return vehicles;
    }

    // Calculate occupancy rate
    public double getOccupancyRate() {
        int totalSpots = 0;
        int occupiedSpots = 0;
        
        for (Floor floor : floors) {
            totalSpots += floor.getTotalSpots();
            occupiedSpots += floor.getOccupiedSpots();
        }
        
        return totalSpots > 0 ? (double) occupiedSpots / totalSpots * 100 : 0;
    }

    // Getters
    public String getName() { return name; }
    public List<Floor> getFloors() { return floors; }
    public int getTotalFloors() { return floors.size(); }
}