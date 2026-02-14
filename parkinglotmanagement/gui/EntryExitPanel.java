package gui;

import model.*;
import service.AppContext;
import service.ParkingService;
import service.PaymentService;

import javax.swing.*;
import java.awt.*;

public class EntryExitPanel extends JPanel {

    private final ParkingService parkingService;
    private final PaymentService paymentService;

    private JTextField plateField;
    private JComboBox<String> vehicleTypeBox;
    private JTextArea outputArea;

    public EntryExitPanel(AppContext ctx) {
        this.parkingService = ctx.parkingService;
        this.paymentService = ctx.paymentService;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        plateField = new JTextField();
        vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV", "Handicapped"
        });

        JButton entryButton = new JButton("Vehicle Entry");
        JButton exitButton = new JButton("Vehicle Exit");

        topPanel.add(new JLabel("License Plate:"));
        topPanel.add(plateField);
        topPanel.add(new JLabel("Vehicle Type:"));
        topPanel.add(vehicleTypeBox);
        topPanel.add(entryButton);
        topPanel.add(exitButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        entryButton.addActionListener(e -> handleEntry());
        exitButton.addActionListener(e -> handleExit());
    }

    private void handleEntry() {
        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a license plate.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vehicle v = createVehicleFromUI(plate);
            Ticket t = parkingService.parkVehicle(v);

            outputArea.append(" ENTRY SUCCESS\n");
            outputArea.append("Ticket ID: " + t.getTicketId() + "\n");
            outputArea.append("Plate: " + t.getVehicle().getPlateNumber() + "\n");
            outputArea.append("Spot: " + t.getSpot().getSpotId() + " (" + t.getSpot().getType() + ")\n");
            outputArea.append("Entry Time: " + t.getEntryTime() + "\n\n");
        } catch (Exception ex) {
            outputArea.append(" ENTRY FAILED: " + ex.getMessage() + "\n\n");
        }
    }

    private void handleExit() {
        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a license plate.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ParkingService.ExitResult result = parkingService.exitVehicle(plate);

            double parkingFee = result.parkingFee;
            double fineAmt = (result.fine == null) ? 0 : result.fine.getAmount();
            double total = result.totalDue();

            StringBuilder bill = new StringBuilder();
            bill.append("Ticket ID: ").append(result.ticket.getTicketId()).append("\n");
            bill.append("Plate: ").append(plate).append("\n");
            bill.append("Hours Stayed: ").append(result.hoursStayed).append("\n");
            bill.append("Parking Fee: RM ").append(String.format("%.2f", parkingFee)).append("\n");
            bill.append("Fine: RM ").append(String.format("%.2f", fineAmt)).append("\n");
            bill.append("-------------------------\n");
            bill.append("TOTAL: RM ").append(String.format("%.2f", total)).append("\n\n");
            bill.append("Pay fine now? (Parking fee will be recorded either way.)");

            int choice = JOptionPane.showConfirmDialog(this, bill.toString(), "Exit Billing", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                paymentService.recordPayment(plate, parkingFee, result.fine);
                outputArea.append(" EXIT + PAYMENT RECORDED (fee + fine if any)\n\n");
            } else {
                // record parking fee only; leave fine unpaid
                paymentService.recordPayment(plate, parkingFee, null);
                outputArea.append("EXIT RECORDED (parking fee paid). Fine remains UNPAID if it exists.\n\n");
            }

        } catch (Exception ex) {
            outputArea.append(" EXIT FAILED: " + ex.getMessage() + "\n\n");
        }
    }

    private Vehicle createVehicleFromUI(String plate) {
        String type = (String) vehicleTypeBox.getSelectedItem();
        if (type == null) type = "Car";

        switch (type) {
            case "Motorcycle": return new Motorcycle(plate);
            case "SUV": return new SUV(plate);
            case "Handicapped": return new HandicappedVehicle(plate);
            default: return new Car(plate);
        }
    }
}
