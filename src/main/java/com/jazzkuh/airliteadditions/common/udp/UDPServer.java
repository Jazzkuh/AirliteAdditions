package com.jazzkuh.airliteadditions.common.udp;

import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedBlinkSpeed;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.udp.metering.MeteringListener;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class UDPServer {
    private static final Logger LOGGER = Logger.getLogger(UDPServer.class.getName());

    private @Getter DatagramSocket socket;
    private @Getter DatagramSocket meteringSocket;
    private final @Getter ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final @Getter String hostAddress = "127.0.0.1";
    private final @Getter int port = 19549;
    private final @Getter int hostPort = 19551; // Port number on the Airlite device

    @SneakyThrows
    public UDPServer() {
        try {
            socket = new DatagramSocket(port);
            LOGGER.info("UDP server is running on port " + port);

            meteringSocket = new DatagramSocket(19548);
            LOGGER.info("UDP metering server is running on port 19548");
        } catch (Exception e) {
            LOGGER.severe("Error while starting the UDP server: " + e.getMessage());
        }
    }

    public void start() {
        new UDPListener(socket).start();
        new MeteringListener(meteringSocket).start();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendKeepAlive();
            }
        }, 4000, 4000);
        this.write((byte) 0x02, (byte) 0x63);
        this.write((byte) 0x02, (byte) 0x61);
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

    public void writeRemoteOn(AirliteFaderStatus faderStatus, boolean activate) {
        try {
            // Create a message with a fixed size of 12 bytes
            byte[] message = new byte[12];
            message[0] = (byte) 0xA0; // Airlite
            message[1] = (byte) 0xA0; // Airlite
            message[2] = (byte) 0x04; // Size
            message[3] = (byte) 0x05; // CMD
            message[4] = faderStatus.getModule(); // ID
            message[5] = (byte) (activate ? 0x01 : 0x00); // ON/OFF

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

    public void write(byte size, byte cmd, byte... data) {
        try {
            // Create a message with a fixed size of 12 bytes
            byte[] message = new byte[12];
            message[0] = (byte) 0xA0; // Airlite
            message[1] = (byte) 0xA0; // Airlite
            message[2] = size; // Size
            message[3] = cmd; // CMD

            if (data.length > 0) {
                System.arraycopy(data, 0, message, 4, data.length);
            }

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


