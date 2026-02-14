package strategy;

public class HourlyFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long hoursStayed) {
        if (hoursStayed <= 24) return 0;

        long overdue = hoursStayed - 24;
        // c- Hourly 10 per overdue hour
        return 10 * overdue;
    }
}