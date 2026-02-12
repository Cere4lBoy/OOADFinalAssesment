package gui;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("University Parking Lot Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Entry / Exit", new EntryExitPanel());
        tabbedPane.add("Admin Panel", new AdminPanel());
        tabbedPane.add("Reports", new ReportPanel());

        add(tabbedPane);

        setVisible(true);
    }
}