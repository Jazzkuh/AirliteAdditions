package com.jazzkuh.airliteadditions.common.udp.metering;

import com.jazzkuh.airliteadditions.common.web.WebServer;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MeteringListener extends Thread {
    private @Getter DatagramSocket socket;
    byte[] receiveData = new byte[12];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

    @SneakyThrows
    public MeteringListener(DatagramSocket socket) {
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
                MeteringReceiveHandler.process(data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}