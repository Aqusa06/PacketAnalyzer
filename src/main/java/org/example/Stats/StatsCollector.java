package org.example.Stats;

import java.util.HashMap;
import java.util.Map;

public class StatsCollector {
//     Stores count of each protocol
//     Key = protocol name , Value = count
    private Map<String , Integer> protocolCounts = new HashMap<>();
    private int totalPackets = 0;


    private long bytesThisSecond = 0;

    private long lastByteSnapshot = 0;

    public void addBytes(int bytes) {
        bytesThisSecond += bytes;
    }

    public long getBytesPerSecond() {
        long diff = bytesThisSecond - lastByteSnapshot;
        lastByteSnapshot = bytesThisSecond;
        return diff;
    }




//    Called every time a new packet arrives
  public void addPacket(String protocol){
      totalPackets++;
      protocolCounts.put(protocol , protocolCounts.getOrDefault(protocol , 0) + 1);

  }
//  return percentage of one protocol
    public double getPercentage(String protocol){
      if(totalPackets == 0) return 0;
      int count = protocolCounts.getOrDefault(protocol, 0);
      return (count * 100.0 ) / totalPackets;
    }

//    return total packets captured
    public int getTotalPackets(){
      return totalPackets;
    }

//    return the full map - used by StatsPanel to draw chart
    public Map<String , Integer> getProtocolCounts(){
      return protocolCounts;
    }



//    returns count of one specific protocol
    public int getCount(String protocol){
      return protocolCounts.getOrDefault(protocol , 0);
    }
}
