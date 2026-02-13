package model;

/**
 * Fine Class
 * Represents a fine associated with a vehicle's license plate
 * 
 * @author Intan - Exit & Payment Management
 */
public class Fine {
    
    private String plateNumber;  // License plate this fine belongs to
    private double amount;        // Fine amount in RM
    private boolean isPaid;       // Payment status
    
    /**
     * Constructor for Fine
     * 
     * @param plateNumber - Vehicle's license plate
     * @param amount - Fine amount in RM
     */
    public Fine(String plateNumber, double amount) {
        this.plateNumber = plateNumber;
        this.amount = amount;
        this.isPaid = false;  // All fines start as unpaid
    }
    
    /**
     * Mark this fine as paid
     * Called when customer pays the fine
     */
    public void markAsPaid() {
        this.isPaid = true;
    }
    
    // Getters
    public String getPlateNumber() {
        return plateNumber;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    @Override
    public String toString() {
        return String.format("Fine[Plate=%s, Amount=RM%.2f, Paid=%s]", 
                           plateNumber, amount, isPaid ? "Yes" : "No");
    }
}