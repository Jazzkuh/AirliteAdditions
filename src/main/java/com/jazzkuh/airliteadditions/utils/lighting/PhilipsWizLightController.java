package com.jazzkuh.airliteadditions.utils.lighting;

import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

@UtilityClass
public class PhilipsWizLightController {
    private static final int BULB_PORT = 38899;

    @Getter
    private final Queue<DatagramPacket> packetQueue = new LinkedList<>();

    @SneakyThrows
    public static void setRGBColor(Bulb bulb, int red, int green, int blue, int brightness) {
        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"r\":%d,\"g\":%d,\"b\":%d,\"dimming\":%d}}",
                red, green, blue, brightness);

        updateBulb(bulb.getIp(), message);
    }

    public static void setColorTemperature(Bulb bulb, int brightness) {
        setColorTemperature(bulb, 2200, brightness);
    }

    @SneakyThrows
    public static void setColorTemperature(Bulb bulb, int temperature, int brightness) {
        // Range of possible temperatures: 2700K to 6500K
        if (temperature < 2200 || temperature > 6500) {
            System.out.println("Invalid color temperature. The valid range is 2700K to 6500K.");
            return;
        }

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"temp\":%d,\"dimming\":%d}}",
                temperature, brightness);

        updateBulb(bulb.getIp(), message);
    }

    @SneakyThrows
    public static void setState(Bulb bulb, boolean on) {
        if (bulb == null) {
            System.out.println("Bulb is null.");
            return;
        }

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"state\":" + (on ? "true" : "false") + "}}");
        updateBulb(bulb.getIp(), message);
    }

    @SneakyThrows
    public static void setScene(Bulb bulb, Scene scene, int brightness) {
        int sceneId = scene.ordinal() + 1; // Scene IDs are 1-based

        String message = String.format("{\"method\":\"setPilot\",\"params\":{\"sceneId\":%d,\"dimming\":%d}}",
                sceneId, brightness);

        updateBulb(bulb.getIp(), message);
    }

    @SneakyThrows
    private static void updateBulb(String socketAddress, String message) {
        byte[] sendData = message.getBytes();
        InetAddress ipAddress = InetAddress.getByName(socketAddress);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, BULB_PORT);
        packetQueue.add(sendPacket);
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
        Steampunk,
        Diwali,
        Snow,
        Warning
    }
}