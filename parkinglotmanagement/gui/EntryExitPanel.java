package gui;

<<<<<<< HEAD
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.*;

public class EntryExitPanel extends JPanel {

    private EntryService entryService;
    private PaymentService paymentService; // For exit process
    
=======
import model.*;
import service.AppContext;
import service.ParkingService;
import service.PaymentService;

import javax.swing.*;
import java.awt.*;

public class EntryExitPanel extends JPanel {

    private final ParkingService parkingService;
    private final PaymentService paymentService;

>>>>>>> main
    private JTextField plateField;
    private JComboBox<String> vehicleTypeBox;
    private JCheckBox handicappedCardCheckBox;
    private JCheckBox hasReservationCheckBox;
    private JTextArea outputArea;
    
    // For spot selection
    private JTable spotsTable;
    private DefaultTableModel spotsTableModel;
    private List<ParkingSpot> availableSpots;

<<<<<<< HEAD
    public EntryExitPanel(EntryService entryService, PaymentService paymentService) {
        this.entryService = entryService;
        this.paymentService = paymentService;
        
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
=======
    public EntryExitPanel(AppContext ctx) {
        this.parkingService = ctx.parkingService;
        this.paymentService = ctx.paymentService;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
>>>>>>> main

        plateField = new JTextField();
        vehicleTypeBox = new JComboBox<>(new String[]{
                "Motorcycle", "Car", "SUV", "Handicapped"
        });
        
        handicappedCardCheckBox = new JCheckBox("Has Handicapped Card");
        handicappedCardCheckBox.setEnabled(false);
        
        hasReservationCheckBox = new JCheckBox("Has Reservation (for Reserved spots)");

        JButton entryButton = new JButton("Vehicle Entry");
        JButton exitButton = new JButton("Vehicle Exit");

        topPanel.add(new JLabel("License Plate:"));
        topPanel.add(plateField);
        topPanel.add(new JLabel("Vehicle Type:"));
        topPanel.add(vehicleTypeBox);
        topPanel.add(handicappedCardCheckBox);
        topPanel.add(hasReservationCheckBox);
        topPanel.add(entryButton);
        topPanel.add(exitButton);

        // Center panel - split into spots table and output area
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Available spots table
        centerPanel.add(createSpotsTablePanel());
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(outputArea));

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

<<<<<<< HEAD
        // Enable/disable handicapped card checkbox based on vehicle type
        vehicleTypeBox.addActionListener(e -> {
            String selected = (String) vehicleTypeBox.getSelectedItem();
            handicappedCardCheckBox.setEnabled("Handicapped".equals(selected));
        });

        // ENTRY BUTTON - Implements 5-step process
        entryButton.addActionListener(e -> handleVehicleEntry());

        // EXIT BUTTON
        exitButton.addActionListener(e -> handleVehicleExit());
    }
    
    /**
     * Create the spots table panel
     */
    private JPanel createSpotsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Spots (Step 1)"));
        
