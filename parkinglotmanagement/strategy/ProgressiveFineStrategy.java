package strategy;

public class ProgressiveFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hoursStayed) {
        if (hoursStayed <= 24) return 0;

        long overdue = hoursStayed - 24; // hours beyond 24
        // a- Fixed 50 (already done in FixedFineStrategy)
        // b- Progressive 20 + 5 per overdue hour
        return 20 + (5 * overdue);
    }
}