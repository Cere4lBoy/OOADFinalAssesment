package service;

import model.*;
import strategy.FineStrategy;
import strategy.FixedFineStrategy;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * ParkingService - Central service for parking operations
 * Handles: Entry, Exit, Vehicle tracking, Duration calculation
 */
public class ParkingService {
    private ParkingLot parkingLot;
    private Map<String, Ticket> activeTickets; // plateNumber -> Ticket
    private Map<String, Double> unpaidFines;   // plateNumber -> fine amount
    private FineStrategy fineStrategy; // Strategy for fine calculation

    public ParkingService(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.activeTickets = new HashMap<>();
        this.unpaidFines = new HashMap<>();
        this.fineStrategy = new FixedFineStrategy(); // Default strategy
    }

    // ==================== FINE STRATEGY ====================
    
    /**
     * Set the fine calculation strategy
     */
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    /**
     * Get current fine strategy
     */
    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    /**
     * Calculate and add fine using the current strategy
     */
    public void calculateAndAddFine(String plateNumber, long hoursOverstay) {
        if (fineStrategy != null) {
            double fine = fineStrategy.calculateFine(hoursOverstay);
            if (fine > 0) {
                addFine(plateNumber, fine);
            }
        }
    }

    // ==================== ENTRY OPERATIONS ====================
    
    /**
     * Find available spots for a vehicle
     */
    public List<ParkingSpot> findAvailableSpots(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        return parkingLot.findAvailableSpots(vehicle);
    }

    /**
     * Park a vehicle (complete entry process)
     */
    public Ticket parkVehicle(Vehicle vehicle, String spotId) throws ParkingException {
        // Check if vehicle is already parked
        if (activeTickets.containsKey(vehicle.getPlateNumber())) {
            throw new ParkingException("Vehicle " + vehicle.getPlateNumber() + " is already parked");
        }

        // Find the spot
        ParkingSpot spot = parkingLot.findSpotById(spotId);
        if (spot == null) {
            throw new ParkingException("Spot " + spotId + " not found");
        }

        // Validate spot selection
        if (!spot.canAccommodate(vehicle)) {
            throw new ParkingException(getReasonForRejection(vehicle, spot));
        }

        // Set entry time
        vehicle.setEntryTime(LocalDateTime.now());

        // Park the vehicle
        if (!spot.parkVehicle(vehicle)) {
            throw new ParkingException("Failed to park vehicle in spot " + spotId);
        }

        // Generate ticket
        Ticket ticket = new Ticket(vehicle, spot);
        activeTickets.put(vehicle.getPlateNumber(), ticket);

        return ticket;
    }

    // ==================== EXIT OPERATIONS ====================
    
    /**
     * Find vehicle by plate number
     */
    public Vehicle findVehicle(String plateNumber) {
        return parkingLot.findVehicleByPlate(plateNumber);
    }

    /**
     * Calculate parking duration in hours (rounded up)
     */
    public int calculateParkingDuration(Vehicle vehicle) {
        if (vehicle.getEntryTime() == null) {
            return 0;
        }

        LocalDateTime exitTime = LocalDateTime.now();
        Duration duration = Duration.between(vehicle.getEntryTime(), exitTime);
        
        // Get total minutes and round up to nearest hour
        long minutes = duration.toMinutes();
        int hours = (int) Math.ceil(minutes / 60.0);
        
        return Math.max(hours, 1); // Minimum 1 hour
    }

    /**
     * Calculate parking fee
     */
    public double calculateParkingFee(Vehicle vehicle) {
        if (vehicle.getAssignedSpot() == null) {
            return 0.0;
        }

        int hours = calculateParkingDuration(vehicle);
        ParkingSpot spot = vehicle.getAssignedSpot();
        double hourlyRate = spot.getHourlyRate(vehicle);

        return hours * hourlyRate;
    }

    /**
     * Get unpaid fines for a vehicle
     */
    public double getUnpaidFines(String plateNumber) {
        return unpaidFines.getOrDefault(plateNumber, 0.0);
    }

    /**
     * Calculate total amount due (parking fee + unpaid fines)
     */
    public double calculateTotalDue(Vehicle vehicle) {
        double parkingFee = calculateParkingFee(vehicle);
        double fines = getUnpaidFines(vehicle.getPlateNumber());
        return parkingFee + fines;
    }

    /**
     * Process vehicle exit (after payment is confirmed)
     */
    public void exitVehicle(String plateNumber) throws ParkingException {
        Vehicle vehicle = findVehicle(plateNumber);
        if (vehicle == null) {
            throw new ParkingException("Vehicle " + plateNumber + " not found");
        }

        ParkingSpot spot = vehicle.getAssignedSpot();
        if (spot == null) {
            throw new ParkingException("Vehicle has no assigned spot");
        }

        // Remove vehicle from spot
        spot.removeVehicle();

        // Remove active ticket
        activeTickets.remove(plateNumber);
    }

    /**
     * Add fine to vehicle's account
     */
    public void addFine(String plateNumber, double fineAmount) {
        double currentFines = unpaidFines.getOrDefault(plateNumber, 0.0);
        unpaidFines.put(plateNumber, currentFines + fineAmount);
    }

    /**
     * Clear fines for a vehicle (after payment)
     */
    public void clearFines(String plateNumber) {
        unpaidFines.remove(plateNumber);
    }

    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Get all currently parked vehicles
     */
    public List<Vehicle> getAllParkedVehicles() {
        return parkingLot.getAllParkedVehicles();
    }

    /**
     * Get ticket for a vehicle
     */
    public Ticket getTicket(String plateNumber) {
        return activeTickets.get(plateNumber);
    }

    /**
     * Check if vehicle is currently parked
     */
    public boolean isVehicleParked(String plateNumber) {
        return activeTickets.containsKey(plateNumber);
    }

    /**
     * Get parking lot occupancy rate
     */
    public double getOccupancyRate() {
        return parkingLot.getOccupancyRate();
    }

    /**
     * Get spot by ID
     */
    public ParkingSpot getSpot(String spotId) {
        return parkingLot.findSpotById(spotId);
    }

    // ==================== HELPER METHODS ====================
    
    private String getReasonForRejection(Vehicle vehicle, ParkingSpot spot) {
        if (spot.isOccupied()) {
            return "Spot is already occupied";
        }
        
        if (!vehicle.canParkIn(spot.getType())) {
            return vehicle.getVehicleType() + " cannot park in " + spot.getType() + " spots";
        }
        
        if (spot.getType() == SpotType.RESERVED && !vehicle.hasReservation()) {
            return "Vehicle does not have a reservation for this reserved spot";
        }
        
        return "Unknown reason";
    }

    // Getter for parkingLot (for other services)
    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    // Get all unpaid fines (for reporting)
    public Map<String, Double> getAllUnpaidFines() {
        return new HashMap<>(unpaidFines);
    }
}