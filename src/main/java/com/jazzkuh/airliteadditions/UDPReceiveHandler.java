package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.channel.ChannelTrigger;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerType;
import com.jazzkuh.airliteadditions.common.registry.ButtonTriggerRegistry;
import com.jazzkuh.airliteadditions.common.registry.ChannelTriggerRegistry;

import java.util.logging.Logger;

public class UDPReceiveHandler {
    private static final Logger LOGGER = Logger.getLogger(UDPReceiveHandler.class.getName());

    public static void process(byte[] data) {
        try {
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
                            LOGGER.info("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                        }
                    }

                    TriggerAction triggerAction = ChannelTriggerRegistry.getAction(channelTrigger);
                    if (triggerAction != null) {
                        triggerAction.process();
                        LOGGER.info("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
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
                            LOGGER.info("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                        }
                    }

                    TriggerAction triggerAction = ChannelTriggerRegistry.getAction(channelTrigger);
                    if (triggerAction != null) {
                        triggerAction.process();
                        LOGGER.info("Triggered action for channel " + i + ": " + triggerAction.getClass().getSimpleName());
                    }
                }
            }

            if (data[3] == -60) {
                int buttonId = data[10];
                int pressedValueA = data[4];
                int pressedValueB = data[5];

                ControlButton controlButton = ControlButton.getButton(buttonId);
                if (controlButton == null) return;

                boolean pressed = controlButton.getRow().equals("A") && pressedValueA == controlButton.getPressedValue() || controlButton.getRow().equals("B") && pressedValueB == controlButton.getPressedValue();
                ButtonTrigger buttonTrigger = new ButtonTrigger(controlButton, pressed ? TriggerType.BUTTON_PRESSED : TriggerType.BUTTON_RELEASED);
                TriggerAction triggerAction = ButtonTriggerRegistry.getAction(buttonTrigger);

                if (controlButton.isHasPressedColor() && ButtonTriggerRegistry.isKnownButton(controlButton)) {
                    if (pressed) AirliteAdditions.getUdpServer().writeStaticLed(controlButton, ControlLedColor.RED);
                    else AirliteAdditions.getUdpServer().writeStaticLed(controlButton, ControlLedColor.GREEN);
                }

                if (triggerAction != null) {
                    triggerAction.process();
                    LOGGER.info("Triggered action for button " + controlButton + ": " + triggerAction.getClass().getSimpleName());
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error while receiving data: " + e.getMessage());
        }
    }
}
