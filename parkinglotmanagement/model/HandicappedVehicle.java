package model;

public class HandicappedVehicle extends Vehicle {

    public HandicappedVehicle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // Must use handicapped spot
        return type == SpotType.HANDICAPPED;
    }
}