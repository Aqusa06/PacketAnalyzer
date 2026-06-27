# 🛡️ PacketAnalyzer — NetScan Pro

> A real-time network packet analyzer and cyber defense dashboard built in Java.

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Pcap4J](https://img.shields.io/badge/Pcap4J-1.8.2-blue)
![Maven](https://img.shields.io/badge/Maven-3.x-red?logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)

---

## 📌 About

**PacketAnalyzer (NetScan Pro)** is a Java-based network packet analyzer that captures live traffic directly from your Network Interface Card (NIC). It provides a professional dark-themed dashboard for monitoring network activity, detecting security threats, and analyzing saved PCAP capture files.

This is **not** a Wireshark wrapper — it captures and displays packets natively using **Pcap4J**.

---

## ✨ Features

| Feature | Description |
|---|---|
| ✅ **Live NIC Traffic Capture** | Capture packets in real-time from any network interface |
| ✅ **Real-Time Protocol Stats** | Live pie chart — TCP, UDP, ARP, ICMP breakdown |
| ✅ **Security Threat Detection** | Automatically detects suspicious network activity |
| ✅ **PCAP File Support** | Load and analyze previously saved capture sessions |
| ✅ **Auto Save Captures** | Every session automatically saved as a .pcap file |
| ✅ **DPI Search** | Deep packet inspection search across all fields |
| ✅ **Dark Cyber Dashboard** | Professional dark-themed Java Swing UI |

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| **Java** | 17 | Core programming language |
| **Pcap4J** | 1.8.2 | Native packet capture from NIC |
| **Java Swing** | Built-in | Dark-themed GUI dashboard |
| **Maven** | 3.x | Build tool and dependency management |
| **Npcap** | Latest | Windows network driver for capture |

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Installation Guide |
|---|---|
| **Java 17+** | [Download JDK](https://adoptium.net/) |
| **Maven 3.x** | [Download Maven](https://maven.apache.org/download.cgi) |
| **Npcap** | [Download Npcap](https://npcap.com/) |
| **Git** | [Download Git](https://git-scm.com/downloads) |

### Installation

1. Clone the repository
```bash
git clone https://github.com/Aqusa06/PacketAnalyzer.git
cd PacketAnalyzer
```
2. Open in **IntelliJ IDEA**
3. Run `MainWindow.java`
4. Select your network interface
5. Packets start flowing! 🚀

---

## 📁 Project Structure

```
src/main/java/org/example/
├── capture/
│   ├── DeviceManager.java
│   └── PacketCaptureThread.java
├── gui/
│   ├── MainWindow.java
│   ├── PacketTablePanel.java
│   ├── PayloadSearchPanel.java
│   ├── SplashScreen.java
│   └── StatsPanel.java
├── parser/
│   ├── PacketModel.java
│   └── PacketParser.java
└── Stats/
    ├── AlertEngine.java
    ├── PcapFileReader.java
    ├── PcapFileWriter.java
    └── StatsCollector.java
```

---

## 📸 Preview

### Splash Screen
![Splash Screen](screenshot.png)

### Live Dashboard
![NetScan Pro Dashboard](dashboard.png)

---

## 👩‍💻 Author

**Aqusa Fatima** — [@Aqusa06](https://github.com/Aqusa06)

---

⭐ *If you find this project useful, please give it a star — it means a lot!*
