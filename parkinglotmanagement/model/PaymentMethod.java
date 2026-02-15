package model;

/**
<<<<<<< HEAD
 * Payment method options
 */
public enum PaymentMethod {
    CASH("Cash Payment"),
    CARD("Card Payment");

    private String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

=======
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
    
>>>>>>> main
    @Override
    public String toString() {
        return displayName;
    }
}