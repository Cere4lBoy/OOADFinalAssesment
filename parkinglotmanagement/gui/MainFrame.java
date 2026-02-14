package gui;

import service.AppContext;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("University Parking Lot Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        AppContext ctx = new AppContext();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Entry / Exit", new EntryExitPanel(ctx));
        tabbedPane.add("Admin Panel", new AdminPanel(ctx));
        tabbedPane.add("Reports", new ReportPanel(ctx));

        add(tabbedPane);
        setVisible(true);
    }
}