package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payment Class
 * Handles payment information and receipt generation
 * 
 * @author Intan - Exit & Payment Management
 */
public class Payment {
    
    private String paymentId;
    private Vehicle vehicle;
    private double parkingFee;
    private double fineAmount;
    private double totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentTime;
    
    /**
     * Constructor for Payment
     * 
     * @param vehicle - The vehicle making payment
     * @param parkingFee - Calculated parking fee
     * @param fineAmount - Total fines (0 if no fines)
     * @param paymentMethod - CASH or CARD
     */
    public Payment(Vehicle vehicle, double parkingFee, double fineAmount, PaymentMethod paymentMethod) {
        this.vehicle = vehicle;
        this.parkingFee = parkingFee;
        this.fineAmount = fineAmount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = LocalDateTime.now();
        
        // Generate unique payment ID
        this.paymentId = generatePaymentId();
        
        // Calculate total amount
        calculateTotal();
    }
    
    /**
     * Calculate total amount (parking fee + fines)
     */
    public void calculateTotal() {
        this.totalAmount = this.parkingFee + this.fineAmount;
    }
    
    /**
     * Generate unique payment ID
     * Format: P-PLATE-TIMESTAMP
     * Example: P-ABC123-20260213143022
     */
    private String generatePaymentId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = paymentTime.format(formatter);
        return String.format("P-%s-%s", vehicle.getPlateNumber(), timestamp);
    }
    
    /**
     * Generate formatted receipt for display
     * 
     * @param entryTime - Vehicle entry time
     * @param exitTime - Vehicle exit time
     * @param hoursParked - Total hours parked
     * @param spotId - Parking spot ID
     * @param hourlyRate - Hourly rate of the spot
     * @return Formatted receipt string
     */
    public String generateReceipt(LocalDateTime entryTime, LocalDateTime exitTime, 
                                  long hoursParked, String spotId, double hourlyRate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("═══════════════════════════════════════════\n");
        receipt.append("           PARKING RECEIPT\n");
        receipt.append("═══════════════════════════════════════════\n");
        receipt.append("Payment ID    : ").append(paymentId).append("\n");
        receipt.append("License Plate : ").append(vehicle.getPlateNumber()).append("\n");
        receipt.append("Spot ID       : ").append(spotId).append("\n");
        receipt.append("───────────────────────────────────────────\n");
        receipt.append("Entry Time    : ").append(entryTime.format(formatter)).append("\n");
        receipt.append("Exit Time     : ").append(exitTime.format(formatter)).append("\n");
        receipt.append("Duration      : ").append(hoursParked).append(" hour(s)\n");
        receipt.append("───────────────────────────────────────────\n");
        receipt.append("Hourly Rate   : RM ").append(String.format("%.2f", hourlyRate)).append("\n");
        receipt.append("Parking Fee   : RM ").append(String.format("%.2f", parkingFee)).append("\n");
        
        if (fineAmount > 0) {
            receipt.append("Fines         : RM ").append(String.format("%.2f", fineAmount)).append("\n");
        }
        
        receipt.append("───────────────────────────────────────────\n");
        receipt.append("TOTAL AMOUNT  : RM ").append(String.format("%.2f", totalAmount)).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod.getDisplayName()).append("\n");
        receipt.append("Payment Time  : ").append(paymentTime.format(formatter)).append("\n");
        receipt.append("═══════════════════════════════════════════\n");
        receipt.append("       Thank you for parking with us!\n");
        receipt.append("═══════════════════════════════════════════\n");
        
        return receipt.toString();
    }
    
    // Getters
    public String getPaymentId() {
        return paymentId;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public double getParkingFee() {
        return parkingFee;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    @Override
    public String toString() {
        return String.format("Payment[%s] - %s: RM %.2f (%s)",
            paymentId,
            vehicle.getPlateNumber(),
            totalAmount,
            paymentMethod.getDisplayName());
    }
}