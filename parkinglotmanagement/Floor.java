package model;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        initializeSpots();
    }

    // Initialize spots for this floor with a default configuration
    private void initializeSpots() {
        // Example configuration: 4 rows, 10 spots per row
        // You can modify this based on your needs
        
        int spotsPerRow = 10;
        int numberOfRows = 4;
        
        for (int row = 1; row <= numberOfRows; row++) {
            for (int spot = 1; spot <= spotsPerRow; spot++) {
                SpotType type = determineSpotType(row, spot);
                spots.add(new ParkingSpot(floorNumber, row, spot, type));
            }
        }
    }

    // Determine spot type based on position (you can customize this logic)
    private SpotType determineSpotType(int row, int spot) {
        // Row 1: Compact spots (1-5), Regular spots (6-10)
        if (row == 1) {
            return (spot <= 5) ? SpotType.COMPACT : SpotType.REGULAR;
        }
        // Row 2: Regular spots (1-8), Handicapped spots (9-10)
        else if (row == 2) {
            return (spot <= 8) ? SpotType.REGULAR : SpotType.HANDICAPPED;
        }
        // Row 3: Reserved spots (1-4), Regular spots (5-10)
        else if (row == 3) {
            return (spot <= 4) ? SpotType.RESERVED : SpotType.REGULAR;
        }
        // Row 4: All regular spots
        else {
            return SpotType.REGULAR;
        }
    }

    // Get all available spots suitable for a vehicle
    public List<ParkingSpot> getAvailableSpots(Vehicle vehicle) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        
        for (ParkingSpot spot : spots) {
            if (spot.canAccommodate(vehicle)) {
                availableSpots.add(spot);
            }
        }
        
        return availableSpots;
    }

    // Find spot by ID on this floor
    public ParkingSpot findSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }

    // Get all spots on this floor
    public List<ParkingSpot> getAllSpots() {
        return new ArrayList<>(spots);
    }

    // Get total number of spots
    public int getTotalSpots() {
        return spots.size();
    }

    // Get number of occupied spots
    public int getOccupiedSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) {
                count++;
            }
        }
        return count;
    }

    // Get number of available spots
    public int getAvailableSpotCount() {
        return getTotalSpots() - getOccupiedSpots();
    }

    // Getters
    public int getFloorNumber() { return floorNumber; }
}