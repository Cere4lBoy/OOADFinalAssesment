package service;

import model.*;
import strategy.FineStrategy;
import strategy.FixedFineStrategy;
<<<<<<< HEAD
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
=======

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingService {

    private final List<Floor> floors = new ArrayList<>();
    private final Map<String, Ticket> activeTicketsByPlate = new HashMap<>();
    private final List<Fine> fines = new ArrayList<>();

    private FineStrategy fineStrategy = new FixedFineStrategy();

    public ParkingService() {
        // Default lot: 2 floors. Edit counts/rates/types as you like.
        Floor f1 = new Floor(1);
        f1.addSpot(new ParkingSpot("F1-C1", SpotType.COMPACT));
        f1.addSpot(new ParkingSpot("F1-R1", SpotType.REGULAR));
        f1.addSpot(new ParkingSpot("F1-H1", SpotType.HANDICAPPED));
        f1.addSpot(new ParkingSpot("F1-V1", SpotType.RESERVED));

        Floor f2 = new Floor(2);
        f2.addSpot(new ParkingSpot("F2-C1", SpotType.COMPACT));
        f2.addSpot(new ParkingSpot("F2-R1", SpotType.REGULAR));
        f2.addSpot(new ParkingSpot("F2-R2", SpotType.REGULAR));
        f2.addSpot(new ParkingSpot("F2-V1", SpotType.RESERVED));

        floors.add(f1);
        floors.add(f2);
    }

>>>>>>> main
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

<<<<<<< HEAD
    /**
     * Get current fine strategy
     */
=======
>>>>>>> main
    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

<<<<<<< HEAD
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
=======
    public List<Floor> getFloors() {
        return floors;
    }

    public List<Fine> getFines() {
        return fines;
    }

    public Collection<Ticket> getActiveTickets() {
        return activeTicketsByPlate.values();
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        String plate = vehicle.getPlateNumber();
        if (activeTicketsByPlate.containsKey(plate)) {
            throw new IllegalStateException("Vehicle already parked: " + plate);
        }

        ParkingSpot spot = findAvailableSpotFor(vehicle);
        if (spot == null) {
            throw new IllegalStateException("No available spot for this vehicle type.");
        }

        spot.assignVehicle(vehicle);
        String ticketId = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketId, vehicle, spot, vehicle.getEntryTime());

        activeTicketsByPlate.put(plate, ticket);
        return ticket;
    }

    public ExitResult exitVehicle(String plateNumber) {
        Ticket ticket = activeTicketsByPlate.get(plateNumber);
        if (ticket == null) throw new IllegalStateException("No active ticket for: " + plateNumber);

        LocalDateTime exitTime = LocalDateTime.now();
        ticket.close(exitTime);

        long hoursStayed = Math.max(1, Duration.between(ticket.getEntryTime(), exitTime).toHours());
        double parkingFee = hoursStayed * ticket.getSpot().getType().getHourlyRate();

        double fineAmt = fineStrategy.calculateFine(hoursStayed);
        Fine fineObj = null;
        if (fineAmt > 0) {
            fineObj = new Fine(plateNumber, fineAmt, "Over 24 hours stay", exitTime);
            fines.add(fineObj);
        }

        ticket.getSpot().removeVehicle();
        activeTicketsByPlate.remove(plateNumber);

        return new ExitResult(ticket, hoursStayed, parkingFee, fineObj);
    }

    private ParkingSpot findAvailableSpotFor(Vehicle vehicle) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isAvailable() && vehicle.canParkIn(spot.getType())) {
                    return spot;
                }
            }
        }
        return null;
    }

    public static class ExitResult {
        public final Ticket ticket;
        public final long hoursStayed;
        public final double parkingFee;
        public final Fine fine; // nullable

        public ExitResult(Ticket ticket, long hoursStayed, double parkingFee, Fine fine) {
            this.ticket = ticket;
            this.hoursStayed = hoursStayed;
            this.parkingFee = parkingFee;
            this.fine = fine;
        }

        public double totalDue() {
            return parkingFee + (fine == null ? 0 : fine.getAmount());
        }
    }
}
>>>>>>> main
