package model;

import java.time.LocalDateTime;

public abstract class Vehicle {

    protected String plateNumber;
    protected LocalDateTime entryTime;

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
        this.entryTime = LocalDateTime.now();
    }

    public abstract boolean canParkIn(SpotType type);

    public String getPlateNumber() {
        return plateNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}