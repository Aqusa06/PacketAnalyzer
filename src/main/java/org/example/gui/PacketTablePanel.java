package org.example.gui;

import org.example.parser.PacketModel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PacketTablePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;

    public PacketTablePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#1e1e2e"));
        setupTable();
    }

    // Clears all rows from the table
    public void clearPackets() {
        tableModel.setRowCount(0);
    }



    // Adds one packet row to the table
    public void addPacket(PacketModel model) {
        tableModel.insertRow(0, new Object[]{
                model.getTimestamp(),
                model.getSourceIp(),
                model.getDestinationIp(),
                model.getProtocol(),
                model.getSourcePort(),
                model.getDestinationPort(),
                model.getDomainName(),
                model.getSize() + " B"
        });

        // Keep only last 500 rows so app stays fast
        if (tableModel.getRowCount() > 500) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        }
    }



    private void setupTable() {
        setBackground(new Color(13, 17, 23));

        String[] columns = {"Time","Src IP","Dst IP","Protocol","Src Port","Dst Port","Service","Size"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);

                String proto = "";
                try { proto = (String) getValueAt(row, 3); } catch (Exception ignored) {}
                if (proto == null) proto = "";

                // ── Full row background by protocol ──────────────
                if (!isRowSelected(row)) {
                    switch (proto) {
                        case "TCP":  c.setBackground(new Color(20, 40, 25));  break; // dark green
                        case "UDP":  c.setBackground(new Color(18, 30, 55));  break; // dark blue
                        case "ICMP": c.setBackground(new Color(55, 18, 18));  break; // dark red
                        case "ARP":  c.setBackground(new Color(50, 35, 10));  break; // dark gold
                        default:     c.setBackground(row % 2 == 0
                                ? new Color(22, 27, 34)
                                : new Color(17, 21, 28));            break;
                    }
                } else {
                    c.setBackground(new Color(56, 139, 253, 80));
                }

                // ── Text color by column ─────────────────────────
                switch (col) {
                    case 1: case 2:                                          // IPs
                        c.setForeground(new Color(201, 209, 217)); break;
                    case 3:                                                   // Protocol
                        switch (proto) {
                            case "TCP":  c.setForeground(new Color(63, 185, 80));  break;
                            case "UDP":  c.setForeground(new Color(88, 166, 255)); break;
                            case "ICMP": c.setForeground(new Color(248, 81, 73));  break;
                            case "ARP":  c.setForeground(new Color(210, 153, 34)); break;
                            default:     c.setForeground(new Color(139, 148, 158));break;
                        }
                        break;
                    case 6:                                                   // Service
                        c.setForeground(new Color(121, 192, 255)); break;
                    case 7:                                                   // Size
                        c.setForeground(new Color(139, 148, 158)); break;
                    default:
                        c.setForeground(new Color(139, 148, 158)); break;
                }

                c.setFont(new Font("Courier New", Font.PLAIN, 11));
                return c;
            }
        };

        table.setBackground(new Color(22, 27, 34));
        table.setForeground(new Color(139, 148, 158));
        table.setGridColor(new Color(33, 38, 45));
        table.setRowHeight(26);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(56, 139, 253, 80));
        table.setSelectionForeground(Color.WHITE);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(33, 38, 45));
        header.setForeground(new Color(0, 217, 255));
        header.setFont(new Font("Courier New", Font.BOLD, 10));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(33, 38, 45)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(85);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(155);
        table.getColumnModel().getColumn(7).setPreferredWidth(55);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(22, 27, 34));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(33, 38, 45), 1));
        add(scroll, BorderLayout.CENTER);
    }
    // Returns colour based on protocol
    private Color getProtocolColor(String protocol) {
        if (protocol == null) return Color.decode("#1e1e2e");
        switch (protocol) {
            case "TCP":   return Color.decode("#1e3a5f");
            case "UDP":   return Color.decode("#1a3a1a");
            case "ICMP":  return Color.decode("#3a1a1a");
            case "ARP":   return Color.decode("#3a2a1a");
            default:      return Color.decode("#2a2a2a");
        }
    }
    public JTable getTable() {
        return this.table;
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }
}
