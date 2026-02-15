package model;

public class HandicappedVehicle extends Vehicle {
    private boolean hasHandicappedCard;

    public HandicappedVehicle(String plateNumber, boolean hasHandicappedCard) {
        super(plateNumber);
        this.hasHandicappedCard = hasHandicappedCard;
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // Can park in any spot type
        return true;
    }

    @Override
    public String getVehicleType() {
        return "Handicapped Vehicle";
    }

    public boolean hasHandicappedCard() {
        return hasHandicappedCard;
    }

    // This vehicle gets special pricing (RM 2/hour) if they have the card
    // This will be handled in ParkingSpot.getHourlyRate(vehicle)
}