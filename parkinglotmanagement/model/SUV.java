package model;

public class SUV extends Vehicle {

    public SUV(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        return type == SpotType.REGULAR || type == SpotType.RESERVED;
    }
}