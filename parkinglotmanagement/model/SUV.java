package model;

public class SUV extends Vehicle {

    public SUV(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // SUVs/Trucks can only park in REGULAR spots
        return type == SpotType.REGULAR;
    }
}