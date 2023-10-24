package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.framework.trigger.ChannelTriggerAction;
import com.jazzkuh.airliteadditions.framework.trigger.Trigger;
import com.jazzkuh.airliteadditions.framework.trigger.TriggerType;
import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {
    @SneakyThrows
    public void start() {
        int port = 19550; // Choose a port number for the UDP server
        DatagramSocket socket = new DatagramSocket(port);

        byte[] receiveData = new byte[12]; // Adjust the buffer size as needed

        System.out.println("UDP server is running on port " + port);

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

                    Trigger trigger = new Trigger(i, channelOn ? TriggerType.CHANNEL_ON : TriggerType.CHANNEL_OFF);
                    if (channelOn && airliteFaderStatus.isFaderActive()) {
                        ChannelTriggerAction channelTriggerAction = ChannelTriggerRegistry.getAction(new Trigger(i, TriggerType.FADER_AND_CHANNEL_ON));
                        if (channelTriggerAction != null) {
                            channelTriggerAction.process(airliteFaderStatus);
                            System.out.println("Triggered action for channel " + i + ": " + channelTriggerAction.getClass().getSimpleName());
                        }
                    }

                    ChannelTriggerAction channelTriggerAction = ChannelTriggerRegistry.getAction(trigger);
                    if (channelTriggerAction != null) {
                        channelTriggerAction.process(airliteFaderStatus);
                        System.out.println("Triggered action for channel " + i + ": " + channelTriggerAction.getClass().getSimpleName());
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

                    Trigger trigger = new Trigger(i, faderActive ? TriggerType.FADER_ON : TriggerType.FADER_OFF);
                    if (airliteFaderStatus.isChannelOn() && faderActive) {
                        ChannelTriggerAction channelTriggerAction = ChannelTriggerRegistry.getAction(new Trigger(i, TriggerType.FADER_AND_CHANNEL_ON));
                        if (channelTriggerAction != null) {
                            channelTriggerAction.process(airliteFaderStatus);
                            System.out.println("Triggered action for channel " + i + ": " + channelTriggerAction.getClass().getSimpleName());
                        }
                    }

                    ChannelTriggerAction channelTriggerAction = ChannelTriggerRegistry.getAction(trigger);
                    if (channelTriggerAction != null) {
                        channelTriggerAction.process(airliteFaderStatus);
                        System.out.println("Triggered action for channel " + i + ": " + channelTriggerAction.getClass().getSimpleName());
                    }
                }
            }
        }
    }
}


