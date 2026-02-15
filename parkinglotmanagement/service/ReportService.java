package service;

import model.*;
<<<<<<< HEAD
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportService - Generates reports for admin panel
 * Reports: Currently parked vehicles, Revenue, Occupancy, Fines, All Spots
 */
public class ReportService {
    private ParkingService parkingService;
    private PaymentService paymentService;
=======

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final ParkingService parkingService;
    private final PaymentService paymentService;
>>>>>>> main

    public ReportService(ParkingService parkingService, PaymentService paymentService) {
        this.parkingService = parkingService;
        this.paymentService = paymentService;
    }

<<<<<<< HEAD
    // ==================== VEHICLE REPORTS ====================

    /**
     * Get report of all currently parked vehicles
     */
    public String getCurrentlyParkedVehiclesReport() {
        List<Vehicle> vehicles = parkingService.getAllParkedVehicles();
        
        if (vehicles.isEmpty()) {
            return "No vehicles currently parked.";
        }

        StringBuilder report = new StringBuilder();
        report.append("========== CURRENTLY PARKED VEHICLES ==========\n");
        report.append(String.format("Total Vehicles: %d\n\n", vehicles.size()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (Vehicle vehicle : vehicles) {
            ParkingSpot spot = vehicle.getAssignedSpot();
            int duration = parkingService.calculateParkingDuration(vehicle);
            
            report.append(String.format("License Plate: %s\n", vehicle.getPlateNumber()));
            report.append(String.format("Vehicle Type: %s\n", vehicle.getVehicleType()));
            report.append(String.format("Spot: %s (%s)\n", spot.getSpotId(), spot.getType()));
            report.append(String.format("Entry Time: %s\n", 
                vehicle.getEntryTime().format(formatter)));
            report.append(String.format("Duration: %d hour(s)\n", duration));
            report.append("-------------------------------------------\n");
        }
        
        return report.toString();
    }

    /**
     * Get list of currently parked vehicles (for GUI display)
     */
    public List<VehicleReport> getCurrentlyParkedVehiclesList() {
        List<Vehicle> vehicles = parkingService.getAllParkedVehicles();
        List<VehicleReport> reports = new ArrayList<>();
        
        for (Vehicle vehicle : vehicles) {
            ParkingSpot spot = vehicle.getAssignedSpot();
            int duration = parkingService.calculateParkingDuration(vehicle);
            double currentFee = parkingService.calculateParkingFee(vehicle);
            
            reports.add(new VehicleReport(
                vehicle.getPlateNumber(),
                vehicle.getVehicleType(),
                spot.getSpotId(),
                spot.getType().toString(),
                vehicle.getEntryTime(),
                duration,
                currentFee
            ));
        }
        
        return reports;
    }

    // ==================== SPOT REPORTS ====================

    /**
     * Get report of all parking spots
     */
    public String getAllSpotsReport() {
        ParkingLot parkingLot = parkingService.getParkingLot();
        
        StringBuilder report = new StringBuilder();
        report.append("========== ALL PARKING SPOTS ==========\n");
        
        int totalSpots = 0;
        int occupiedSpots = 0;
        
        for (Floor floor : parkingLot.getFloors()) {
            report.append(String.format("\n--- Floor %d ---\n", floor.getFloorNumber()));
            
            for (ParkingSpot spot : floor.getAllSpots()) {
                totalSpots++;
                String status = spot.isOccupied() ? "OCCUPIED" : "AVAILABLE";
                if (spot.isOccupied()) {
                    occupiedSpots++;
                }
                
                report.append(String.format("Spot: %-8s | Type: %-12s | Status: %s",
                    spot.getSpotId(),
                    spot.getType(),
                    status
                ));
                
                if (spot.isOccupied()) {
                    Vehicle vehicle = spot.getCurrentVehicle();
                    report.append(String.format(" | Vehicle: %s", vehicle.getPlateNumber()));
                }
                
                report.append("\n");
            }
        }
        
        report.append("\n========================================\n");
        report.append(String.format("Total Spots: %d\n", totalSpots));
        report.append(String.format("Occupied: %d\n", occupiedSpots));
        report.append(String.format("Available: %d\n", totalSpots - occupiedSpots));
        report.append("========================================\n");
        
        return report.toString();
    }

    /**
     * Get list of all parking spots (for GUI display)
     */
    public List<SpotReport> getAllSpotsList() {
        ParkingLot parkingLot = parkingService.getParkingLot();
        List<SpotReport> spots = new ArrayList<>();
        
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                String vehiclePlate = spot.isOccupied() ? 
                    spot.getCurrentVehicle().getPlateNumber() : "";
                
                spots.add(new SpotReport(
                    spot.getSpotId(),
                    spot.getType().toString(),
                    floor.getFloorNumber(),
                    spot.isOccupied() ? "OCCUPIED" : "AVAILABLE",
                    vehiclePlate
                ));
            }
        }
        
        return spots;
    }

