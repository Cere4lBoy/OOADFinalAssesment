package strategy;

public class FixedFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hoursStayed) {
        if (hoursStayed > 24) {
            return 50;
        }
        return 0;
    }
}