package org.example.capture;


import org.example.gui.MainWindow;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import javax.swing.*;
import java.util.List;
import java.util.Scanner;

public class DeviceManager {
    public static void main(String[] args) throws Exception {

//        Find all network interface on this laptop
        List<PcapNetworkInterface> devices = Pcaps.findAllDevs();

//        Print each one

        for(int i = 0; i < devices.size(); i++){
            System.out.println(i + " : " + devices.get(i).getDescription());
        }

//         Get User Choice
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter index of the device to capture : ");
        int choice = sc.nextInt();

//        Select the device
        PcapNetworkInterface selectedDevice = devices.get(choice);
        System.out.println("Selected: " + selectedDevice.getDescription());

//        Create and show the GUI window
        MainWindow window = new MainWindow();
        window.setVisible(true);

//        Start teh background capture thread
        PacketCaptureThread captureThread = new PacketCaptureThread(selectedDevice , window);
        captureThread.start();

    }
}
