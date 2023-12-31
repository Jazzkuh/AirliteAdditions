package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedBlinkSpeed;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.registry.ButtonTriggerRegistry;
import com.jazzkuh.airliteadditions.common.registry.ChannelTriggerRegistry;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.framework.channel.ChannelTrigger;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class UDPServer {
    private static final Logger LOGGER = Logger.getLogger(UDPServer.class.getName());

    private @Getter DatagramSocket socket;
    private final @Getter ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final @Getter String hostAddress = "127.0.0.1";
    private final @Getter int port = 19550;
    private final @Getter int hostPort = 19551; // Port number on the Airlite device

    @SneakyThrows
    public UDPServer() {
        try {
            socket = new DatagramSocket(port);
            LOGGER.info("UDP server is running on port " + port);
        } catch (Exception e) {
            LOGGER.severe("Error while starting the UDP server: " + e.getMessage());
        }
    }

    public void start() {
        new UDPListener(socket).start();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendKeepAlive();
            }
        }, 4000, 4000);
    }

    public void sendKeepAlive() {
        try {
            // Create a message with a fixed size of 12 bytes
            byte[] message = new byte[12];
            message[0] = (byte) 0xA0; // Airlite
            message[1] = (byte) 0xA0; // Airlite
            message[2] = (byte) 0x4B; // K
            message[3] = (byte) 0x41; // A

            executorService.submit(() -> {
                try {
                    DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(hostAddress), hostPort);
                    socket.send(sendPacket);
                    System.out.println("Sent keep alive");
                } catch (Exception e) {
                    LOGGER.warning("Error while sending data: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LOGGER.warning("Error while sending data: " + e.getMessage());
        }
    }

    public void writeStaticLed(ControlButton controlButton, ControlLedColor ledColor) {
        try {
            // Create a message with a fixed size of 12 bytes
            byte[] message = new byte[12];
            message[0] = (byte) 0xA0; // Airlite
            message[1] = (byte) 0xA0; // Airlite
            message[2] = (byte) 0x04; // Size
            message[3] = (byte) 0x02; // CMD
            message[4] = controlButton.getButtonId(); // ID
            message[5] = ledColor.getData(); // COLOR

            executorService.submit(() -> {
                try {
                    DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(hostAddress), hostPort);
                    socket.send(sendPacket);
                } catch (Exception e) {
                    LOGGER.warning("Error while sending data: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LOGGER.warning("Error while sending data: " + e.getMessage());
        }
    }

    public void writeBlinkingLed(ControlButton controlButton, ControlLedColor colorOn, ControlLedColor colorOff, ControlLedBlinkSpeed blinkSpeed) {
        try {
            // Create a message with a fixed size of 12 bytes
            byte[] message = new byte[12];
            message[0] = (byte) 0xA0; // Airlite
            message[1] = (byte) 0xA0; // Airlite
            message[2] = (byte) 0x04; // Size
            message[3] = (byte) 0x03; // CMD
            message[4] = controlButton.getButtonId(); // ID
            message[5] = colorOn.getData(); // COLOR ON
            message[6] = colorOff.getData(); // COLOR
            message[7] = blinkSpeed.getData(); // COLOR

            executorService.submit(() -> {
                try {
                    DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(hostAddress), hostPort);
                    socket.send(sendPacket);
                } catch (Exception e) {
                    LOGGER.warning("Error while sending data: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LOGGER.warning("Error while sending data: " + e.getMessage());
        }
    }
}


