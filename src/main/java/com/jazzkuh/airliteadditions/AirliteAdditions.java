package com.jazzkuh.airliteadditions;

import com.jazzkuh.airliteadditions.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.triggers.RegularLightTrigger;
import de.jangassen.MenuToolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AirliteAdditions {
    private static @Getter @Setter(AccessLevel.PRIVATE) AirliteAdditions instance;
    private @Getter @Setter Map<Integer, AirliteFaderStatus> faderStatuses = new HashMap<>();

    public AirliteAdditions() {
        for (int i = 1; i <= 8; i++) {
            AirliteFaderStatus airliteFaderStatus = new AirliteFaderStatus(i, (byte) 0, (byte) 1);
            faderStatuses.put(i, airliteFaderStatus);
        }
    }

    public static void main(String[] args) {
        setInstance(new AirliteAdditions());

        new RegularLightTrigger().process(null);

        System.out.println("Starting AirliteAdditions");

        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        URL imageResource = AirliteAdditions.class.getClassLoader().getResource("app.png");
        java.awt.Image image = defaultToolkit.getImage(imageResource);
        Taskbar.getTaskbar().setIconImage(image);

        UDPServer udpServer = new UDPServer();
        udpServer.start();
    }
}