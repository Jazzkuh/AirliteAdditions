package com.jazzkuh.airliteadditions.common.udp;

import com.jazzkuh.airliteadditions.common.web.WebServer;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener extends Thread {
    private @Getter DatagramSocket socket;
    private final @Getter String hostAddress = "127.0.0.1";

    byte[] receiveData = new byte[12];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

    @SneakyThrows
    public UDPListener(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            boolean running = true;
            while (running) {
                socket.receive(receivePacket);
                WebServer.broadcastMessage();

                //Send packet message to UI
                byte[] data = receivePacket.getData();
                UDPReceiveHandler.process(data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}