package service;

import model.*;
import strategy.FineStrategy;
import strategy.FixedFineStrategy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingService {

    private final List<Floor> floors = new ArrayList<>();
    private final Map<String, Ticket> activeTicketsByPlate = new HashMap<>();
    private final List<Fine> fines = new ArrayList<>();

    private FineStrategy fineStrategy = new FixedFineStrategy();

    public ParkingService() {
        // Default lot: 2 floors. Edit counts/rates/types as you like.
        Floor f1 = new Floor(1);
        f1.addSpot(new ParkingSpot("F1-C1", SpotType.COMPACT));
        f1.addSpot(new ParkingSpot("F1-R1", SpotType.REGULAR));
        f1.addSpot(new ParkingSpot("F1-H1", SpotType.HANDICAPPED));
        f1.addSpot(new ParkingSpot("F1-V1", SpotType.RESERVED));

        Floor f2 = new Floor(2);
        f2.addSpot(new ParkingSpot("F2-C1", SpotType.COMPACT));
        f2.addSpot(new ParkingSpot("F2-R1", SpotType.REGULAR));
        f2.addSpot(new ParkingSpot("F2-R2", SpotType.REGULAR));
        f2.addSpot(new ParkingSpot("F2-V1", SpotType.RESERVED));

        floors.add(f1);
        floors.add(f2);
    }

    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<Fine> getFines() {
        return fines;
    }

    public Collection<Ticket> getActiveTickets() {
        return activeTicketsByPlate.values();
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        String plate = vehicle.getPlateNumber();
        if (activeTicketsByPlate.containsKey(plate)) {
            throw new IllegalStateException("Vehicle already parked: " + plate);
        }

        ParkingSpot spot = findAvailableSpotFor(vehicle);
        if (spot == null) {
            throw new IllegalStateException("No available spot for this vehicle type.");
        }

        spot.assignVehicle(vehicle);
        String ticketId = "T-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketId, vehicle, spot, vehicle.getEntryTime());

        activeTicketsByPlate.put(plate, ticket);
        return ticket;
    }

    public ExitResult exitVehicle(String plateNumber) {
        Ticket ticket = activeTicketsByPlate.get(plateNumber);
        if (ticket == null) throw new IllegalStateException("No active ticket for: " + plateNumber);

        LocalDateTime exitTime = LocalDateTime.now();
        ticket.close(exitTime);

        long hoursStayed = Math.max(1, Duration.between(ticket.getEntryTime(), exitTime).toHours());
        double parkingFee = hoursStayed * ticket.getSpot().getType().getHourlyRate();

        double fineAmt = fineStrategy.calculateFine(hoursStayed);
        Fine fineObj = null;
        if (fineAmt > 0) {
            fineObj = new Fine(plateNumber, fineAmt, "Over 24 hours stay", exitTime);
            fines.add(fineObj);
        }

        ticket.getSpot().removeVehicle();
        activeTicketsByPlate.remove(plateNumber);

        return new ExitResult(ticket, hoursStayed, parkingFee, fineObj);
    }

    private ParkingSpot findAvailableSpotFor(Vehicle vehicle) {
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isAvailable() && vehicle.canParkIn(spot.getType())) {
                    return spot;
                }
            }
        }
        return null;
    }

    public static class ExitResult {
        public final Ticket ticket;
        public final long hoursStayed;
        public final double parkingFee;
        public final Fine fine; // nullable

        public ExitResult(Ticket ticket, long hoursStayed, double parkingFee, Fine fine) {
            this.ticket = ticket;
            this.hoursStayed = hoursStayed;
            this.parkingFee = parkingFee;
            this.fine = fine;
        }

        public double totalDue() {
            return parkingFee + (fine == null ? 0 : fine.getAmount());
        }
    }
}
