package model;

import java.time.LocalDateTime;
<<<<<<< HEAD
import java.time.format.DateTimeFormatter;

public class Ticket {
    private final String ticketId;
    private final String plateNumber;
    private final LocalDateTime entryTime;
    private final String spotId;
    private final SpotType spotType;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.plateNumber = vehicle.getPlateNumber();
        this.entryTime = LocalDateTime.now();
        this.spotId = spot.getSpotId();
        this.spotType = spot.getType();
        
        // Generate ticket ID: T-PLATE-TIMESTAMP
        String timestamp = entryTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.ticketId = String.format("T-%s-%s", plateNumber, timestamp);
    }

    public String getTicketId() { return ticketId; }
    public String getPlateNumber() { return plateNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("""
                             === PARKING TICKET ===
                             Ticket ID: %s
                             License Plate: %s
                             Spot: %s (%s)
                             Entry Time: %s
                             ====================""",
            ticketId, plateNumber, spotId, spotType, entryTime.format(formatter)
        );
=======

public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public boolean isActive() { return exitTime == null; }

    public void close(LocalDateTime exitTime) {
        this.exitTime = exitTime;
>>>>>>> main
    }
}