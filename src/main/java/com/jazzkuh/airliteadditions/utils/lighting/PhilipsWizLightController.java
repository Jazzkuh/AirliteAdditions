package com.jazzkuh.airliteadditions.utils.lighting;

import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@UtilityClass
public class PhilipsWizLightController {
    private static final int BULB_PORT = 38899;
    private static final int TIMEOUT_MS = 1000;

    @SneakyThrows
    public static void setRGBColor(Bulb bulb, int red, int green, int blue, int brightness) {
        @Cleanup
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"r\":%d,\"g\":%d,\"b\":%d,\"dimming\":%d}}",
                red, green, blue, brightness);

        updateBulb(bulb.getIp(), socket, message);
    }

    public static void setColorTemperature(Bulb bulb, int brightness) {
        setColorTemperature(bulb, 2200, brightness);
    }

    @SneakyThrows
    public static void setColorTemperature(Bulb bulb, int temperature, int brightness) {
        @Cleanup
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);

        // Range of possible temperatures: 2700K to 6500K
        if (temperature < 2200 || temperature > 6500) {
            System.out.println("Invalid color temperature. The valid range is 2700K to 6500K.");
            return;
        }

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"temp\":%d,\"dimming\":%d}}",
                temperature, brightness);

        updateBulb(bulb.getIp(), socket, message);
    }

    @SneakyThrows
    public static void setScene(Bulb bulb, Scene scene, int brightness) {
        @Cleanup
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);

        int sceneId = scene.ordinal() + 1; // Scene IDs are 1-based

        // Range of valid scene IDs: 1 to 32
        if (sceneId > 32) {
            System.out.println("Invalid scene ID. The valid range is 1 to 32.");
            return;
        }

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"sceneId\":%d,\"dimming\":%d}}",
                sceneId, brightness);

        updateBulb(bulb.getIp(), socket, message);
    }

    private static void updateBulb(String socketAddress, DatagramSocket socket, String message) {
        try {
            byte[] sendData = message.getBytes();
            InetAddress ipAddress = InetAddress.getByName(socketAddress);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, BULB_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Response: " + response);
        } catch (Exception ignored) {
        }
    }

    public enum Scene {
        Ocean,
        Romance,
        Sunset,
        Party,
        Fireplace,
        Cozy,
        Forest,
        PastelColors,
        WakeUp,
        Bedtime,
        WarmWhite,
        Daylight,
        CoolWhite,
        NightLight,
        Focus,
        Relax,
        TrueColors,
        TVTime,
        PlantGrowth,
        Spring,
        Summer,
        Fall,
        DeepDive,
        Jungle,
        Mojito,
        Club,
        Christmas,
        Halloween,
        Candlelight,
        GoldenWhite,
        Pulse,
        Steampunk
    }
}