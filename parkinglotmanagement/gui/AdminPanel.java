package gui;

import model.Fine;
import service.AppContext;
import service.ParkingService;
import service.ReportService;
import strategy.FixedFineStrategy;
import strategy.HourlyFineStrategy;
import strategy.ProgressiveFineStrategy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private final ParkingService parkingService;
    private final ReportService reportService;

    public AdminPanel(AppContext ctx) {
        this.parkingService = ctx.parkingService;
        this.reportService = ctx.reportService;

        setLayout(new BorderLayout(10,10));

        JPanel controls = new JPanel(new GridLayout(6,1,10,10));

        JButton viewSpots = new JButton("View All Spots");
        JButton viewRevenue = new JButton("View Revenue");
        JButton viewOccupancy = new JButton("View Occupancy (Per Floor)");
        JButton viewUnpaidFines = new JButton("View Unpaid Fines");
        JButton viewParkedVehicles = new JButton("View Parked Vehicles");

        JComboBox<String> fineScheme = new JComboBox<>(new String[]{"Fixed", "Progressive", "Hourly"});
        JLabel note = new JLabel("Fine scheme applies to FUTURE entries only.");

        controls.add(viewSpots);
        controls.add(viewRevenue);
        controls.add(viewOccupancy);
        controls.add(viewUnpaidFines);
        controls.add(viewParkedVehicles);
        controls.add(fineScheme);

        add(controls, BorderLayout.WEST);
        add(note, BorderLayout.SOUTH);

        viewSpots.addActionListener(e -> showAllSpotsDialog());
        viewRevenue.addActionListener(e -> showRevenueDialog());
        viewOccupancy.addActionListener(e -> showOccupancyDialog());
        viewUnpaidFines.addActionListener(e -> showUnpaidFinesDialog());
        viewParkedVehicles.addActionListener(e -> showParkedVehiclesDialog());

        fineScheme.addActionListener(e -> {
            String selected = (String) fineScheme.getSelectedItem();
            if (selected == null) return;

            switch (selected) {
                case "Progressive":
                    parkingService.setFineStrategy(new ProgressiveFineStrategy());
                    break;
                case "Hourly":
                    parkingService.setFineStrategy(new HourlyFineStrategy());
                    break;
                default:
                    parkingService.setFineStrategy(new FixedFineStrategy());
            }
            JOptionPane.showMessageDialog(this,
                    "Fine scheme set to: " + selected + "\n(Applies to future entries only.)",
                    "Fine Scheme Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showAllSpotsDialog() {
        String[] cols = {"Floor", "Spot ID", "Type", "Status", "Plate"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (ReportService.ParkingSpotRow r : reportService.getAllSpots()) {
            model.addRow(new Object[]{r.floor, r.spotId, r.type, r.status, r.plate});
        }

        JTable table = new JTable(model);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "All Spots", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRevenueDialog() {
        ReportService.RevenueSummary rev = reportService.getRevenueSummary();
        String msg = "Total Parking Fees: RM " + String.format("%.2f", rev.totalFees) + "\n"
                + "Total Fines Paid: RM " + String.format("%.2f", rev.totalFines) + "\n"
                + "TOTAL Revenue: RM " + String.format("%.2f", rev.totalRevenue);
        JOptionPane.showMessageDialog(this, msg, "Revenue Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showOccupancyDialog() {
        ReportService.OccupancySummary occ = reportService.getOccupancySummary();

        String[] cols = {"Floor", "Occupied", "Total", "Occupancy %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        occ.perFloor.forEach((floor, arr) -> {
            int o = arr[0], t = arr[1];
            double pct = t == 0 ? 0 : (o * 100.0 / t);
            model.addRow(new Object[]{floor, o, t, String.format("%.1f%%", pct)});
        });

        JTable table = new JTable(model);

        String header = "Overall: " + occ.occupied + "/" + occ.total + " (" + String.format("%.1f%%", occ.occupancyRate()) + ")";
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(header), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Occupancy (Per Floor)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUnpaidFinesDialog() {
        List<Fine> fines = reportService.getOutstandingFines();
        String[] cols = {"Plate", "Reason", "Amount", "Issued At", "Paid"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Fine f : fines) {
            model.addRow(new Object[]{f.getPlateNumber(), f.getReason(), String.format("RM %.2f", f.getAmount()), f.getIssuedAt(), f.isPaid()});
        }

        JTable table = new JTable(model);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "Outstanding Fines (Unpaid)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showParkedVehiclesDialog() {
        String[] cols = {"Plate", "Spot", "Type", "Entry Time", "Ticket ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        reportService.getCurrentlyParkedTickets().forEach(t -> {
            model.addRow(new Object[]{
                    t.getVehicle().getPlateNumber(),
                    t.getSpot().getSpotId(),
                    t.getSpot().getType(),
                    t.getEntryTime(),
                    t.getTicketId()
            });
        });

        JTable table = new JTable(model);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "Vehicles Currently Parked", JOptionPane.INFORMATION_MESSAGE);
    }
}
