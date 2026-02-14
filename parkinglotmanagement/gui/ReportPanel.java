package gui;

import model.Fine;
import model.Ticket;
import service.AppContext;
import service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReportPanel extends JPanel {

    private final ReportService reportService;
    private JTextArea reportArea;

    public ReportPanel(AppContext ctx) {
        this.reportService = ctx.reportService;

        setLayout(new BorderLayout());

        reportArea = new JTextArea();
        reportArea.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton vehiclesBtn = new JButton("Parked Vehicles Report");
        JButton occupancyBtn = new JButton("Occupancy Report");
        JButton revenueBtn = new JButton("Revenue Report");
        JButton finesBtn = new JButton("Outstanding Fines Report");

        top.add(vehiclesBtn);
        top.add(occupancyBtn);
        top.add(revenueBtn);
        top.add(finesBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        vehiclesBtn.addActionListener(e -> renderParkedVehicles());
        occupancyBtn.addActionListener(e -> renderOccupancy());
        revenueBtn.addActionListener(e -> renderRevenue());
        finesBtn.addActionListener(e -> renderFines());
    }

    private void renderParkedVehicles() {
        StringBuilder sb = new StringBuilder();
        sb.append(" PARKED VEHICLES REPORT \n\n");

        List<Ticket> tickets = reportService.getCurrentlyParkedTickets();
        if (tickets.isEmpty()) {
            sb.append("No vehicles currently parked.\n");
        } else {
            for (Ticket t : tickets) {
                sb.append("Plate: ").append(t.getVehicle().getPlateNumber()).append("\n");
                sb.append("Ticket: ").append(t.getTicketId()).append("\n");
                sb.append("Spot: ").append(t.getSpot().getSpotId()).append(" (").append(t.getSpot().getType()).append(")\n");
                sb.append("Entry: ").append(t.getEntryTime()).append("\n");
                sb.append("-------------------------------\n");
            }
        }
        reportArea.setText(sb.toString());
    }

    private void renderOccupancy() {
        ReportService.OccupancySummary occ = reportService.getOccupancySummary();

        StringBuilder sb = new StringBuilder();
        sb.append("OCCUPANCY REPORT\n\n");
        sb.append("Overall Occupancy: ").append(occ.occupied).append("/").append(occ.total)
                .append(" (").append(String.format("%.1f%%", occ.occupancyRate())).append(")\n\n");

        occ.perFloor.forEach((floor, arr) -> {
            int o = arr[0], t = arr[1];
            double pct = t == 0 ? 0 : (o * 100.0 / t);
            sb.append("Floor ").append(floor).append(": ").append(o).append("/").append(t)
                    .append(" (").append(String.format("%.1f%%", pct)).append(")\n");
        });

        reportArea.setText(sb.toString());
    }

    private void renderRevenue() {
        ReportService.RevenueSummary rev = reportService.getRevenueSummary();

        StringBuilder sb = new StringBuilder();
        sb.append("REVENUE REPORT\n\n");
        sb.append("Total Parking Fees: RM ").append(String.format("%.2f", rev.totalFees)).append("\n");
        sb.append("Total Fines Paid: RM ").append(String.format("%.2f", rev.totalFines)).append("\n");
        sb.append("TOTAL Revenue: RM ").append(String.format("%.2f", rev.totalRevenue)).append("\n");

        reportArea.setText(sb.toString());
    }

    private void renderFines() {
        List<Fine> fines = reportService.getOutstandingFines();

        StringBuilder sb = new StringBuilder();
        sb.append("OUTSTANDING FINES REPORT\n\n");

        if (fines.isEmpty()) {
            sb.append("No unpaid fines.\n");
        } else {
            for (Fine f : fines) {
                sb.append("Plate: ").append(f.getPlateNumber()).append("\n");
                sb.append("Reason: ").append(f.getReason()).append("\n");
                sb.append("Amount: RM ").append(String.format("%.2f", f.getAmount())).append("\n");
                sb.append("Issued: ").append(f.getIssuedAt()).append("\n");
                sb.append("-------------------------------\n");
            }
        }

        reportArea.setText(sb.toString());
    }
}
