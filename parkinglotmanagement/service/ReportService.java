package service;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final ParkingService parkingService;
    private final PaymentService paymentService;

    public ReportService(ParkingService parkingService, PaymentService paymentService) {
        this.parkingService = parkingService;
        this.paymentService = paymentService;
    }

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
