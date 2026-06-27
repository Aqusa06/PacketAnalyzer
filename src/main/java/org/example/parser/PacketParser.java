package org.example.parser;

import org.pcap4j.packet.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PacketParser {

//     parse() receives one raw packet and returns a PacketModel
//    This is the onion peeling = layer by layer
    public static PacketModel parse(Packet rawPacket) {

//         general info
        int size = rawPacket.length();
        String timestamp = LocalDateTime.now(). format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

//        Default values of a layer is missing
        String srcMac = "N/A";
        String dstMac = "N/A";
        String srcIp = "N/A";
        String dstIp = "N/A";
        String protocol = "N/A";
        int srcPort = 0;
        int dstPort = 0;
        String domain = "Non-Ip Traffic";

//         Layer 2 - ethernet (peel the outer layer )
//         Gets the hardware MAC address of sender and receiver
         if(rawPacket.contains(EthernetPacket.class)){
             EthernetPacket eth = rawPacket.get(EthernetPacket.class);
             srcMac = eth.getHeader().getSrcAddr().toString();
             dstMac = eth.getHeader().getDstAddr().toString();
         }

//         Layer 3 - IP (peel teh next layer)
//        Gets the logical IP address of source and destination
        if(rawPacket.contains(IpV4Packet.class)) {
            IpV4Packet ip = rawPacket.get(IpV4Packet.class);
            srcIp    = ip.getHeader().getSrcAddr().getHostAddress();
            dstIp    = ip.getHeader().getDstAddr().getHostAddress();
            protocol = ip.getHeader().getProtocol().name();
            domain   = "Direct IP";

            // ── Layer 4 — TCP (peel deeper) ───────────────────────
            // TCP is used by: websites, email, SSH, databases
            if (rawPacket.contains(TcpPacket.class)) {
                TcpPacket tcp = rawPacket.get(TcpPacket.class);
                srcPort = tcp.getHeader().getSrcPort().valueAsInt();
                dstPort = tcp.getHeader().getDstPort().valueAsInt();
                protocol = "TCP";

                // ── Layer 7 — Application (identify the app) ──────
                // Port number tells us WHICH app is communicating
                if      (dstPort == 80   || srcPort == 80)   domain = "Web (HTTP)";
                else if (dstPort == 443  || srcPort == 443)  domain = "Secure Web (HTTPS)";
                else if (dstPort == 22   || srcPort == 22)   domain = "SSH Connection";
                else if (dstPort == 25   || srcPort == 25)   domain = "Email (SMTP)";
                else if (dstPort == 587  || srcPort == 587)  domain = "Email (SMTP TLS)";
                else if (dstPort == 143  || srcPort == 143)  domain = "Email (IMAP)";
                else if (dstPort == 3306 || srcPort == 3306) domain = "Database (MySQL)";
                else if (dstPort == 5432 || srcPort == 5432) domain = "Database (PostgreSQL)";
                else if (dstPort == 8080 || srcPort == 8080) domain = "Web Proxy / Dev Server";
                else if (dstPort == 21   || srcPort == 21)   domain = "File Transfer (FTP)";
                else                                          domain = "TCP — Port " + dstPort;
            }

            // ── Layer 4 — UDP (peel deeper) ───────────────────────
            // UDP is used by: DNS, video streaming, online games
            else if (rawPacket.contains(UdpPacket.class)) {
                UdpPacket udp = rawPacket.get(UdpPacket.class);
                srcPort  = udp.getHeader().getSrcPort().valueAsInt();
                dstPort  = udp.getHeader().getDstPort().valueAsInt();
                protocol = "UDP";

                // ── Layer 7 — Application ─────────────────────────
                if      (dstPort == 53  || srcPort == 53)  domain = "DNS Lookup";
                else if (dstPort == 67  || dstPort == 68)  domain = "DHCP (IP Assignment)";
                else if (dstPort == 123 || srcPort == 123) domain = "NTP (Time Sync)";
                else if (dstPort == 443 || srcPort == 443) domain = "QUIC / HTTP3";
                else if (dstPort == 5353)                  domain = "mDNS (Local Discovery)";
                else                                        domain = "UDP — Port " + dstPort;
            }

            // ── ICMP — ping packets ────────────────────────────────
            else if (rawPacket.contains(IcmpV4CommonPacket.class)) {
                protocol = "ICMP";
                domain   = "Ping / Network Test";
            }
        }

        // ── ARP — finding who has what IP on local network ────────
        else if (rawPacket.contains(ArpPacket.class)) {
            protocol = "ARP";
            domain   = "Local Network Discovery";
        }

        // ── Return the filled PacketModel ─────────────────────────
        return new PacketModel(
                srcMac, dstMac,
                srcIp,  dstIp,
                protocol, srcPort, dstPort,
                size, timestamp, domain
        );

        }

    // Overload for loading from pcap file with real capture timestamp
    public static PacketModel parseWithTimestamp(Packet rawPacket, String timestamp) {
        int size = rawPacket.length();

        String srcMac = "N/A", dstMac = "N/A";
        String srcIp = "N/A", dstIp = "N/A";
        String protocol = "N/A";
        int srcPort = 0, dstPort = 0;
        String domain = "Non-Ip Traffic";

        if (rawPacket.contains(EthernetPacket.class)) {
            EthernetPacket eth = rawPacket.get(EthernetPacket.class);
            srcMac = eth.getHeader().getSrcAddr().toString();
            dstMac = eth.getHeader().getDstAddr().toString();
        }

        if (rawPacket.contains(IpV4Packet.class)) {
            IpV4Packet ip = rawPacket.get(IpV4Packet.class);
            srcIp    = ip.getHeader().getSrcAddr().getHostAddress();
            dstIp    = ip.getHeader().getDstAddr().getHostAddress();
            protocol = ip.getHeader().getProtocol().name();
            domain   = "Direct IP";

            if (rawPacket.contains(TcpPacket.class)) {
                TcpPacket tcp = rawPacket.get(TcpPacket.class);
                srcPort = tcp.getHeader().getSrcPort().valueAsInt();
                dstPort = tcp.getHeader().getDstPort().valueAsInt();
                protocol = "TCP";

                if      (dstPort == 80   || srcPort == 80)   domain = "Web (HTTP)";
                else if (dstPort == 443  || srcPort == 443)  domain = "Secure Web (HTTPS)";
                else if (dstPort == 22   || srcPort == 22)   domain = "SSH Connection";
                else if (dstPort == 25   || srcPort == 25)   domain = "Email (SMTP)";
                else if (dstPort == 587  || srcPort == 587)  domain = "Email (SMTP TLS)";
                else if (dstPort == 143  || srcPort == 143)  domain = "Email (IMAP)";
                else if (dstPort == 3306 || srcPort == 3306) domain = "Database (MySQL)";
                else if (dstPort == 5432 || srcPort == 5432) domain = "Database (PostgreSQL)";
                else if (dstPort == 8080 || srcPort == 8080) domain = "Web Proxy / Dev Server";
                else if (dstPort == 21   || srcPort == 21)   domain = "File Transfer (FTP)";
                else                                          domain = "TCP — Port " + dstPort;
            }
            else if (rawPacket.contains(UdpPacket.class)) {
                UdpPacket udp = rawPacket.get(UdpPacket.class);
                srcPort  = udp.getHeader().getSrcPort().valueAsInt();
                dstPort  = udp.getHeader().getDstPort().valueAsInt();
                protocol = "UDP";

                if      (dstPort == 53  || srcPort == 53)  domain = "DNS Lookup";
                else if (dstPort == 67  || dstPort == 68)  domain = "DHCP (IP Assignment)";
                else if (dstPort == 123 || srcPort == 123) domain = "NTP (Time Sync)";
                else if (dstPort == 443 || srcPort == 443) domain = "QUIC / HTTP3";
                else if (dstPort == 5353)                  domain = "mDNS (Local Discovery)";
                else                                        domain = "UDP — Port " + dstPort;
            }
            else if (rawPacket.contains(IcmpV4CommonPacket.class)) {
                protocol = "ICMP";
                domain   = "Ping / Network Test";
            }
        }
        else if (rawPacket.contains(ArpPacket.class)) {
            protocol = "ARP";
            domain   = "Local Network Discovery";
        }

        return new PacketModel(
                srcMac, dstMac,
                srcIp,  dstIp,
                protocol, srcPort, dstPort,
                size, timestamp, domain
        );
    }

    }

