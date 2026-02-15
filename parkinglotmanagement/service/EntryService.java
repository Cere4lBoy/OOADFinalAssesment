package service;

import model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EntryController - handles vehicle entry and parking process
 * This is your existing controller adapted to work with the new architecture
 * 
 * @author Member 2 - Vehicle & Entry Management Lead
 */
public class EntryService {
    private ParkingService parkingService;
    
    /**
     * Creates entry controller
     * @param parkingService The parking service to use
     */
    public EntryService(ParkingService parkingService) {
        this.parkingService = parkingService;
        System.out.println("✓ EntryController initialized");
    }
    
    /**
     * Creates a vehicle based on type
     * @param licensePlate License plate number
     * @param type Vehicle type (as String: "MOTORCYCLE", "CAR", "SUV", "HANDICAPPED")
     * @param hasHandicappedCard Whether vehicle has handicapped card (for HANDICAPPED type)
     * @return Created vehicle
     */
    public Vehicle createVehicle(String licensePlate, String type, boolean hasHandicappedCard) {
        // Validate license plate
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        
        licensePlate = licensePlate.trim().toUpperCase();
        
        // Check if vehicle already parked
        if (parkingService.isVehicleParked(licensePlate)) {
            throw new IllegalStateException("Vehicle " + licensePlate + " is already parked!");
        }
        
        // Create appropriate vehicle type
        Vehicle vehicle;
        switch (type.toUpperCase()) {
            case "MOTORCYCLE":
                vehicle = new Motorcycle(licensePlate);
                break;
            case "CAR":
                vehicle = new Car(licensePlate);
                break;
            case "SUV":
                vehicle = new SUV(licensePlate);
                break;
            case "HANDICAPPED":
                vehicle = new HandicappedVehicle(licensePlate, hasHandicappedCard);
                break;
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + type);
        }
        
        return vehicle;
    }
    
    /**
     * Find available spots for a vehicle
     * @param vehicle The vehicle to find spots for
     * @return List of available suitable spots
     */
    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        return parkingService.findAvailableSpots(vehicle);
    }
    
    /**
     * Find available spots for a vehicle type (String version)
     * @param vehicleType Type of vehicle as string
     * @return List of available suitable spots
     */
    public List<ParkingSpot> findAvailableSpotsForType(String vehicleType) {
        // Create a temporary vehicle to find spots
        Vehicle tempVehicle = createVehicle("TEMP", vehicleType, false);
        return parkingService.findAvailableSpots(tempVehicle);
    }
    
    /**
     * Park a vehicle in a specific spot
     * @param vehicle Vehicle to park
     * @param spotId ID of the spot to park in
     * @return Generated parking ticket, or null if failed
     */
    public Ticket parkVehicle(Vehicle vehicle, String spotId) {
        try {
            // Use ParkingService to park the vehicle
            Ticket ticket = parkingService.parkVehicle(vehicle, spotId);
            
            System.out.println("✓ Vehicle parked successfully!");
            System.out.println("  Vehicle: " + vehicle.getPlateNumber());
            System.out.println("  Spot: " + spotId);
            System.out.println("  Ticket: " + ticket.getTicketId());
            
            return ticket;
            
        } catch (ParkingException e) {
            System.err.println("✗ Failed to park vehicle: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Complete parking process - find spot and park
     * @param licensePlate License plate number
     * @param vehicleType Type of vehicle
     * @param hasHandicappedCard Whether has handicapped card
     * @param preferredSpotId Optional preferred spot ID (can be null)
     * @return Generated ticket, or null if failed
     */
    public Ticket processParkingEntry(String licensePlate, String vehicleType, 
                                     boolean hasHandicappedCard, String preferredSpotId) {
        try {
            // Create vehicle
            Vehicle vehicle = createVehicle(licensePlate, vehicleType, hasHandicappedCard);
            
            // Determine spot to use
            String spotId = preferredSpotId;
            
            if (spotId == null || spotId.trim().isEmpty()) {
                // Find first available spot
                List<ParkingSpot> availableSpots = findAvailableSpots(vehicle);
                if (availableSpots.isEmpty()) {
                    System.err.println("✗ No available spots for " + vehicleType);
                    return null;
                }
                spotId = availableSpots.get(0).getSpotId();
            }
            
            // Park vehicle
            return parkVehicle(vehicle, spotId);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("✗ Parking entry failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get parking lot statistics
     * @return Statistics string
     */
    public String getParkingLotStats() {
        double occupancyRate = parkingService.getOccupancyRate();
        List<Vehicle> parkedVehicles = parkingService.getAllParkedVehicles();
        ParkingLot parkingLot = parkingService.getParkingLot();
        
        int totalSpots = 0;
        int occupiedSpots = parkedVehicles.size();
        
        // Calculate total spots
        for (Floor floor : parkingLot.getFloors()) {
            totalSpots += floor.getTotalSpots();
        }
        
        int availableSpots = totalSpots - occupiedSpots;
        
        return String.format(
            "Total Spots: %d | Available: %d | Occupied: %d | Occupancy: %.1f%%",
            totalSpots,
            availableSpots,
            occupiedSpots,
            occupancyRate
        );
    }
    
    /**
     * Validate license plate format
     * @param licensePlate License plate to validate
     * @return true if valid
     */
    public boolean validateLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        
        // Basic validation: 3-10 alphanumeric characters
        String cleaned = licensePlate.trim().replaceAll("[^A-Za-z0-9]", "");
        return cleaned.length() >= 3 && cleaned.length() <= 10;
    }
    
    /**
     * Get a specific parking spot
     * @param spotId Spot ID
     * @return The parking spot, or null if not found
     */
    public ParkingSpot getSpot(String spotId) {
        return parkingService.getSpot(spotId);
    }
    
    /**
     * Check if a vehicle is currently parked
     * @param licensePlate License plate to check
     * @return true if vehicle is parked
     */
    public boolean isVehicleParked(String licensePlate) {
        return parkingService.isVehicleParked(licensePlate);
    }
    
    /**
     * Find a parked vehicle by license plate
     * @param licensePlate License plate to find
     * @return The vehicle, or null if not found
     */
    public Vehicle findVehicle(String licensePlate) {
        return parkingService.findVehicle(licensePlate);
    }
}