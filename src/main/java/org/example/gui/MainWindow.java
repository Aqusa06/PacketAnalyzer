package org.example.gui;

import org.example.capture.PacketCaptureThread;
import org.example.parser.PacketModel;
import org.example.Stats.AlertEngine;
import org.example.Stats.StatsCollector;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.example.Stats.PcapFileReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    // ── Fields ────────────────────────────────────────────────────
    private PacketTablePanel   tablePanel;
    private StatsPanel         statsPanel;
    private JLabel             totalPacketsLabel, threatsLabel, bandwidthLabel;
    private StatsCollector     statsCollector;
    private AlertEngine        alertEngine;
    private PayloadSearchPanel searchPanel;
    private JPanel             threatListPanel;

    // ── Colors ────────────────────────────────────────────────────
    static final Color BG_DARK      = new Color(13,  17,  23);
    static final Color BG_PANEL     = new Color(22,  27,  34);
    static final Color BG_ROW       = new Color(17,  21,  28);
    static final Color BORDER_COLOR = new Color(33,  38,  45);
    static final Color TEXT_PRIMARY = new Color(201, 209, 217);
    static final Color TEXT_MUTED   = new Color(139, 148, 158);
    static final Color ACCENT_CYAN  = new Color(0,   217, 255);
    static final Color ACCENT_BLUE  = new Color(88,  166, 255);
    static final Color ACCENT_GREEN = new Color(63,  185, 80);
    static final Color ACCENT_RED   = new Color(248, 81,  73);
    static final Color ACCENT_GOLD  = new Color(210, 153, 34);
    static final Font  MONO         = new Font("Courier New", Font.PLAIN, 12);

    // ── Constructor ───────────────────────────────────────────────
    public MainWindow() {
        statsCollector = new StatsCollector();
        alertEngine    = new AlertEngine();
        setupWindow();
    }

    // ── Called on every packet ────────────────────────────────────
    public void onPacketReceived(PacketModel model) {
        tablePanel.addPacket(model);
        statsCollector.addPacket(model.getProtocol());
        statsCollector.addBytes(model.getSize());

        if (totalPacketsLabel != null)
            totalPacketsLabel.setText(String.valueOf(statsCollector.getTotalPackets()));

        alertEngine.analyse(model.getSourceIp(), model.getDestinationPort(), model.getSize());

        if (threatsLabel != null)
            threatsLabel.setText(String.valueOf(alertEngine.getAlerts().size()));

        statsPanel.updateStats(statsCollector.getProtocolCounts(), statsCollector.getTotalPackets());

        // Refresh threat list on EDT
        SwingUtilities.invokeLater(this::refreshThreatList);
    }

    // ── Refresh threat cards ──────────────────────────────────────
    private void refreshThreatList() {
        threatListPanel.removeAll();
        List<String> alerts = alertEngine.getAlerts();

        if (alerts.isEmpty()) {
            JLabel noThreats = new JLabel("No threats detected");
            noThreats.setForeground(TEXT_MUTED);
            noThreats.setFont(MONO.deriveFont(10f));
            noThreats.setAlignmentX(Component.LEFT_ALIGNMENT);
            threatListPanel.add(noThreats);
        } else {
            // Show ALL threats, newest first
            for (int i = alerts.size() - 1; i >= 0; i--) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(new Color(40, 15, 15));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(248, 81, 73, 80), 1),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                card.setMaximumSize(new Dimension(260, 55));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel msg = new JLabel(
                        "<html><body style='width:220px'>" + alerts.get(i) + "</body></html>");
                msg.setForeground(ACCENT_RED);
                msg.setFont(MONO.deriveFont(10f));
                card.add(msg, BorderLayout.CENTER);

                threatListPanel.add(card);
                threatListPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
        }

        threatListPanel.revalidate();
        threatListPanel.repaint();
    }

    // ── Main layout ───────────────────────────────────────────────
    private void setupWindow() {
        setTitle("NetScan Pro — Cyber Defense Dashboard");
        setSize(1350, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        // ══════════════════════════════════════════════════════════
        // 1. SIDEBAR (LEFT)
        // ══════════════════════════════════════════════════════════
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_DARK);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        JLabel brand = new JLabel("NETSCAN PRO");
        brand.setForeground(ACCENT_CYAN);
        brand.setFont(MONO.deriveFont(Font.BOLD, 13f));
        brand.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sideSep = new JSeparator();
        sideSep.setMaximumSize(new Dimension(200, 1));
        sideSep.setForeground(BORDER_COLOR);

        sidebar.add(brand);
        sidebar.add(sideSep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(makeNavItem("▣  Dashboard",       true));
        sidebar.add(makeNavItem("◈  Live Traffic",    false));
        sidebar.add(makeNavItem("◉  Security Alerts", false));
        sidebar.add(makeNavItem("◫  Reports",         false));
        sidebar.add(Box.createVerticalGlue());

        // ══════════════════════════════════════════════════════════
        // 2. TITLE BAR (TOP CENTER)
        // ══════════════════════════════════════════════════════════
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BG_PANEL);
        titleBar.setPreferredSize(new Dimension(0, 46));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel mainTitle = new JLabel("N E T S C A N   P R O", SwingConstants.CENTER);
        mainTitle.setForeground(ACCENT_BLUE);
        mainTitle.setFont(MONO.deriveFont(Font.BOLD, 15f));
        titleBar.add(mainTitle, BorderLayout.CENTER);

        PulseDot pulse = new PulseDot();
        pulse.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
        titleBar.add(pulse, BorderLayout.EAST);

        // ══════════════════════════════════════════════════════════
// 3. SEARCH BAR
// ══════════════════════════════════════════════════════════
        tablePanel  = new PacketTablePanel();
        searchPanel = new PayloadSearchPanel(tablePanel.getTable(), tablePanel.getTableModel());

// Open PCAP button — styled to match your UI
        JButton openPcapBtn = new JButton("📂 Open PCAP");
        openPcapBtn.setFont(MONO.deriveFont(11f));
        openPcapBtn.setBackground(new Color(33, 38, 45));
        openPcapBtn.setForeground(ACCENT_CYAN);
        openPcapBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_CYAN, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        openPcapBtn.setFocusPainted(false);
        openPcapBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        openPcapBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open PCAP File");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "PCAP Files (*.pcap)", "pcap"
            ));
            // Open directly in your project folder
            chooser.setCurrentDirectory(new java.io.File(
                    System.getProperty("user.dir")
            ));

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = chooser.getSelectedFile().getAbsolutePath();

                // Clear current table
                tablePanel.clearPackets();
                statsCollector = new StatsCollector();
                alertEngine    = new AlertEngine();
                totalPacketsLabel.setText("0");
                threatsLabel.setText("0");
                bandwidthLabel.setText("0 B/s");

                // Load packets from file in background
                SwingWorker<List<org.example.parser.PacketModel>, org.example.parser.PacketModel> worker =
                        new SwingWorker<>() {
                            @Override
                            protected List<org.example.parser.PacketModel> doInBackground() {
                                org.example.Stats.PcapFileReader reader = new org.example.Stats.PcapFileReader();
                                return reader.loadFromFile(filePath);
                            }

                            @Override
                            protected void done() {
                                try {
                                    List<org.example.parser.PacketModel> packets = get();
                                    for (org.example.parser.PacketModel pkt : packets) {
                                        SwingUtilities.invokeLater(() -> onPacketReceived(pkt));
                                    }
                                    SwingUtilities.invokeLater(() ->
                                            JOptionPane.showMessageDialog(null,
                                                    "Loaded " + packets.size() + " packets!",
                                                    "PCAP Loaded", JOptionPane.INFORMATION_MESSAGE)
                                    );
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null,
                                            "Failed to load file: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        };
                worker.execute();
            }
        });

