package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatsPanel extends JPanel {

    private Map<String, Integer> protocolCounts;
    private int totalPackets = 0;

    private static final Color[] COLORS = {
            new Color(63, 185, 80),    // TCP  — green
            new Color(88, 166, 255),   // UDP  — blue
            new Color(139, 148, 158),  // N/A  — gray
            new Color(210, 153, 34),   // IGMP — gold
            new Color(121, 192, 255),  // ARP  — light blue
    };

    public StatsPanel() {
        setBackground(new Color(13, 17, 23));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(33, 38, 45)));
    }

    public void updateStats(Map<String, Integer> counts, int total) {
        this.protocolCounts = counts;
        this.totalPackets   = total;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w       = getWidth();
        int padding = 15;

        // Title
        g2.setColor(new Color(0, 217, 255));
        g2.setFont(new Font("Courier New", Font.BOLD, 13));
        g2.drawString("Protocol Stats", padding, 30);

        // Total count
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));
        g2.setColor(new Color(139, 148, 158));
        g2.drawString("Total: " + totalPackets, padding, 50);

        if (protocolCounts == null || totalPackets == 0) return;

        // Pie chart
        int pieSize   = w - padding * 2;
        int pieY      = 70;
        int startAngle = 0;
        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : protocolCounts.entrySet()) {
            int angle = (int) Math.round((entry.getValue() * 360.0) / totalPackets);
            g2.setColor(COLORS[colorIndex % COLORS.length]);
            g2.fillArc(padding, pieY, pieSize, pieSize, startAngle, angle);
            startAngle += angle;
            colorIndex++;
        }

        // Legend
        int legendY  = pieY + pieSize + 20;
        colorIndex   = 0;
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));

        for (Map.Entry<String, Integer> entry : protocolCounts.entrySet()) {
            double pct = (entry.getValue() * 100.0) / totalPackets;
            g2.setColor(COLORS[colorIndex % COLORS.length]);
            g2.fillRect(padding, legendY - 10, 12, 12);
            g2.setColor(new Color(201, 209, 217));
            g2.drawString(String.format("%s: %d (%.1f%%)",
                            entry.getKey(), entry.getValue(), pct),
                    padding + 18, legendY);
            legendY += 20;
            colorIndex++;
        }
    }
}