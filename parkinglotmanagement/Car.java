package model;

public class Car extends Vehicle {

    public Car(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
        return type == SpotType.COMPACT || type == SpotType.REGULAR;
    }
}