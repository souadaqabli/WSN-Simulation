package com.myprj.wsn_1;

import javax.swing.*;
import java.awt.*;

public class HistoryFrame extends JFrame {
    private JTextArea historyTextArea;

    public HistoryFrame() {
        setTitle("Simulation History");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendText(String text) {
        historyTextArea.append(text + "\n");
    }

}
