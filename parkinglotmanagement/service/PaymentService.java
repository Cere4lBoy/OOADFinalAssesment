package service;

import model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.PaymentMethod;

/**
 * PaymentService - Handles all payment operations
 * Supports: Cash and Card payments, Receipt generation
 */
public class PaymentService {
    private ParkingService parkingService;
    private double totalRevenue;
    private int totalTransactions;

    public PaymentService(ParkingService parkingService) {
        this.parkingService = parkingService;
        this.totalRevenue = 0.0;
        this.totalTransactions = 0;
    }

    /**
     * Process payment for vehicle exit
     */
    public Receipt processPayment(String plateNumber, PaymentMethod paymentMethod, double amountPaid) 
            throws ParkingException {
        
        // Find vehicle
        Vehicle vehicle = parkingService.findVehicle(plateNumber);
        if (vehicle == null) {
            throw new ParkingException("Vehicle " + plateNumber + " not found");
        }

        // Calculate fees
        int duration = parkingService.calculateParkingDuration(vehicle);
        double parkingFee = parkingService.calculateParkingFee(vehicle);
        double unpaidFines = parkingService.getUnpaidFines(plateNumber);
        double totalDue = parkingFee + unpaidFines;

        // Validate payment amount
        if (amountPaid < totalDue) {
            throw new ParkingException(
                String.format("Insufficient payment. Required: RM %.2f, Paid: RM %.2f", 
                    totalDue, amountPaid)
            );
        }

        double change = amountPaid - totalDue;

        // Create receipt
        Receipt receipt = new Receipt(
            vehicle,
            duration,
            parkingFee,
            unpaidFines,
            totalDue,
            amountPaid,
            change,
            paymentMethod
        );

        // Update revenue
        totalRevenue += totalDue;
        totalTransactions++;

        // Clear fines after payment
        if (unpaidFines > 0) {
            parkingService.clearFines(plateNumber);
        }

        // Exit vehicle
        parkingService.exitVehicle(plateNumber);

        return receipt;
    }

    /**
     * Calculate total amount due without processing payment
     */
    public PaymentSummary getPaymentSummary(String plateNumber) throws ParkingException {
        Vehicle vehicle = parkingService.findVehicle(plateNumber);
        if (vehicle == null) {
            throw new ParkingException("Vehicle " + plateNumber + " not found");
        }

        int duration = parkingService.calculateParkingDuration(vehicle);
        double parkingFee = parkingService.calculateParkingFee(vehicle);
        double unpaidFines = parkingService.getUnpaidFines(plateNumber);
        double totalDue = parkingFee + unpaidFines;
        ParkingSpot spot = vehicle.getAssignedSpot();

        return new PaymentSummary(
            plateNumber,
            vehicle.getEntryTime(),
            LocalDateTime.now(),
            duration,
            spot.getHourlyRate(vehicle),
            parkingFee,
            unpaidFines,
            totalDue
        );
    }

    // Getters for reporting
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    // ==================== INNER CLASSES ====================

    /**
     * Payment summary (before actual payment)
     */
    public static class PaymentSummary {
        private String plateNumber;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private int durationHours;
        private double hourlyRate;
        private double parkingFee;
        private double fines;
        private double totalDue;

        public PaymentSummary(String plateNumber, LocalDateTime entryTime, 
                            LocalDateTime exitTime, int durationHours, double hourlyRate,
                            double parkingFee, double fines, double totalDue) {
            this.plateNumber = plateNumber;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
            this.durationHours = durationHours;
            this.hourlyRate = hourlyRate;
            this.parkingFee = parkingFee;
            this.fines = fines;
            this.totalDue = totalDue;
        }

        // Getters
        public String getPlateNumber() { return plateNumber; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public LocalDateTime getExitTime() { return exitTime; }
        public int getDurationHours() { return durationHours; }
        public double getHourlyRate() { return hourlyRate; }
        public double getParkingFee() { return parkingFee; }
        public double getFines() { return fines; }
        public double getTotalDue() { return totalDue; }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format(
                "=== PAYMENT SUMMARY ===\n" +
                "License Plate: %s\n" +
                "Entry Time: %s\n" +
                "Exit Time: %s\n" +
                "Duration: %d hour(s)\n" +
                "Hourly Rate: RM %.2f\n" +
                "Parking Fee: RM %.2f\n" +
                "Unpaid Fines: RM %.2f\n" +
                "TOTAL DUE: RM %.2f\n" +
                "======================",
                plateNumber,
                entryTime.format(formatter),
                exitTime.format(formatter),
                durationHours,
                hourlyRate,
                parkingFee,
                fines,
                totalDue
            );
        }
    }

    /**
     * Receipt after payment
     */
    public static class Receipt {
        private String receiptId;
        private Vehicle vehicle;
        private LocalDateTime exitTime;
        private int durationHours;
        private double parkingFee;
        private double fines;
        private double totalDue;
        private double amountPaid;
        private double change;
        private PaymentMethod paymentMethod;

        public Receipt(Vehicle vehicle, int durationHours, double parkingFee, 
                      double fines, double totalDue, double amountPaid, 
                      double change, PaymentMethod paymentMethod) {
            this.vehicle = vehicle;
            this.exitTime = LocalDateTime.now();
            this.durationHours = durationHours;
            this.parkingFee = parkingFee;
            this.fines = fines;
            this.totalDue = totalDue;
            this.amountPaid = amountPaid;
            this.change = change;
            this.paymentMethod = paymentMethod;
            
            // Generate receipt ID
            String timestamp = exitTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            this.receiptId = "R-" + vehicle.getPlateNumber() + "-" + timestamp;
        }

        // Getters
        public String getReceiptId() { return receiptId; }
        public Vehicle getVehicle() { return vehicle; }
        public LocalDateTime getExitTime() { return exitTime; }
        public int getDurationHours() { return durationHours; }
        public double getParkingFee() { return parkingFee; }
        public double getFines() { return fines; }
        public double getTotalDue() { return totalDue; }
        public double getAmountPaid() { return amountPaid; }
        public double getChange() { return change; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            
            sb.append("========== RECEIPT ==========\n");
            sb.append(String.format("Receipt ID: %s\n", receiptId));
            sb.append(String.format("License Plate: %s\n", vehicle.getPlateNumber()));
            sb.append(String.format("Spot: %s\n", vehicle.getAssignedSpot().getSpotId()));
            sb.append(String.format("Entry Time: %s\n", 
                vehicle.getEntryTime().format(formatter)));
            sb.append(String.format("Exit Time: %s\n", exitTime.format(formatter)));
            sb.append(String.format("Duration: %d hour(s)\n", durationHours));
            sb.append("-----------------------------\n");
            sb.append(String.format("Parking Fee: RM %.2f\n", parkingFee));
            
            if (fines > 0) {
                sb.append(String.format("Fines Paid: RM %.2f\n", fines));
            }
            
            sb.append(String.format("Total Due: RM %.2f\n", totalDue));
            sb.append(String.format("Amount Paid: RM %.2f\n", amountPaid));
            
            if (change > 0) {
                sb.append(String.format("Change: RM %.2f\n", change));
            }
            
            sb.append(String.format("Payment Method: %s\n", paymentMethod));
            sb.append("=============================\n");
            sb.append("Thank you for parking with us!\n");
            
            return sb.toString();
        }
    }
}