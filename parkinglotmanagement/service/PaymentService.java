package service;

import model.*;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Payment Service
 * Handles all payment-related operations and calculations
 * 
 * @author Intan - Exit & Payment Management
 */
public class PaymentService {
    
    /**
     * Calculate parking duration in hours with CEILING rounding
     * 
     * IMPORTANT: Rounds UP to nearest hour
     * - 1 hour 1 minute = 2 hours
     * - 30 minutes = 1 hour
     * - 3 hours exactly = 3 hours
     * 
     * @param entryTime - When vehicle entered
     * @param exitTime - When vehicle is exiting
     * @return Hours parked (rounded UP)
     */
    public long calculateDuration(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry time and exit time cannot be null");
        }
        
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        
        // Calculate total minutes between entry and exit
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        
        // Convert to hours with CEILING rounding
        long hours = totalMinutes / 60;           // Get full hours
        long remainingMinutes = totalMinutes % 60; // Get remaining minutes
        
        if (remainingMinutes > 0) {
            hours++; // Round UP if there are any remaining minutes
        }
        
        // Minimum charge is 1 hour
        return Math.max(1, hours);
    }
    
    /**
     * Calculate parking fee based on spot type and duration
     * 
     * Special Case: Handicapped vehicles get RM 2/hour in handicapped spots
     * 
     * @param spotType - Type of parking spot
     * @param hoursParked - Duration in hours
     * @param vehicle - The vehicle (for handicapped discount check)
     * @return Parking fee in RM
     */
    public double calculateParkingFee(SpotType spotType, long hoursParked, Vehicle vehicle) {
        double hourlyRate = spotType.getHourlyRate();
        
        // Apply handicapped discount if applicable
        if (vehicle instanceof HandicappedVehicle && spotType == SpotType.HANDICAPPED) {
            hourlyRate = 2.0; // Special rate for handicapped vehicles in handicapped spots
        }
        
        return hourlyRate * hoursParked;
    }
    
    /**
     * Create a Payment object
     * 
     * @param vehicle - Vehicle making payment
     * @param parkingFee - Calculated parking fee
     * @param fineAmount - Total unpaid fines
     * @param paymentMethod - CASH or CARD
     * @return Payment object
     */
    public Payment createPayment(Vehicle vehicle, double parkingFee, double fineAmount, 
                                 PaymentMethod paymentMethod) {
        return new Payment(vehicle, parkingFee, fineAmount, paymentMethod);
    }
    
    /**
     * Process complete payment (main integration method)
     * Called by ParkingService.processExit()
     * 
     * @param vehicle - Vehicle exiting
     * @param entryTime - Entry time
     * @param exitTime - Exit time
     * @param spotType - Type of spot
     * @param spotId - Spot ID (for receipt)
     * @param unpaidFines - Total unpaid fines
     * @param paymentMethod - Payment method
     * @return Payment object with all details
     */
    public Payment processPayment(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime, 
                                  SpotType spotType, String spotId, double unpaidFines, 
                                  PaymentMethod paymentMethod) {
        
        // Step 1: Calculate duration (ceiling rounding)
        long hoursParked = calculateDuration(entryTime, exitTime);
        
        // Step 2: Calculate parking fee
        double parkingFee = calculateParkingFee(spotType, hoursParked, vehicle);
        
        // Step 3: Create payment with parking fee + fines
        Payment payment = createPayment(vehicle, parkingFee, unpaidFines, paymentMethod);
        
        return payment;
    }
    
    /**
     * Generate receipt string
     * 
     * @param payment - Payment object
     * @param entryTime - Entry time
     * @param exitTime - Exit time
     * @param hoursParked - Hours parked
     * @param spotId - Spot ID
     * @param hourlyRate - Hourly rate
     * @return Formatted receipt string
     */
    public String generateReceipt(Payment payment, LocalDateTime entryTime, LocalDateTime exitTime, 
                                  long hoursParked, String spotId, double hourlyRate) {
        return payment.generateReceipt(entryTime, exitTime, hoursParked, spotId, hourlyRate);
    }
    
    /**
     * Check if vehicle has overstayed (more than 24 hours)
     * 
     * @param durationHours - Duration in hours
     * @return true if overstayed
     */
    public boolean hasOverstayed(long durationHours) {
        return durationHours > 24;
    }
    
    /**
     * Calculate change for cash payments
     * 
     * @param totalDue - Total amount due
     * @param amountPaid - Amount paid by customer
     * @return Change to return
     * @throws IllegalArgumentException if insufficient payment
     */
    public double calculateChange(double totalDue, double amountPaid) {
        if (amountPaid < totalDue) {
            throw new IllegalArgumentException(
                String.format("Insufficient payment: Paid RM %.2f but need RM %.2f", 
                            amountPaid, totalDue)
            );
        }
        return amountPaid - totalDue;
    }
    
    /**
     * Validate payment amount
     * 
     * @param amount - Amount to validate
     * @return true if valid (non-negative)
     */
    public boolean validatePaymentAmount(double amount) {
        return amount >= 0;
    }
}