package model;

public class Motorcycle extends Vehicle {

    public Motorcycle(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // Allow motorcycles 
        return true;
    }
}