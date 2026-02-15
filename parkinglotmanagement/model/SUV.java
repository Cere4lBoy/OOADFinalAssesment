package model;

public class SUV extends Vehicle {
<<<<<<< HEAD
    
=======

>>>>>>> main
    public SUV(String plateNumber) {
        super(plateNumber);
    }

    @Override
    public boolean canParkIn(SpotType type) {
<<<<<<< HEAD
        return type == SpotType.REGULAR;
    }

    @Override
    public String getVehicleType() {
        return "SUV";
=======
        return type == SpotType.REGULAR || type == SpotType.RESERVED;
>>>>>>> main
    }
}