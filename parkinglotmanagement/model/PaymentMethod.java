package model;

/**
 * Payment Method Enum
 * Defines available payment methods for parking fees
 * 
 * @author Intan - Exit & Payment Management
 */
public enum PaymentMethod {
    CASH("Cash"),
    CARD("Card");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}