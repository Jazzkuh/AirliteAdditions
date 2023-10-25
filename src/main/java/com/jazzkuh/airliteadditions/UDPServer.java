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

public class UDPServer {
    private @Getter DatagramSocket socket;
    private final @Getter String hostAddress = "127.0.0.1";
    private final @Getter int port = 19550;
    private final @Getter int hostPort = 19551; // Port number on the Airlite device

    @SneakyThrows
    public UDPServer() {
        socket = new DatagramSocket(port);
        System.out.println("UDP server is running on port " + port);
    }

    @SneakyThrows
    public void start() {
        byte[] receiveData = new byte[12];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String senderAddress = receivePacket.getAddress().getHostAddress();
            int senderPort = receivePacket.getPort();
            byte[] data = receivePacket.getData();

            System.out.print("Received from " + senderAddress + ":" + senderPort + " - ");
            System.out.println("Data: " + Arrays.toString(receivePacket.getData()));

            if (data[3] == -32) {
                for (int i = 1; i <= 8; i++) {
                    AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(i);
                    int airliteIndex = airliteFaderStatus.getAirliteIndex();
                    boolean channelOn = data[airliteIndex] >= 1;

                    if (airliteFaderStatus.isChannelOn() == channelOn) continue;
                    airliteFaderStatus.setChannelOn(channelOn);

                    ChannelTrigger channelTrigger = new ChannelTrigger(i, channelOn ? TriggerType.CHANNEL_ON : TriggerType.CHANNEL_OFF);
                    if (channelOn && airliteFaderStatus.isFaderActive()) {
                        TriggerAction triggerAction = ChannelTriggerRegistry.getAction(new ChannelTrigger(i, TriggerType.FADER_AND_CHANNEL_ON));
                        if (triggerAction != null) {
                            triggerAction.process();
                            System.out.println("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                        }
                    }

                    TriggerAction triggerAction = ChannelTriggerRegistry.getAction(channelTrigger);
                    if (triggerAction != null) {
                        triggerAction.process();
                        System.out.println("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                    }
                }
            }

            if (data[3] == -30) {
                for (int i = 1; i <= 8; i++) {
                    AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(i);
                    int airliteIndex = airliteFaderStatus.getAirliteIndex();
                    boolean faderActive = data[airliteIndex] >= 1;

                    if (airliteFaderStatus.isFaderActive() == faderActive) continue;
                    airliteFaderStatus.setFaderActive(faderActive);

                    ChannelTrigger channelTrigger = new ChannelTrigger(i, faderActive ? TriggerType.FADER_ON : TriggerType.FADER_OFF);
                    if (airliteFaderStatus.isChannelOn() && faderActive) {
                        TriggerAction triggerAction = ChannelTriggerRegistry.getAction(new ChannelTrigger(i, TriggerType.FADER_AND_CHANNEL_ON));
                        if (triggerAction != null) {
                            triggerAction.process();
                            System.out.println("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                        }
                    }

                    TriggerAction triggerAction = ChannelTriggerRegistry.getAction(channelTrigger);
                    if (triggerAction != null) {
                        triggerAction.process();
                        System.out.println("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                    }
                }
            }

            if (data[3] == -60) {
                int buttonId = data[10];
                int pressedValueA = data[4];
                int pressedValueB = data[5];

                ControlButton controlButton = ControlButton.getButton(buttonId);
                if (controlButton == null) continue;

                boolean pressed = controlButton.getRow().equals("A") && pressedValueA == controlButton.getPressedValue() || controlButton.getRow().equals("B") && pressedValueB == controlButton.getPressedValue();
                ButtonTrigger buttonTrigger = new ButtonTrigger(controlButton, pressed ? TriggerType.BUTTON_PRESSED : TriggerType.BUTTON_RELEASED);
                TriggerAction triggerAction = ButtonTriggerRegistry.getAction(buttonTrigger);

                if (controlButton.isHasPressedColor() && ButtonTriggerRegistry.isKnownButton(controlButton)) {
                    if (pressed) writeStaticLed(controlButton, ControlLedColor.RED);
                    else writeStaticLed(controlButton, ControlLedColor.GREEN);
                }

                if (triggerAction != null) {
                    triggerAction.process();
                    System.out.println("Triggered action for button " + controlButton + ": " + triggerAction.getClass().getSimpleName());
                }
            }
        }
    }


    @SneakyThrows
    public void writeStaticLed(ControlButton controlButton, ControlLedColor ledColor) {
        // Create a message with a fixed size of 12 bytes
        byte[] message = new byte[12];
        message[0] = (byte) 0xA0; // Airlite
        message[1] = (byte) 0xA0; // Airlite
        message[2] = (byte) 0x04; // Size
        message[3] = (byte) 0x02; // CMD
        message[4] = controlButton.getButtonId(); // ID
        message[5] = ledColor.getData(); // COLOR

        DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(hostAddress), hostPort);
        socket.send(sendPacket);
    }

    @SneakyThrows
    public void writeBlinkingLed(ControlButton controlButton, ControlLedColor colorOn, ControlLedColor colorOff, ControlLedBlinkSpeed blinkSpeed) {
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

        DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(hostAddress), hostPort);
        socket.send(sendPacket);
    }
}


