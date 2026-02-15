package model;

import java.time.LocalDateTime;

public abstract class Vehicle {

    protected String plateNumber;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;  // NEW: null if still parked
    protected ParkingSpot assignedSpot; // NEW: track assigned spot
    protected boolean hasReservation;   // NEW: for reserved spots

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
        this.entryTime = null; // Will be set when parking
        this.exitTime = null;
        this.assignedSpot = null;
        this.hasReservation = false;
    }

    // Abstract method - must be implemented by subclasses
    public abstract boolean canParkIn(SpotType type);
    
    // Abstract method - must return vehicle type name
    public abstract String getVehicleType();

    // Getters
    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }

    public boolean hasReservation() {
        return hasReservation;
    }

    // Setters
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setAssignedSpot(ParkingSpot spot) {
        this.assignedSpot = spot;
    }

    public void setReservation(boolean hasReservation) {
        this.hasReservation = hasReservation;
    }
}