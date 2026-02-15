package model;

/**
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

    @Override
    public String toString() {
        return displayName;
    }
}