    // ==================== REVENUE REPORTS ====================

    /**
     * Get revenue report
     */
    public String getRevenueReport() {
        double totalRevenue = paymentService.getTotalRevenue();
        int totalTransactions = paymentService.getTotalTransactions();
        
        StringBuilder report = new StringBuilder();
        report.append("========== REVENUE REPORT ==========\n");
        report.append(String.format("Total Revenue: RM %.2f\n", totalRevenue));
        report.append(String.format("Total Transactions: %d\n", totalTransactions));
        
        if (totalTransactions > 0) {
            double avgRevenue = totalRevenue / totalTransactions;
            report.append(String.format("Average per Transaction: RM %.2f\n", avgRevenue));
        }
        
        report.append("====================================\n");
        
        return report.toString();
    }

    /**
     * Get revenue data (for GUI display)
     */
    public RevenueReport getRevenueData() {
        return new RevenueReport(
            paymentService.getTotalRevenue(),
            paymentService.getTotalTransactions()
        );
    }

    // ==================== OCCUPANCY REPORTS ====================

    /**
     * Get occupancy report
     */
    public String getOccupancyReport() {
        ParkingLot parkingLot = parkingService.getParkingLot();
        double overallOccupancy = parkingService.getOccupancyRate();
        
        StringBuilder report = new StringBuilder();
        report.append("========== OCCUPANCY REPORT ==========\n");
        report.append(String.format("Overall Occupancy: %.2f%%\n\n", overallOccupancy));
        
        // Floor-by-floor breakdown
        for (Floor floor : parkingLot.getFloors()) {
            int total = floor.getTotalSpots();
            int occupied = floor.getOccupiedSpots();
            int available = floor.getAvailableSpotCount();
            double occupancyRate = total > 0 ? (double) occupied / total * 100 : 0;
            
            report.append(String.format("Floor %d:\n", floor.getFloorNumber()));
            report.append(String.format("  Total Spots: %d\n", total));
            report.append(String.format("  Occupied: %d\n", occupied));
            report.append(String.format("  Available: %d\n", available));
            report.append(String.format("  Occupancy: %.2f%%\n", occupancyRate));
            report.append("-----------------------------------\n");
        }
        
        return report.toString();
    }

    /**
     * Get occupancy data by floor (for GUI display)
     */
    public List<FloorOccupancy> getOccupancyByFloor() {
        ParkingLot parkingLot = parkingService.getParkingLot();
        List<FloorOccupancy> occupancies = new ArrayList<>();
        
        for (Floor floor : parkingLot.getFloors()) {
            int total = floor.getTotalSpots();
            int occupied = floor.getOccupiedSpots();
            int available = floor.getAvailableSpotCount();
            double rate = total > 0 ? (double) occupied / total * 100 : 0;
            
            occupancies.add(new FloorOccupancy(
                floor.getFloorNumber(),
                total,
                occupied,
                available,
                rate
            ));
        }
        
        return occupancies;
    }

