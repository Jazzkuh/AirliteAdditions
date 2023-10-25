package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.registry.ButtonTriggerRegistry;
import com.jazzkuh.airliteadditions.common.triggers.fader.RegularLightTrigger;
import com.jazzkuh.airliteadditions.common.utils.music.MusicEngine;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AirliteAdditions {
    private static @Getter @Setter(AccessLevel.PRIVATE) AirliteAdditions instance;
    private static @Getter UDPServer udpServer;
    private @Getter @Setter Map<Integer, AirliteFaderStatus> faderStatuses = new HashMap<>();
    private @Getter MusicEngine musicEngine;
    private @Getter @Setter Boolean shouldSkipOnStart = true;
    private @Getter @Setter Boolean partyLightEnabled = false;
    private @Getter @Setter Boolean brightLightsEnabled = false;

    public AirliteAdditions() {
        for (int i = 1; i <= 8; i++) {
            AirliteFaderStatus airliteFaderStatus = new AirliteFaderStatus(i, (byte) 0, (byte) 1);
            faderStatuses.put(i, airliteFaderStatus);
        }

        this.musicEngine = new MusicEngine(MusicEngine.MusicEngineProvider.SPOTIFY);
    }

    public void start() {
        new RegularLightTrigger().process();

        System.out.println("Starting AirliteAdditions");

        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        URL imageResource = AirliteAdditions.class.getClassLoader().getResource("app.png");
        java.awt.Image image = defaultToolkit.getImage(imageResource);
        Taskbar.getTaskbar().setIconImage(image);

        udpServer = new UDPServer();

        udpServer.writeStaticLed(ControlButton.ALL_LEDS, ControlLedColor.OFF);
        for (ButtonTrigger buttonTrigger : ButtonTriggerRegistry.getTriggers().keySet()) {
            ControlButton controlButton = buttonTrigger.getControlButton();
            udpServer.writeStaticLed(controlButton, ControlLedColor.GREEN);

            TriggerAction triggerAction = ButtonTriggerRegistry.getAction(buttonTrigger);
            if (triggerAction == null) continue;
            triggerAction.startActions();
        }

        Thread shutdownHook = new Thread(() -> udpServer.writeStaticLed(ControlButton.ALL_LEDS, ControlLedColor.OFF));
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        udpServer.start();
    }

    public static void main(String[] args) {
        setInstance(new AirliteAdditions());
        instance.start();
    }
}