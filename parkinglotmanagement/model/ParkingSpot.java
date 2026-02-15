package model;

public class ParkingSpot {
    private final String spotId;      // e.g., "F1-R1-S1"
    private SpotType type;
    private boolean isOccupied;
    private Vehicle currentVehicle;
    private final int floor;
    private final int row;
    private final int spotNumber;

    public ParkingSpot(int floor, int row, int spotNumber, SpotType type) {
        this.floor = floor;
        this.row = row;
        this.spotNumber = spotNumber;
        this.type = type;
        this.spotId = String.format("F%d-R%d-S%d", floor, row, spotNumber);
        this.isOccupied = false;
        this.currentVehicle = null;
    }

    // Check if this spot can accommodate the given vehicle
    public boolean canAccommodate(Vehicle vehicle) {
        if (isOccupied) {
            return false;
        }
        
        // Check if vehicle can park in this type of spot
        if (!vehicle.canParkIn(type)) {
            return false;
        }
        
        // Check reservation requirement for RESERVED spots
        if (type == SpotType.RESERVED && !vehicle.hasReservation()) {
            return false;
        }
        
        return true;
    }

    // Park a vehicle in this spot
    public boolean parkVehicle(Vehicle vehicle) {
        if (!canAccommodate(vehicle)) {
            return false;
        }
        
        this.currentVehicle = vehicle;
        this.isOccupied = true;
        vehicle.setAssignedSpot(this);
        return true;
    }

    // Remove vehicle from this spot
    public void removeVehicle() {
        if (currentVehicle != null) {
            currentVehicle.setAssignedSpot(null);
        }
        this.currentVehicle = null;
        this.isOccupied = false;
    }

    // Get hourly rate for this spot (with handicapped discount logic)
    public double getHourlyRate(Vehicle vehicle) {
        // Special pricing for handicapped vehicles with card in handicapped spots
        if (type == SpotType.HANDICAPPED && 
            vehicle instanceof HandicappedVehicle && 
            ((HandicappedVehicle) vehicle).hasHandicappedCard()) {
            return 0.0; // FREE
        }
        return type.getHourlyRate();
    }

    // Getters and Setters
    public String getSpotId() { return spotId; }
    
    public SpotType getType() { return type; }
    public void setType(SpotType type) { this.type = type; }
    
    public boolean isOccupied() { return isOccupied; }
    
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    
    public int getFloor() { return floor; }
    public int getRow() { return row; }
    public int getSpotNumber() { return spotNumber; }
    
    public double getBaseHourlyRate() {
        return type.getHourlyRate();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s", 
            spotId, 
            type, 
            isOccupied ? "OCCUPIED by " + currentVehicle : "AVAILABLE");
    }
}