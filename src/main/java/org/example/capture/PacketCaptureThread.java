package org.example.capture;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.example.parser.PacketModel;
import org.example.parser.PacketParser;
import org.example.gui.MainWindow;
import org.example.Stats.PcapFileWriter;
import javax.swing.SwingUtilities;

import javax.swing.*;

public class PacketCaptureThread extends Thread {
    private final PcapNetworkInterface device;
    private final MainWindow window;
    private final PcapFileWriter fileWriter  = new PcapFileWriter();

    // Constructor: Receives the device from DeviceManager
    public PacketCaptureThread(PcapNetworkInterface device , MainWindow window) {
        this.device = device;
        this.window = window;
    }

    @Override
    public void run() {
        int snaplen = 65536;  // Max bytes per packet — capture full packet
        int timeout = 10;     // 10ms timeout — keeps app responsive

        try (PcapHandle handle = device.openLive(snaplen, PromiscuousMode.PROMISCUOUS, timeout)) {

            // Auto-name PCAP file with timestamp
            String autoSavePath = "NetScan_"
                    + java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter
                            .ofPattern("HH-mm-ss"))
                    + ".pcap";

            // Start writing to disk immediately
            fileWriter.startNewCapture(handle , autoSavePath);
            System.out.println("Auto-saving to: " + autoSavePath);

            while (true) {
                Packet packet = handle.getNextPacket();

                if (packet != null) {
                    fileWriter.storePacket(packet);
                    PacketModel model = PacketParser.parse(packet);
                    SwingUtilities.invokeLater(() ->
                            window.onPacketReceived(model));
                }
            }

        } catch (Exception e) {
            System.out.println("Capture error: " + e.getMessage());
        } finally {
            fileWriter.stopAndClose();
        }
    }

    }
