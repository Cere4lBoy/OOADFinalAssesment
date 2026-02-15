package model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // Motorcycles can only park in COMPACT spots
        return type == SpotType.COMPACT;
    }
}