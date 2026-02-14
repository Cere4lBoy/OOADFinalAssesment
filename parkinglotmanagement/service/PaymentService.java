package service;

import model.Fine;
import model.Payment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentService {

    private final List<Payment> payments = new ArrayList<>();

    public Payment recordPayment(String plate, double parkingFee, Fine fineToPay) {
        double fineAmount = 0;

        if (fineToPay != null && !fineToPay.isPaid()) {
            fineAmount = fineToPay.getAmount();
            fineToPay.markPaid();
        }

        String payId = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = new Payment(payId, plate, parkingFee, fineAmount, LocalDateTime.now());
        payments.add(payment);
        return payment;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public double getTotalRevenue() {
        return payments.stream().mapToDouble(Payment::getTotalAmount).sum();
    }

    public double getTotalParkingFees() {
        return payments.stream().mapToDouble(Payment::getParkingFee).sum();
    }

    public double getTotalFinesPaid() {
        return payments.stream().mapToDouble(Payment::getFinePaid).sum();
    }
}