        // Table columns
        String[] columns = {"Spot ID", "Floor", "Type", "Rate/Hour"};
        spotsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        spotsTable = new JTable(spotsTableModel);
        spotsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spotsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(spotsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Park button at bottom
        JButton parkButton = new JButton("Park in Selected Spot (Step 2-5)");
        parkButton.addActionListener(e -> parkInSelectedSpot());
        panel.add(parkButton, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * STEP 1: Handle Vehicle Entry - Show available spots
     */
    private void handleVehicleEntry() {
        // Validate input
        String plateNumber = plateField.getText().trim();
        if (plateNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a license plate number", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate license plate format
        if (!entryService.validateLicensePlate(plateNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid license plate format. Must be 3-10 alphanumeric characters.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if vehicle already parked
        if (entryService.isVehicleParked(plateNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Vehicle " + plateNumber + " is already parked in the system", 
                "Already Parked", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get vehicle type
        String vehicleType = (String) vehicleTypeBox.getSelectedItem();
        boolean hasHandicappedCard = handicappedCardCheckBox.isSelected();

        // Create vehicle based on type
        Vehicle vehicle;
        try {
            vehicle = entryService.createVehicle(plateNumber, vehicleType, hasHandicappedCard);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Set reservation if checked
        if (hasReservationCheckBox.isSelected()) {
            vehicle.setHasReservation(true);
        }

        // STEP 1: Find available spots
        outputArea.setText(""); // Clear output
        outputArea.append("=== VEHICLE ENTRY PROCESS ===\n\n");
        outputArea.append("Step 1: Finding available spots...\n");
        outputArea.append("Vehicle: " + vehicle + "\n\n");

        try {
            availableSpots = entryService.findAvailableSpots(vehicle);
            
            if (availableSpots.isEmpty()) {
                outputArea.append("❌ No available spots for " + vehicle.getVehicleType() + "\n");
                JOptionPane.showMessageDialog(this, 
                    "No available spots for this vehicle type", 
                    "No Spots Available", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Display available spots in table
            spotsTableModel.setRowCount(0); // Clear table
            for (ParkingSpot spot : availableSpots) {
                spotsTableModel.addRow(new Object[]{
                    spot.getSpotId(),
                    "Floor " + spot.getFloor(),
                    spot.getType(),
                    "RM " + String.format("%.2f", spot.getBaseHourlyRate())
                });
            }

            outputArea.append("✓ Found " + availableSpots.size() + " available spot(s)\n");
            outputArea.append("Step 2: Please select a spot from the table and click 'Park'\n");

        } catch (Exception ex) {
            outputArea.append("❌ Error: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, 
                "Error finding spots: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * STEPS 2-5: Park vehicle in selected spot
     */
    private void parkInSelectedSpot() {
        // STEP 2: Validate spot selection
        int selectedRow = spotsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a spot from the table", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (availableSpots == null || availableSpots.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please click 'Vehicle Entry' first to find available spots", 
                "No Spots Found", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get selected spot
        ParkingSpot selectedSpot = availableSpots.get(selectedRow);
        String spotId = selectedSpot.getSpotId();

        outputArea.append("\nStep 2: User selected spot: " + spotId + "\n");

        // Create vehicle again
        String plateNumber = plateField.getText().trim();
        String vehicleType = (String) vehicleTypeBox.getSelectedItem();
        boolean hasHandicappedCard = handicappedCardCheckBox.isSelected();
        
        Vehicle vehicle;
        try {
            vehicle = entryService.createVehicle(plateNumber, vehicleType, hasHandicappedCard);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hasReservationCheckBox.isSelected()) {
            vehicle.setHasReservation(true);
        }

        // STEP 3: System marks spot as occupied
        // STEP 4: System records entry time & assigns spot
        outputArea.append("Step 3: Marking spot as occupied...\n");
        outputArea.append("Step 4: Recording entry time and assigning spot...\n");
        
        Ticket ticket = entryService.parkVehicle(vehicle, spotId);
        
        if (ticket == null) {
            outputArea.append("\n❌ Failed to park vehicle\n");
            JOptionPane.showMessageDialog(this, 
                "Failed to park vehicle. Please try again.", 
                "Parking Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // STEP 5: Generate and display ticket
        outputArea.append("Step 5: Generating ticket...\n\n");
        outputArea.append("✓ VEHICLE PARKED SUCCESSFULLY!\n\n");
        outputArea.append(ticket.toString());

        // Show ticket in dialog as well
        JOptionPane.showMessageDialog(this, 
            ticket.toString(), 
            "Parking Ticket", 
            JOptionPane.INFORMATION_MESSAGE);

        // Clear the form
        clearForm();
    }

    /**
     * Handle vehicle exit
     */
    private void handleVehicleExit() {
        String plateNumber = plateField.getText().trim();
        
        if (plateNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a license plate number", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        outputArea.setText(""); // Clear output
        outputArea.append("=== VEHICLE EXIT PROCESS ===\n\n");

        // Find vehicle
        Vehicle vehicle = entryService.findVehicle(plateNumber);
        
        if (vehicle == null) {
            outputArea.append("❌ Vehicle " + plateNumber + " not found in parking lot\n");
            JOptionPane.showMessageDialog(this, 
                "Vehicle not found in parking lot", 
                "Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        outputArea.append("Vehicle found: " + vehicle + "\n");
        outputArea.append("Spot: " + vehicle.getAssignedSpot().getSpotId() + "\n\n");

        try {
            // Get payment summary
            PaymentService.PaymentSummary summary = paymentService.getPaymentSummary(plateNumber);
            
            outputArea.append("Duration: " + summary.getDurationHours() + " hour(s)\n");
            outputArea.append("Parking Fee: RM " + String.format("%.2f", summary.getParkingFee()) + "\n");
            
            if (summary.getFines() > 0) {
                outputArea.append("Unpaid Fines: RM " + String.format("%.2f", summary.getFines()) + "\n");
            }
            
            outputArea.append("TOTAL DUE: RM " + String.format("%.2f", summary.getTotalDue()) + "\n\n");
            outputArea.append("Note: In full system, this would proceed to payment processing.\n");
            outputArea.append("For now, this is just showing the calculation.\n");
            
        } catch (Exception e) {
            outputArea.append("❌ Error calculating payment: " + e.getMessage() + "\n");
        }
    }

    /**
     * Clear the form after successful parking
     */
    private void clearForm() {
        plateField.setText("");
        vehicleTypeBox.setSelectedIndex(0);
        handicappedCardCheckBox.setSelected(false);
        hasReservationCheckBox.setSelected(false);
        spotsTableModel.setRowCount(0);
        availableSpots = null;
=======
        entryButton.addActionListener(e -> handleEntry());
        exitButton.addActionListener(e -> handleExit());
>>>>>>> main
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
