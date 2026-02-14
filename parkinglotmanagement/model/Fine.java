package model;

import java.time.LocalDateTime;

public class Fine {
    private final String plateNumber;
    private final double amount;
    private final String reason;
    private final LocalDateTime issuedAt;
    private boolean paid;

    public Fine(String plateNumber, double amount, String reason, LocalDateTime issuedAt) {
        this.plateNumber = plateNumber;
        this.amount = amount;
        this.reason = reason;
        this.issuedAt = issuedAt;
        this.paid = false;
    }

    public String getPlateNumber() { return plateNumber; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public boolean isPaid() { return paid; }

    public void markPaid() { this.paid = true; }
}