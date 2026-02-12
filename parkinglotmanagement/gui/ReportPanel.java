package gui;

import java.awt.*;
import javax.swing.*;

public class ReportPanel extends JPanel {

    private JTextArea reportArea;

    public ReportPanel() {
        setLayout(new BorderLayout());

        reportArea = new JTextArea();
        reportArea.setEditable(false);

        JButton generateReport = new JButton("Generate Report");

        add(generateReport, BorderLayout.NORTH);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        generateReport.addActionListener(e -> {
            reportArea.setText("Report Generated...\n");
        });
    }
}