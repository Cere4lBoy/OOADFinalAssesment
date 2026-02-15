package model;

import java.time.LocalDateTime;

public class Payment {
    private final String paymentId;
    private final String plateNumber;
    private final double parkingFee;
    private final double finePaid;
    private final double totalAmount;
    private final LocalDateTime paidAt;

    public Payment(String paymentId, String plateNumber, double parkingFee, double finePaid, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.plateNumber = plateNumber;
        this.parkingFee = parkingFee;
        this.finePaid = finePaid;
        this.totalAmount = parkingFee + finePaid;
        this.paidAt = paidAt;
    }

    public String getPaymentId() { return paymentId; }
    public String getPlateNumber() { return plateNumber; }
    public double getParkingFee() { return parkingFee; }
    public double getFinePaid() { return finePaid; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getPaidAt() { return paidAt; }
}