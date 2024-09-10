package com.jazzkuh.airliteadditions.utils.lighting;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.TimerTask;

public class PacketRunnable extends TimerTask {
    @Override
    @SneakyThrows
    public void run() {
        Queue<DatagramPacket> packetQueue = PhilipsWizLightController.getPacketQueue();
        DatagramPacket packet = packetQueue.poll();
        if (packet == null) return;

        @Cleanup
        DatagramSocket socket = new DatagramSocket();
        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Failed to send packet: " + e.getMessage());
        }
    }
}
