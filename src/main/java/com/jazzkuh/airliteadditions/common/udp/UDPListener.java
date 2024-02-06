package com.jazzkuh.airliteadditions.common.udp;

import lombok.Getter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener extends Thread {
    private @Getter DatagramSocket socket;
    private final @Getter String hostAddress = "127.0.0.1";
    private final @Getter int port = 19550;
    private final @Getter int hostPort = 19551; // Port number on the Airlite device

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

                //Send packet message to UI
                byte[] data = receivePacket.getData();
                UDPReceiveHandler.process(data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}