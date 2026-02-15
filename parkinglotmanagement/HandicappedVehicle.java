package model;

public class HandicappedVehicle extends Vehicle {
    private boolean hasHandicappedCard;

    public HandicappedVehicle(String plateNumber, boolean hasHandicappedCard) {
        super(plateNumber);
        this.hasHandicappedCard = hasHandicappedCard;
    }

    @Override
    public boolean canParkIn(SpotType type) {
        // Handicapped vehicles can park in ANY spot type
        return true;
    }

    public boolean hasHandicappedCard() {
        return hasHandicappedCard;
    }

    public void setHasHandicappedCard(boolean hasHandicappedCard) {
        this.hasHandicappedCard = hasHandicappedCard;
    }
}