    /**
     * Get occupancy by spot type
     */
    public Map<SpotType, OccupancyStats> getOccupancyBySpotType() {
        ParkingLot parkingLot = parkingService.getParkingLot();
        Map<SpotType, OccupancyStats> stats = new HashMap<>();
        
        // Initialize stats for each spot type
        for (SpotType type : SpotType.values()) {
            stats.put(type, new OccupancyStats());
        }
        
        // Count spots by type
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                OccupancyStats stat = stats.get(spot.getType());
                stat.totalSpots++;
                if (spot.isOccupied()) {
                    stat.occupiedSpots++;
                }
            }
        }
        
        return stats;
    }

    // ==================== FINE REPORTS ====================

    /**
     * Get unpaid fines report
     */
    public String getUnpaidFinesReport() {
        Map<String, Double> unpaidFines = parkingService.getAllUnpaidFines();
        
        if (unpaidFines.isEmpty()) {
            return "No unpaid fines.";
        }

        StringBuilder report = new StringBuilder();
        report.append("========== UNPAID FINES REPORT ==========\n");
        
        double totalFines = 0.0;
        
        for (Map.Entry<String, Double> entry : unpaidFines.entrySet()) {
            report.append(String.format("License Plate: %s - RM %.2f\n", 
                entry.getKey(), entry.getValue()));
            totalFines += entry.getValue();
        }
        
        report.append("----------------------------------------\n");
        report.append(String.format("Total Unpaid Fines: RM %.2f\n", totalFines));
        report.append("=========================================\n");
        
        return report.toString();
    }

    /**
     * Get unpaid fines list (for GUI display)
     */
    public List<FineReport> getUnpaidFinesList() {
        Map<String, Double> unpaidFines = parkingService.getAllUnpaidFines();
        List<FineReport> reports = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : unpaidFines.entrySet()) {
            reports.add(new FineReport(entry.getKey(), entry.getValue()));
        }
        
        return reports;
    }

    // ==================== COMBINED DASHBOARD REPORT ====================

    /**
     * Get complete dashboard summary
     */
    public String getDashboardReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("============================================\n");
        report.append("     PARKING MANAGEMENT SYSTEM DASHBOARD    \n");
        report.append("============================================\n\n");
        
        // Quick stats
        int totalParked = parkingService.getAllParkedVehicles().size();
        double occupancy = parkingService.getOccupancyRate();
        double revenue = paymentService.getTotalRevenue();
        int transactions = paymentService.getTotalTransactions();
        
        report.append(String.format("Currently Parked: %d vehicles\n", totalParked));
        report.append(String.format("Occupancy Rate: %.2f%%\n", occupancy));
        report.append(String.format("Total Revenue: RM %.2f\n", revenue));
        report.append(String.format("Total Transactions: %d\n\n", transactions));
        
        return report.toString();
    }

    // ==================== INNER CLASSES (Data Transfer Objects) ====================

    public static class VehicleReport {
        public String plateNumber;
        public String vehicleType;
        public String spotId;
        public String spotType;
        public java.time.LocalDateTime entryTime;
        public int durationHours;
        public double currentFee;

        public VehicleReport(String plateNumber, String vehicleType, String spotId,
                           String spotType, java.time.LocalDateTime entryTime, 
                           int durationHours, double currentFee) {
            this.plateNumber = plateNumber;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;
            this.durationHours = durationHours;
            this.currentFee = currentFee;
        }
    }

    public static class SpotReport {
        public String spotId;
        public String spotType;
        public int floorNumber;
        public String status;
        public String vehiclePlate;

        public SpotReport(String spotId, String spotType, int floorNumber, 
                         String status, String vehiclePlate) {
            this.spotId = spotId;
            this.spotType = spotType;
            this.floorNumber = floorNumber;
            this.status = status;
            this.vehiclePlate = vehiclePlate;
        }
    }

    public static class RevenueReport {
        public double totalRevenue;
        public int totalTransactions;

        public RevenueReport(double totalRevenue, int totalTransactions) {
            this.totalRevenue = totalRevenue;
            this.totalTransactions = totalTransactions;
        }
    }

    public static class FloorOccupancy {
        public int floorNumber;
        public int totalSpots;
        public int occupiedSpots;
        public int availableSpots;
        public double occupancyRate;

        public FloorOccupancy(int floorNumber, int totalSpots, int occupiedSpots,
                            int availableSpots, double occupancyRate) {
            this.floorNumber = floorNumber;
            this.totalSpots = totalSpots;
            this.occupiedSpots = occupiedSpots;
            this.availableSpots = availableSpots;
            this.occupancyRate = occupancyRate;
        }
    }

    public static class OccupancyStats {
        public int totalSpots = 0;
        public int occupiedSpots = 0;

        public int getAvailableSpots() {
            return totalSpots - occupiedSpots;
        }

        public double getOccupancyRate() {
            return totalSpots > 0 ? (double) occupiedSpots / totalSpots * 100 : 0;
        }
    }

    public static class FineReport {
        public String plateNumber;
        public double fineAmount;

        public FineReport(String plateNumber, double fineAmount) {
            this.plateNumber = plateNumber;
            this.fineAmount = fineAmount;
        }
    }
}
=======
    public List<ParkingSpotRow> getAllSpots() {
        List<ParkingSpotRow> rows = new ArrayList<>();
        for (Floor floor : parkingService.getFloors()) {
            for (ParkingSpot s : floor.getSpots()) {
                String plate = getPlateForSpot(s.getSpotId());
                rows.add(new ParkingSpotRow(floor.getFloorNumber(), s.getSpotId(), s.getType(), s.getStatus(), plate));
            }
        }
        return rows;
    }

    private String getPlateForSpot(String spotId) {
        for (Ticket t : parkingService.getActiveTickets()) {
            if (t.isActive() && t.getSpot().getSpotId().equals(spotId)) {
                return t.getVehicle().getPlateNumber();
            }
        }
        return "";
    }

    public List<Ticket> getCurrentlyParkedTickets() {
        return parkingService.getActiveTickets().stream()
                .filter(Ticket::isActive)
                .sorted(Comparator.comparing(t -> t.getVehicle().getPlateNumber()))
                .collect(Collectors.toList());
    }

    public OccupancySummary getOccupancySummary() {
        int total = 0, occupied = 0;
        Map<Integer, int[]> perFloor = new LinkedHashMap<>();

        for (Floor f : parkingService.getFloors()) {
            int fTotal = f.getSpots().size();
            int fOcc = (int) f.getSpots().stream().filter(s -> s.getStatus() == SpotStatus.OCCUPIED).count();

            total += fTotal;
            occupied += fOcc;
            perFloor.put(f.getFloorNumber(), new int[]{fOcc, fTotal});
        }
        return new OccupancySummary(occupied, total, perFloor);
    }

    public RevenueSummary getRevenueSummary() {
        return new RevenueSummary(
                paymentService.getTotalParkingFees(),
                paymentService.getTotalFinesPaid(),
                paymentService.getTotalRevenue()
        );
    }

    public List<Fine> getOutstandingFines() {
        return parkingService.getFines().stream()
                .filter(f -> !f.isPaid())
                .collect(Collectors.toList());
    }

    public static class ParkingSpotRow {
        public final int floor;
        public final String spotId;
        public final SpotType type;
        public final SpotStatus status;
        public final String plate;

        public ParkingSpotRow(int floor, String spotId, SpotType type, SpotStatus status, String plate) {
            this.floor = floor;
            this.spotId = spotId;
            this.type = type;
            this.status = status;
            this.plate = plate;
        }
    }

    public static class OccupancySummary {
        public final int occupied;
        public final int total;
        public final Map<Integer, int[]> perFloor;

        public OccupancySummary(int occupied, int total, Map<Integer, int[]> perFloor) {
            this.occupied = occupied;
            this.total = total;
            this.perFloor = perFloor;
        }

        public double occupancyRate() {
            return total == 0 ? 0 : (occupied * 100.0 / total);
        }
    }

    public static class RevenueSummary {
        public final double totalFees;
        public final double totalFines;
        public final double totalRevenue;

        public RevenueSummary(double totalFees, double totalFines, double totalRevenue) {
            this.totalFees = totalFees;
            this.totalFines = totalFines;
            this.totalRevenue = totalRevenue;
        }
    }
}
>>>>>>> main
