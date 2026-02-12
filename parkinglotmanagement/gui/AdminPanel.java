package gui;

import java.awt.*;
import javax.swing.*;

public class AdminPanel extends JPanel {

    public AdminPanel() {

        setLayout(new GridLayout(5,1,10,10));

        JButton viewSpots = new JButton("View All Spots");
        JButton viewRevenue = new JButton("View Revenue");
        JButton viewOccupancy = new JButton("View Occupancy");
        JButton viewUnpaidFines = new JButton("View Unpaid Fines");

        JComboBox<String> fineScheme = new JComboBox<>(
                new String[]{"Fixed", "Progressive", "Hourly"}
        );

        add(viewSpots);
        add(viewRevenue);
        add(viewOccupancy);
        add(viewUnpaidFines);
        add(fineScheme);
    }
}