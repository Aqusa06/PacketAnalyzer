package org.example.parser;

public class PacketModel {

    //    Layer 2 - Ethernet (Hardware MAC addresses)
    private String sourceMAC;
    private String destinationMAC;

    //     Layer 3 - IP (Logical addressing)
    private String sourceIP;
    private String destinationIP;

    //    Layer 4 - Transport ( Protocol + Port number )
    private String protocol;
    private int sourcePort;
    private int destinationPort;

    //    Layer 7 - Application ( What app is using this?)
    private String domainName;

    //    General detail
    private int size;
    private String timestamp;


//    Constructor

    public PacketModel(String sourceMAC, String destinationMAC, String sourceIP, String destinationIP, String protocol, int sourcePort, int destinationPort, int size, String timestamp, String domainName) {
        this.sourceMAC = sourceMAC;
        this.destinationMAC = destinationMAC;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.protocol = protocol;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.size = size;
        this.timestamp = timestamp;
        this.domainName = domainName;

    }

    //     Getter
    public String getSourceMac() {
        return sourceMAC;
    }

    public String getDestinationMac() {
        return destinationMAC;
    }

    public String getSourceIp() {
        return sourceIP;
    }

    public String getDestinationIp() {
        return destinationIP;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public int getSize() {
        return size;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDomainName() {
        return domainName;
    }

//      toString - prints packet in OSI layer order
//         This is what prints in your console for every packet

    @Override
    public String toString() {
        return "\n╔══════════════════════════════════════════════╗" +
                "\n║           PACKET CAPTURED — " + timestamp + "     ║" +
                "\n╠══════════════════════════════════════════════╣" +
                "\n║ [Layer 2 - Ethernet]  Src MAC : " + sourceMAC +
                "\n║ [Layer 2 - Ethernet]  Dst MAC : " + destinationMAC +
                "\n║ [Layer 3 - IP]        Src IP  : " + sourceIP +
                "\n║ [Layer 3 - IP]        Dst IP  : " + destinationIP +
                "\n║ [Layer 4 - Transport] Protocol: " + protocol +
                "\n║ [Layer 4 - Transport] Src Port: " + sourcePort +
                "\n║ [Layer 4 - Transport] Dst Port: " + destinationPort +
                "\n║ [Layer 7 - App]       Service : " + domainName +
                "\n║ Size : " + size + " bytes" +
                "\n╚══════════════════════════════════════════════╝";


    }
}