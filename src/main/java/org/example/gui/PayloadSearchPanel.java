package org.example.gui;

import org.example.parser.PacketModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PayloadSearchPanel.java
 * -----------------------
 * Deep Packet Inspection (DPI) search bar.
 * Searches through packet payloads and header fields in real time.
 *
 * What it proves: You can look past the "envelope" (headers)
 * and read the "letter" (actual data) inside packets.
 */
public class PayloadSearchPanel extends JPanel {

    private JTextField searchField;
    private JComboBox<String> filterType;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel resultLabel;

    // Reference to the main table to apply filters
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Stores all raw payloads for searching
    private List<String> payloads = new ArrayList<>();

    public PayloadSearchPanel(JTable table, DefaultTableModel tableModel) {
        this.table      = table;
        this.tableModel = tableModel;
        this.sorter     = new TableRowSorter<>(tableModel);
        this.table.setRowSorter(sorter);
        setupPanel();
    }

    private void setupPanel() {
        setBackground(new Color(22, 27, 34));           // was #181825
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(33, 38, 45)));

        JLabel dpiLabel = new JLabel("DPI SEARCH:");
        dpiLabel.setForeground(new Color(0, 217, 255));  // cyan
        dpiLabel.setFont(new Font("Courier New", Font.PLAIN, 11));

        filterType = new JComboBox<>(new String[]{
                "All Fields","Source IP","Dest IP","Protocol","Service","Payload"
        });
        filterType.setBackground(new Color(33, 38, 45));
        filterType.setForeground(new Color(201, 209, 217));
        filterType.setFont(new Font("Courier New", Font.PLAIN, 11));

        searchField = new JTextField(22);
        searchField.setBackground(new Color(33, 38, 45));
        searchField.setForeground(new Color(201, 209, 217));
        searchField.setCaretColor(new Color(0, 217, 255));
        searchField.setFont(new Font("Courier New", Font.PLAIN, 11));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 54, 61)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(31, 111, 235));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Courier New", Font.PLAIN, 11));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(33, 38, 45));
        clearButton.setForeground(new Color(139, 148, 158));
        clearButton.setFont(new Font("Courier New", Font.PLAIN, 11));
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        resultLabel = new JLabel("Showing all packets");
        resultLabel.setForeground(new Color(63, 185, 80));
        resultLabel.setFont(new Font("Courier New", Font.PLAIN, 10));

        // rest of your add() calls stay the same
        add(dpiLabel); add(filterType); add(searchField);
        add(searchButton); add(clearButton); add(resultLabel);

        searchButton.addActionListener(e -> applyFilter());
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) applyFilter();
            }
        });
        clearButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
            resultLabel.setText("Showing all packets");
            resultLabel.setForeground(new Color(63, 185, 80));
        });
    }

    // ── Apply filter based on search text and selected field ──────
    private void applyFilter() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
            resultLabel.setText("Showing all packets");
            return;
        }

        String selected = (String) filterType.getSelectedItem();
        int columnIndex = getColumnIndex(selected);

        try {
            RowFilter<DefaultTableModel, Object> filter;

            if (columnIndex == -1) {
                // Search ALL columns
                filter = RowFilter.regexFilter(
                        "(?i)" + query);
            } else {
                // Search specific column only
                filter = RowFilter.regexFilter(
                        "(?i)" + query, columnIndex);
            }

            sorter.setRowFilter(filter);

            int visible = table.getRowCount();
            int total   = tableModel.getRowCount();

            if (visible == 0) {
                resultLabel.setText("No matches found for: " + query);
                resultLabel.setForeground(Color.decode("#f38ba8"));
            } else {
                resultLabel.setText("Found " + visible
                        + " of " + total + " packets");
                resultLabel.setForeground(Color.decode("#a6e3a1"));
            }

        } catch (Exception ex) {
            resultLabel.setText("Invalid search pattern");
            resultLabel.setForeground(Color.decode("#f38ba8"));
        }
    }

    // ── Map dropdown selection to table column index ──────────────
    private int getColumnIndex(String selected) {
        switch (selected) {
            case "Source IP":  return 1;
            case "Dest IP":    return 2;
            case "Protocol":   return 3;
            case "Service":    return 6;
            default:           return -1; // all fields
        }
    }
}