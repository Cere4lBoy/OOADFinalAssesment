package service;

import model.*;
import strategy.FineStrategy;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingService {
    
    private ParkingLot parkingLot;
    private PaymentService paymentService;
    private Map<String, Ticket> activeTickets;  // plateNumber -> Ticket
    private List<Fine> allFines;                 // All fines in system
    
    /**
     * Constructor
     * 
     * @param parkingLot - The parking lot instance
     */
    public ParkingService(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.paymentService = new PaymentService();
        this.activeTickets = new HashMap<>();
        this.allFines = new ArrayList<>();
    }
    
    /**
     * Process vehicle entry
     * (Aleeya's responsibility - included for integration)
     * 
     * @param vehicle - Vehicle entering
     * @param spot - Selected parking spot
     * @return Ticket for the vehicle
     */
    public Ticket processEntry(Vehicle vehicle, ParkingSpot spot) {
        // Create ticket
        Ticket ticket = new Ticket(vehicle, spot);
        
        // Store active ticket
        activeTickets.put(vehicle.getPlateNumber(), ticket);
        
        // Assign vehicle to spot
        spot.assignVehicle(vehicle);
        
        return ticket;
    }
    
    /**
     * FLOW:
     * 1. Find vehicle by license plate
     * 2. Calculate duration (ceiling rounding)
     * 3. Calculate parking fee
     * 4. Get previous unpaid fines
     * 5. Calculate new fine if overstayed (>24 hours)
     * 6. Create payment (parking fee + all fines)
     * 7. Mark all fines as paid
     * 8. Release parking spot
     * 9. Return payment with receipt
     * 
     * @param plateNumber - Vehicle's license plate
     * @param paymentMethod - CASH or CARD
     * @return Payment object containing receipt
     * @throws Exception if vehicle not found
     */
    public Payment processExit(String plateNumber, PaymentMethod paymentMethod) throws Exception {
        
        // STEP 1: Find vehicle's ticket
        Ticket ticket = activeTickets.get(plateNumber);
        if (ticket == null) {
            throw new Exception("Vehicle with plate " + plateNumber + " not found in parking lot");
        }
        
        Vehicle vehicle = ticket.getVehicle();
        ParkingSpot spot = ticket.getSpot();
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();
        
        // STEP 2: Calculate duration using PaymentService (ceiling rounding)
        long hoursParked = paymentService.calculateDuration(entryTime, exitTime);
        
        // STEP 3: Calculate parking fee
        double parkingFee = paymentService.calculateParkingFee(spot.getType(), hoursParked, vehicle);
        
        // STEP 4: Check for previous unpaid fines
        double previousUnpaidFines = getUnpaidFinesForVehicle(plateNumber);
        
        // STEP 5: Check if vehicle overstayed (>24 hours) and calculate new fine
        double newFine = 0;
        if (hoursParked > 24) {
            // Use the parking lot's fine strategy (Strategy Pattern)
            FineStrategy fineStrategy = parkingLot.getFineStrategy();
            newFine = fineStrategy.calculateFine(hoursParked);
            
            // Create and store the new fine
            Fine fine = new Fine(plateNumber, newFine);
            allFines.add(fine);
        }
        
        // STEP 6: Calculate total fines (previous + new)
        double totalFines = previousUnpaidFines + newFine;
        
        // STEP 7: Create payment using PaymentService
        Payment payment = paymentService.processPayment(
            vehicle, 
            entryTime, 
            exitTime, 
            spot.getType(), 
            spot.getSpotId(), 
            totalFines, 
            paymentMethod
        );
        
        // STEP 8: Mark all fines for this vehicle as paid
        markFinesAsPaid(plateNumber);
        
        // STEP 9: Release the parking spot
        spot.removeVehicle();
        
        // STEP 10: Remove ticket from active tickets
        activeTickets.remove(plateNumber);
        
        // STEP 11: Add revenue to parking lot
        parkingLot.addRevenue(payment.getTotalAmount());
        
        // STEP 12: Return payment (includes receipt generation)
        return payment;
    }
    
    /**
     * Get total unpaid fines for a vehicle
     * (Helper method for processExit)
     * 
     * @param plateNumber - Vehicle's plate
     * @return Total unpaid fine amount
     */
    private double getUnpaidFinesForVehicle(String plateNumber) {
        double total = 0;
        for (Fine fine : allFines) {
            if (fine.getPlateNumber().equals(plateNumber) && !fine.isPaid()) {
                total += fine.getAmount();
            }
        }
        return total;
    }
    
    /**
     * Mark all unpaid fines as paid for a vehicle
     * (Helper method for processExit)
     * 
     * @param plateNumber - Vehicle's plate
     */
    private void markFinesAsPaid(String plateNumber) {
        for (Fine fine : allFines) {
            if (fine.getPlateNumber().equals(plateNumber) && !fine.isPaid()) {
                fine.markAsPaid();
            }
        }
    }
    
    /**
     * Get all unpaid fines (for reporting)
     * (Kong's reports will use this)
     * 
     * @return List of unpaid fines
     */
    public List<Fine> getUnpaidFines() {
        List<Fine> unpaidFines = new ArrayList<>();
        for (Fine fine : allFines) {
            if (!fine.isPaid()) {
                unpaidFines.add(fine);
            }
        }
        return unpaidFines;
    }
    
    /**
     * Get ticket for a vehicle
     * 
     * @param plateNumber - Vehicle's plate
     * @return Ticket or null if not found
     */
    public Ticket getTicket(String plateNumber) {
        return activeTickets.get(plateNumber);
    }
    
    /**
     * Get all active tickets
     * 
     * @return Map of active tickets
     */
    public Map<String, Ticket> getActiveTickets() {
        return new HashMap<>(activeTickets);
    }
    
    /**
     * Get all fines
     * 
     * @return List of all fines
     */
    public List<Fine> getAllFines() {
        return new ArrayList<>(allFines);
    }
}