package service;

public class AppContext {
    public final ParkingService parkingService = new ParkingService();
    public final PaymentService paymentService = new PaymentService();
    public final ReportService reportService = new ReportService(parkingService, paymentService);
}