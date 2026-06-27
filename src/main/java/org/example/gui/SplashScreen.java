package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import java.util.List;

public class SplashScreen extends JFrame {

    // ── Same colors as MainWindow ─────────────────────────────────
    static final Color BG_DARK      = new Color(13,  17,  23);
    static final Color BG_PANEL     = new Color(22,  27,  34);
    static final Color BORDER_COLOR = new Color(33,  38,  45);
    static final Color TEXT_PRIMARY = new Color(201, 209, 217);
    static final Color TEXT_MUTED   = new Color(139, 148, 158);
    static final Color ACCENT_CYAN  = new Color(0,   217, 255);
    static final Color ACCENT_BLUE  = new Color(88,  166, 255);
    static final Color ACCENT_GREEN = new Color(63,  185, 80);
    static final Color ACCENT_RED   = new Color(248, 81,  73);
    static final Font  MONO         = new Font("Courier New", Font.PLAIN, 12);

    public SplashScreen() {
        setupSplash();
    }

    private void setupSplash() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // ── Centre panel ──────────────────────────────────────────
        JPanel centre = new JPanel();
        centre.setBackground(BG_DARK);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        // ── Project name ──────────────────────────────────────────
        // Use a custom painted label to bypass L&F color override
        JLabel nameLabel = new JLabel("NETSCAN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_CYAN);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), x, fm.getAscent());
            }
        };
        nameLabel.setFont(new Font("Courier New", Font.BOLD, 72));
        nameLabel.setForeground(ACCENT_CYAN);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setPreferredSize(new Dimension(800, 90));
        nameLabel.setMaximumSize(new Dimension(900, 90));

        // ── Subtitle ──────────────────────────────────────────────
        JLabel subLabel = new JLabel("Real-Time Network Packet Analyser") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), x, fm.getAscent());
            }
        };
        subLabel.setFont(new Font("Courier New", Font.PLAIN, 20));
        subLabel.setForeground(TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setPreferredSize(new Dimension(800, 30));
        subLabel.setMaximumSize(new Dimension(900, 30));

        // ── Divider ───────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(600, 1));
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Interface label ───────────────────────────────────────
        JLabel selectLabel = new JLabel("Select Network Interface") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_GREEN);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), x, fm.getAscent());
            }
        };
        selectLabel.setFont(new Font("Courier New", Font.BOLD, 15));
        selectLabel.setForeground(ACCENT_GREEN);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectLabel.setPreferredSize(new Dimension(800, 25));
        selectLabel.setMaximumSize(new Dimension(900, 25));

        // ── Interface list panel ──────────────────────────────────
        JPanel listPanel = new JPanel();
        listPanel.setBackground(BG_PANEL);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        listPanel.setMaximumSize(new Dimension(700, 280));
        listPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Load interfaces ───────────────────────────────────────
        List<PcapNetworkInterface> devices;
        try {
            devices = Pcaps.findAllDevs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error finding devices: " + e.getMessage());
            return;
        }

        ButtonGroup group = new ButtonGroup();
        JRadioButton[] buttons = new JRadioButton[devices.size()];
        boolean anySelected = false;

        for (int i = 0; i < devices.size(); i++) {
            String desc = devices.get(i).getDescription();
            if (desc == null) desc = devices.get(i).getName();

            final int idx = i;
            buttons[i] = new JRadioButton(i + "  —  " + desc) {
                @Override
                protected void paintComponent(Graphics g) {
                    // Force background
                    g.setColor(BG_PANEL);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            buttons[i].setBackground(BG_PANEL);
            buttons[i].setForeground(TEXT_PRIMARY);   // ← this is what you wanted changed
            buttons[i].setFont(MONO.deriveFont(13f));
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttons[i].setOpaque(true);
            buttons[i].setFocusPainted(false);

            // Hover effect
            buttons[i].addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    buttons[idx].setForeground(ACCENT_CYAN);
                }
                public void mouseExited(MouseEvent e) {
                    buttons[idx].setForeground(
                            buttons[idx].isSelected() ? ACCENT_CYAN : TEXT_PRIMARY);
                }
            });

            if (desc.toLowerCase().contains("realtek") ||
                    desc.toLowerCase().contains("wi-fi") ||
                    desc.toLowerCase().contains("wireless")) {
                buttons[i].setSelected(true);
                buttons[i].setForeground(ACCENT_CYAN);
                anySelected = true;
            }

            group.add(buttons[i]);
            listPanel.add(buttons[i]);
            listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        if (!anySelected && buttons.length > 0) {
            buttons[0].setSelected(true);
            buttons[0].setForeground(ACCENT_CYAN);
        }

        // ── Start button ──────────────────────────────────────────
        JButton startButton = new JButton("  START CAPTURE  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        startButton.setBackground(ACCENT_CYAN);
        startButton.setForeground(BG_DARK);
        startButton.setFont(MONO.deriveFont(Font.BOLD, 15f));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setMaximumSize(new Dimension(280, 48));
        startButton.setPreferredSize(new Dimension(280, 48));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect on start button
        startButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(0, 195, 230));
            }
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(ACCENT_CYAN);
            }
        });

        // ── Version label ─────────────────────────────────────────
        JLabel versionLabel = new JLabel("v1.0  —  Built with Pcap4J + Java Swing") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), x, fm.getAscent());
            }
        };
        versionLabel.setFont(MONO.deriveFont(11f));
        versionLabel.setForeground(TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setPreferredSize(new Dimension(800, 20));
        versionLabel.setMaximumSize(new Dimension(900, 20));

        // ── Assemble ──────────────────────────────────────────────
        centre.add(Box.createVerticalGlue());
        centre.add(nameLabel);
        centre.add(Box.createRigidArea(new Dimension(0, 12)));
        centre.add(subLabel);
        centre.add(Box.createRigidArea(new Dimension(0, 35)));
        centre.add(sep);
        centre.add(Box.createRigidArea(new Dimension(0, 30)));
        centre.add(selectLabel);
        centre.add(Box.createRigidArea(new Dimension(0, 14)));
        centre.add(listPanel);
        centre.add(Box.createRigidArea(new Dimension(0, 24)));
        centre.add(startButton);
        centre.add(Box.createRigidArea(new Dimension(0, 18)));
        centre.add(versionLabel);
        centre.add(Box.createVerticalGlue());

        add(centre, BorderLayout.CENTER);

        // ── Start button action ───────────────────────────────────
        List<PcapNetworkInterface> finalDevices = devices;
        startButton.addActionListener(e -> {
            int selectedIndex = 0;
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].isSelected()) {
                    selectedIndex = i;
                    break;
                }
            }
            PcapNetworkInterface selectedDevice = finalDevices.get(selectedIndex);
            dispose();
            MainWindow window = new MainWindow();
            window.setVisible(true);
            new org.example.capture.PacketCaptureThread(selectedDevice, window).start();
        });

        // ── ESC to exit ───────────────────────────────────────────
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
            }
        });

        setFocusable(true);
        requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}