// Add button to search panel
        searchPanel.add(openPcapBtn);
        // ══════════════════════════════════════════════════════════
        // 4. STAT CARDS
        // ══════════════════════════════════════════════════════════
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel c1 = makeStatCard("TOTAL PACKETS", "0",      ACCENT_BLUE);
        JPanel c2 = makeStatCard("THREATS",        "0",     ACCENT_RED);
        JPanel c3 = makeStatCard("BANDWIDTH",      "0.0 MB/s", ACCENT_GREEN);

        totalPacketsLabel = (JLabel)((JPanel) c1.getComponent(0)).getComponent(1);
        threatsLabel      = (JLabel)((JPanel) c2.getComponent(0)).getComponent(1);
        bandwidthLabel    = (JLabel)((JPanel) c3.getComponent(0)).getComponent(1);

        cardPanel.add(c1);
        cardPanel.add(c2);
        cardPanel.add(c3);

        // ══════════════════════════════════════════════════════════
        // 5. DASHBOARD CENTER
        // ══════════════════════════════════════════════════════════
        JPanel topControls = new JPanel(new BorderLayout(0, 0));
        topControls.setOpaque(false);
        topControls.add(searchPanel, BorderLayout.NORTH);
        topControls.add(cardPanel,   BorderLayout.CENTER);

        JPanel dashboard = new JPanel(new BorderLayout(0, 8));
        dashboard.setOpaque(false);
        dashboard.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        dashboard.add(topControls, BorderLayout.NORTH);
        dashboard.add(tablePanel,  BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(titleBar,  BorderLayout.NORTH);
        centerContainer.add(dashboard, BorderLayout.CENTER);

        // ══════════════════════════════════════════════════════════
        // 6. RIGHT PANEL — Stats + Threats
        // ══════════════════════════════════════════════════════════
        statsPanel = new StatsPanel();

        // Threat section
        JPanel threatSection = new JPanel();
        threatSection.setLayout(new BoxLayout(threatSection, BoxLayout.Y_AXIS));
        threatSection.setBackground(BG_DARK);
        threatSection.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        JLabel threatTitle = new JLabel("Recent Threats");
        threatTitle.setForeground(ACCENT_CYAN);
        threatTitle.setFont(MONO.deriveFont(Font.BOLD, 11f));
        threatTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        threatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator threatSep = new JSeparator();
        threatSep.setMaximumSize(new Dimension(260, 1));
        threatSep.setForeground(BORDER_COLOR);
        threatSep.setAlignmentX(Component.LEFT_ALIGNMENT);

        threatListPanel = new JPanel();
        threatListPanel.setLayout(new BoxLayout(threatListPanel, BoxLayout.Y_AXIS));
        threatListPanel.setBackground(BG_DARK);
        threatListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder shown until first threat arrives
        JLabel noThreats = new JLabel("No threats detected");
        noThreats.setForeground(TEXT_MUTED);
        noThreats.setFont(MONO.deriveFont(10f));
        noThreats.setAlignmentX(Component.LEFT_ALIGNMENT);
        threatListPanel.add(noThreats);

        JScrollPane threatScroll = new JScrollPane(threatListPanel);
        threatScroll.setBackground(BG_DARK);
        threatScroll.getViewport().setBackground(BG_DARK);
        threatScroll.setBorder(BorderFactory.createEmptyBorder());
        threatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        threatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        threatScroll.setPreferredSize(new Dimension(260, 200));
        threatScroll.getVerticalScrollBar().setBackground(new Color(33, 38, 45));
        threatScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(248, 81, 73, 120);
                this.trackColor = new Color(33, 38, 45);
            }
        });

        threatSection.add(threatTitle);
        threatSection.add(threatSep);
        threatSection.add(Box.createRigidArea(new Dimension(0, 8)));
        threatSection.add(threatScroll);  // ← scroll instead of raw panel

        // Stack: stats on top, threats below
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_DARK);
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));
        rightPanel.setPreferredSize(new Dimension(265, 0));
        rightPanel.add(statsPanel,    BorderLayout.CENTER);
        rightPanel.add(threatSection, BorderLayout.SOUTH);

        // ══════════════════════════════════════════════════════════
        // 7. ASSEMBLE
        // ══════════════════════════════════════════════════════════
        add(sidebar,         BorderLayout.WEST);
        add(centerContainer, BorderLayout.CENTER);
        add(rightPanel,      BorderLayout.EAST);

        // ══════════════════════════════════════════════════════════
        // 8. BANDWIDTH TIMER
        // ══════════════════════════════════════════════════════════
        new Timer(1000, e -> {
            if (bandwidthLabel != null) {
                long bytesPerSec = statsCollector.getBytesPerSecond();
                if (bytesPerSec >= 1_000_000) {
                    bandwidthLabel.setText(String.format("%.2f MB/s", bytesPerSec / 1_000_000.0));
                } else if (bytesPerSec >= 1_000) {
                    bandwidthLabel.setText(String.format("%.1f KB/s", bytesPerSec / 1_000.0));
                } else {
                    bandwidthLabel.setText(bytesPerSec + " B/s");
                }
            }
        }).start();
    }

    // ── Stat card ─────────────────────────────────────────────────
    private JPanel makeStatCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 4));
        inner.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(MONO.deriveFont(10f));
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(MONO.deriveFont(Font.BOLD, 22f));
        lblValue.setForeground(valueColor);

        inner.add(lblTitle);
        inner.add(lblValue);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── Sidebar nav item ──────────────────────────────────────────
    private JPanel makeNavItem(String text, boolean active) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(200, 40));
        p.setPreferredSize(new Dimension(200, 40));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBackground(active ? new Color(0, 217, 255, 18) : BG_DARK);
        p.setBorder(BorderFactory.createMatteBorder(
                0, active ? 2 : 0, 0, 0,
                active ? ACCENT_CYAN : BG_DARK
        ));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(active ? ACCENT_CYAN : TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        p.add(lbl, BorderLayout.CENTER);

        if (!active) {
            p.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    p.setBackground(new Color(0, 217, 255, 12));
                    p.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT_CYAN));
                    lbl.setForeground(ACCENT_CYAN);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    p.setBackground(BG_DARK);
                    p.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, BG_DARK));
                    lbl.setForeground(TEXT_MUTED);
                }
            });
        }
        return p;
    }

    // ── Pulsing LIVE dot ──────────────────────────────────────────
    class PulseDot extends JComponent {
        private float alpha = 1f;
        PulseDot() {
            setPreferredSize(new Dimension(65, 46));
            new Timer(700, e -> { alpha = (alpha == 1f) ? 0.2f : 1f; repaint(); }).start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cy = getHeight() / 2;
            g2.setColor(new Color(63, 185, 80, (int)(alpha * 255)));
            g2.fillOval(2, cy - 5, 9, 9);
            g2.setColor(ACCENT_GREEN);
            g2.setFont(MONO.deriveFont(10f));
            g2.drawString("LIVE", 16, cy + 4);
        }
    }

    // ── Entry point ───────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        List<PcapNetworkInterface> devices = Pcaps.findAllDevs();
        PcapNetworkInterface device = (PcapNetworkInterface) JOptionPane.showInputDialog(
                null, "Select Interface:", "NetScan",
                JOptionPane.PLAIN_MESSAGE, null,
                devices.toArray(), devices.get(0)
        );
        if (device != null) {
            MainWindow w = new MainWindow();
            w.setVisible(true);
            new PacketCaptureThread(device, w).start();
        }
    }
}