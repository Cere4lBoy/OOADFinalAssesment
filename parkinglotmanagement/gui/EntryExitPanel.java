package gui;

import java.awt.*;
import javax.swing.*;

public class EntryExitPanel extends JPanel {

    private JTextField plateField;
    private JComboBox<String> vehicleTypeBox;
    private JTextArea outputArea;

    public EntryExitPanel() {

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3,2,10,10));

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

        entryButton.addActionListener(e -> {
            outputArea.append("Entry Process Triggered\n");
        });

        exitButton.addActionListener(e -> {
            outputArea.append("Exit Process Triggered\n");
        });
    }
}