package model;

public class ParkingSpot {

    private String spotId;
    private SpotType type;
    private SpotStatus status;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, SpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.status = SpotStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
    }

    public void removeVehicle() {
        this.currentVehicle = null;
        this.status = SpotStatus.AVAILABLE;
    }

    public String getSpotId() { return spotId; }
    public SpotType getType() { return type; }
    public SpotStatus getStatus() { return status; }
}