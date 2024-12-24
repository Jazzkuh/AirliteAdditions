package com.jazzkuh.airliteadditions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.framework.button.ButtonTrigger;
import com.jazzkuh.airliteadditions.common.framework.button.ControlButton;
import com.jazzkuh.airliteadditions.common.framework.button.ControlLedColor;
import com.jazzkuh.airliteadditions.common.framework.trigger.TriggerAction;
import com.jazzkuh.airliteadditions.common.registry.ButtonTriggerRegistry;
import com.jazzkuh.airliteadditions.common.triggers.fader.RegularLightTrigger;
import com.jazzkuh.airliteadditions.common.udp.UDPServer;
import com.jazzkuh.airliteadditions.utils.lighting.PacketRunnable;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import com.jazzkuh.airliteadditions.utils.music.MusicEngine;
import com.jazzkuh.airliteadditions.common.web.WebServer;
import core.GLA;
import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.model.Track;
import genius.SongSearch;
import lombok.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AirliteAdditions {
    private static @Getter @Setter(AccessLevel.PRIVATE) AirliteAdditions instance;
    private static @Getter UDPServer udpServer;
    private @Getter @Setter Map<Integer, AirliteFaderStatus> faderStatuses = new HashMap<>();
    private @Getter MusicEngine musicEngine;
    private @Getter WebServer webServer;
    private @Getter @Setter Boolean shouldSkipOnStart = true;
    private @Getter @Setter Boolean partyLightEnabled = false;
    private @Getter @Setter Boolean brightLightsEnabled = false;
    private @Getter @Setter Boolean backLightEnabled = true;
    private @Getter @Setter Boolean sceneEnabled = false;
    private @Getter @Setter long micOn = -1;
    private @Getter @Setter Map<String, Double> meteringValues = new HashMap<>();
    private @Getter @Setter Boolean autoCueCrm = false;
    private @Getter @Setter Boolean autoCueAnnouncer = false;
    private @Getter @Setter Boolean cueAux = false;

    private @Getter @Setter Map<String, String> lyricsCache = new HashMap<>();

    private @Getter static String accessToken = null;
    private @Getter static long tokenExpiration = 0;

    public AirliteAdditions() {
        Map<Integer, Byte> mappedChannels = Map.of(
                1, (byte) 0x00,
                2, (byte) 0x01,
                3, (byte) 0x02,
                4, (byte) 0x03,
                5, (byte) 0x04,
                6, (byte) 0x05,
                7, (byte) 0x06,
                8, (byte) 0x07
        );

        for (int i = 1; i <= 8; i++) {
            AirliteFaderStatus airliteFaderStatus = new AirliteFaderStatus(i, (byte) 0, (byte) 1, mappedChannels.get(i));
            faderStatuses.put(i, airliteFaderStatus);
        }

        this.musicEngine = new MusicEngine(MusicEngine.MusicEngineProvider.SPOTIFY);
        this.webServer = new WebServer(8082);
        this.webServer.init();
    }

    @SneakyThrows
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

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new PacketRunnable(), 50, 50);
        timer.scheduleAtFixedRate(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                GLA gla = new GLA();

                SpotifyAPI spotifyAPI = AirliteAdditions.getInstance().getMusicEngine().getSpotifyAPI();
                Track currentTrack = spotifyAPI.getTrack();
                if (currentTrack == null) return;
                if (lyricsCache.containsKey(currentTrack.getName() + " " + currentTrack.getArtist())) return;

                LinkedList<SongSearch.Hit> hits = gla.search(currentTrack.getName() + " " + currentTrack.getArtist()).getHits();
                if (hits.isEmpty()) return;

                String lyrics = hits.getFirst().fetchLyrics();
                if (lyrics == null) return;

                lyricsCache.put(currentTrack.getName() + " " + currentTrack.getArtist(), lyrics);
            };
        }, 1000, 1000);

        SpotifyTokenManager spotifyTokenManager = new SpotifyTokenManager();
        spotifyTokenManager.run();

        timer.scheduleAtFixedRate(spotifyTokenManager, 5000, 5000);

        udpServer.start();
    }

    public static void main(String[] args) {
        setInstance(new AirliteAdditions());
        instance.start();
    }
}