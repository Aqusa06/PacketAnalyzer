package org.example.Stats;

import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;

public class PcapFileWriter {
    private PcapDumper dumper;
    private int packetCount = 0;

    // Called when you hit the "Start" button
    public void startNewCapture(org.pcap4j.core.PcapHandle handle, String filePath) {
        try {
            // This opens the file for writing immediately
            this.dumper = handle.dumpOpen(filePath);
            this.packetCount = 0;
            System.out.println("PCAP Dumper started: " + filePath);
        } catch (Exception e) {
            System.out.println("Failed to open PCAP dumper: " + e.getMessage());
        }
    }

    // Called every time a packet arrives in your listener
    public void storePacket(Packet packet) {
        if (dumper != null) {
            try {
                dumper.dump(packet);
                packetCount++;
            } catch (Exception e) {
                e.printStackTrace();
                // Silently handle or log error
            }
        }
    }

    // Called when you hit the "Stop" button
    public void stopAndClose() {
        if (dumper != null) {
            dumper.close();
            dumper = null;
            System.out.println("PCAP Dumper closed. Total packets saved: " + packetCount);
        }
    }
}