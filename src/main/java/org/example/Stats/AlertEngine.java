package org.example.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertEngine {

//    Stores which ports each IP has tried to connect to
//     key = source IP , value = list of ports it hits
    private Map<String , List<Integer>> ipPortMap = new HashMap<>();

//    Stores alerts generated
    private static List<String> alerts = new ArrayList<>();

//    how many port in 1 seconds = suspicious
  private static final int PORT_SCAN_THRESHOLD = 10;

//  new variable for Exfiltration
    private Map<String, Integer> largePacketCounter = new HashMap<>();
    private long lastResetTime = System.currentTimeMillis();
    private final String MY_LOCAL_IP = getLocalIp();

    private String getLocalIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";       }
    }
    

//  called for every packet
    public void analyse(String sourceIp , int destinationPort , int packetSize){
        if(sourceIp.equals("N/A") || destinationPort == 0) return;

        long currentTime = System.currentTimeMillis();

//        1. Sliding window reset (every 5 seconds)
        if (currentTime - lastResetTime > 5000) {
            largePacketCounter.clear();
            lastResetTime = currentTime;
        }

//         2. data exflitration logic
        if (sourceIp.equals(MY_LOCAL_IP) && packetSize > 1200) {
            largePacketCounter.put(sourceIp, largePacketCounter.getOrDefault(sourceIp, 0) + 1);

            if (largePacketCounter.get(sourceIp) > 100) {
                String exfilAlert = " ALERT: High-Volume Data Exfiltration from " + sourceIp;
                if (!alerts.contains(exfilAlert)) {
                    alerts.add(exfilAlert);
                    System.out.println(exfilAlert);
                }
            }
        }

//        add this port to the list for this IP
        ipPortMap.putIfAbsent(sourceIp , new ArrayList<>());
        List<Integer> ports = ipPortMap.get(sourceIp);

        if (!ports.contains(destinationPort)){
            ports.add(destinationPort);
        }

//        check if this IP has hit too many ports
        if(ports.size()  >= PORT_SCAN_THRESHOLD){
            String alert = " ALERT: Possible port scan detected from" + sourceIp + " - hit " + ports.size() + " ports!";

            if(!alerts.contains(alert)){
                alerts.add(alert);
                System.out.println("ALERT"  + alert);
            }
        }
    }

//    Returns all alerts generated so far
    public List<String> getAlerts(){
        return alerts;
    }

//    clears all data - called when capture restarts
    public void reset(){
        ipPortMap.clear();
        alerts.clear();
        largePacketCounter.clear();  // clear new data too
    }
}
