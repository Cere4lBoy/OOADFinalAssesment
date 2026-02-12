package model;

public enum SpotType {
    COMPACT(2),
    REGULAR(5),
    HANDICAPPED(2),
    RESERVED(10);

    private final double hourlyRate;

    SpotType(double rate) {
        this.hourlyRate = rate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}