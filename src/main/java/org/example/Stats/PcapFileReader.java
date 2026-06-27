package org.example.Stats;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.example.parser.PacketParser;
import org.example.parser.PacketModel;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PcapFileReader {

    public List<PacketModel> loadFromFile(String filePath) {
        List<PacketModel> packets = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        try {
            PcapHandle handle = Pcaps.openOffline(filePath);
            System.out.println("PCAP file opened: " + filePath);

            Packet packet;
            while ((packet = handle.getNextPacket()) != null) {
                // Get the real timestamp from the pcap file
                Timestamp ts = handle.getTimestamp();
                String timestamp = ts.toLocalDateTime().format(fmt);

                PacketModel model = PacketParser.parseWithTimestamp(packet, timestamp);
                if (model != null) {
                    packets.add(model);
                }
            }

            handle.close();
            System.out.println("Total packets loaded: " + packets.size());

        } catch (Exception e) {
            System.out.println("Failed to load PCAP file: " + e.getMessage());
        }

        return packets;
    }
}