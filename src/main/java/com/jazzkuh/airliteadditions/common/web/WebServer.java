package com.jazzkuh.airliteadditions.common.web;

import com.github.pireba.applescript.AppleScript;
import com.github.pireba.applescript.AppleScriptObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.SpotifyTokenManager;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.udp.WebSocketHandler;
import com.jazzkuh.airliteadditions.utils.lighting.PhilipsWizLightController;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.Bulb;
import com.jazzkuh.airliteadditions.utils.lighting.bulb.BulbRegistry;
import core.GLA;
import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.model.Track;
import genius.SongSearch;
import lombok.Getter;
import lombok.SneakyThrows;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Spark;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class WebServer {
    public static ExecutorService EXECUTORS = Executors.newFixedThreadPool(1);
    public static Set<Session> sessions = new HashSet<>();
    private static final Cache<String, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MILLISECONDS).build();
    public static int spotifyVolume = 100;
    public static Cache<String, Integer> volumeCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public WebServer(Integer port) {
        Spark.port(port);
    }

    public void init() {
        this.initAPI();
    }

    private void initAPI() {
        try {
            Spark.webSocket("/ws", WebSocketHandler.class);
            Spark.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Spark.options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }
                    return "OK";
                });

        Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        Spark.get("/status", (request, response) -> {
            response.type("application/json");
            return getJson().toJSONString();
        });

        Spark.get("/lights/:light/state/:state", (request, response) -> {
            String light = request.params(":light");
            Bulb bulb = BulbRegistry.getBulbByName(light);
            if (bulb == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", false);
                jsonObject.put("message", "Light not found");
                return jsonObject.toJSONString();
            }

            String state = request.params(":state");
            if (state.equalsIgnoreCase("on")) {
                PhilipsWizLightController.setState(bulb, true);
            } else if (state.equalsIgnoreCase("off")) {
                PhilipsWizLightController.setState(bulb, false);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("message", state);
            return jsonObject.toJSONString();
        });

        Spark.get("/lights/:light/dimming/:brightness", (request, response) -> {
            String light = request.params(":light");
            Bulb bulb = BulbRegistry.getBulbByName(light);
            if (bulb == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", false);
                jsonObject.put("message", "Light not found");
                return jsonObject.toJSONString();
            }

            int brightness = Integer.parseInt(request.params(":brightness"));
            PhilipsWizLightController.setBrightness(bulb, brightness);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("message", brightness);
            return jsonObject.toJSONString();
        });

        Spark.get("/write/:channel/:state", (request, response) -> {
            int channel = Integer.parseInt(request.params(":channel"));
            AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(channel);
            if (airliteFaderStatus == null) return "Channel not found";

            if (request.params(":state").equalsIgnoreCase("toggle")) {
                AirliteAdditions.getUdpServer().writeRemoteOn(airliteFaderStatus, !airliteFaderStatus.isChannelOn());
                return "OK";
            }

            boolean state = Boolean.parseBoolean(request.params(":state"));
            AirliteAdditions.getUdpServer().writeRemoteOn(airliteFaderStatus, state);
            return "OK";
        });

        Spark.get("/write/resetpfl", (request, response) -> {
            AirliteAdditions.getUdpServer().write((byte) 0x02, (byte) 0x07);
            return "OK";
        });

        Spark.get("/write/pflautoann", (request, response) -> {
            AirliteAdditions.getUdpServer().write((byte) 0x03, (byte) 0x08, (byte) 0x02);
            return "OK";
        });

        Spark.get("/write/pflautocrm", (request, response) -> {
            AirliteAdditions.getUdpServer().write((byte) 0x03, (byte) 0x09, (byte) 0x02);
            return "OK";
        });

        Spark.get("/write/pflaux", (request, response) -> {
            AirliteAdditions.getUdpServer().write((byte) 0x04, (byte) 0x06, (byte) 0x08, (byte) 0x02);
            return "OK";
        });

        Spark.get("/pfl/:channel", (request, response) -> {
            int channel = Integer.parseInt(request.params(":channel"));
            AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(channel);
            if (airliteFaderStatus == null) return "Channel not found";
            AirliteAdditions.getUdpServer().write((byte) 0x04, (byte) 0x06, airliteFaderStatus.getModule(), (byte) 0x02);
            return "OK";
        });

        Spark.get("/spotify/volume/:volume", (request, response) -> {
            System.out.println("Setting volume to " + request.params(":volume"));
            int volume = Integer.parseInt(request.params(":volume"));
            String[] commands = {
                    "tell application \"Spotify\" to set sound volume to " + volume,
            };

            AppleScript appleScript = new AppleScript(commands);
            appleScript.execute();
            return appleScript.execute();
        });

        Spark.get("/spotify/play", (request, response) -> {
            AirliteAdditions.getInstance().getMusicEngine().playPause();
            return "OK";
        });

        Spark.get("/spotify/next", (request, response) -> {
            AirliteAdditions.getInstance().getMusicEngine().next();
            return "OK";
        });

        Spark.get("/spotify/previous", (request, response) -> {
            AirliteAdditions.getInstance().getMusicEngine().previous();
            return "OK";
        });

        Spark.post("/lyrics", (request, response) -> {
            JSONObject body = (JSONObject) new JSONParser().parse(request.body());
            String query = body.get("query").toString();

            GLA gla = new GLA();
            LinkedList<SongSearch.Hit> hits = gla.search(query).getHits();
            if (hits.isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", false);
                jsonObject.put("message", "No lyrics found.");
                return jsonObject.toJSONString();
            }

            String lyrics = hits.getFirst().fetchLyrics();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("message", lyrics);
            return jsonObject.toJSONString();
        });
    }

    @SneakyThrows
    @SuppressWarnings("all")
    private static JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);

        JSONArray faderStatuses = new JSONArray();
        for (int i = 1; i <= 8; i++) {
            JSONObject faderStatus = new JSONObject();
            AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(i);
            faderStatus.put("channelId", airliteFaderStatus.getChannelId());
            faderStatus.put("faderActive", airliteFaderStatus.isFaderActive());
            faderStatus.put("channelOn", airliteFaderStatus.isChannelOn());
            faderStatus.put("cueActive", airliteFaderStatus.isCueActive());
            faderStatuses.add(faderStatus);
        }
        jsonObject.put("faderStatuses", faderStatuses);

        JSONObject meteringValues = new JSONObject();
        for (String key : AirliteAdditions.getInstance().getMeteringValues().keySet()) {
            double value = AirliteAdditions.getInstance().getMeteringValues().get(key);
            meteringValues.put(key, value > 55 ? 55 : value);
        }
        jsonObject.put("meteringValues", meteringValues);

        jsonObject.put("micOn", AirliteAdditions.getInstance().getMicOn() != -1);
        long elapsedMillis = System.currentTimeMillis() - AirliteAdditions.getInstance().getMicOn();
        Date elapsed = new Date(elapsedMillis);
        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        jsonObject.put("time", dateFormat.format(new Date()));

        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
        jsonObject.put("micOnSince", AirliteAdditions.getInstance().getMicOn());

        jsonObject.put("micOnTime", dateFormat.format(elapsed));
        jsonObject.put("onAir", AirliteAdditions.getInstance().getFaderStatuses().values().stream().anyMatch(faderStatus -> faderStatus.isChannelOn() && faderStatus.isFaderActive()));
        jsonObject.put("cueEnabled", AirliteAdditions.getInstance().getFaderStatuses().values().stream().anyMatch(AirliteFaderStatus::isCueActive) || AirliteAdditions.getInstance().getCueAux());
        jsonObject.put("autoCueCRM", AirliteAdditions.getInstance().getAutoCueCrm());
        jsonObject.put("autoCueANN", AirliteAdditions.getInstance().getAutoCueAnnouncer());
        jsonObject.put("cueAux", AirliteAdditions.getInstance().getCueAux());

        JSONObject spotify = new JSONObject();
        if (volumeCache.asMap().isEmpty()) {
            String[] commands = {
                    "tell application \"Spotify\" to get sound volume",
            };

            AppleScript appleScript = new AppleScript(commands);
            AppleScriptObject result = appleScript.executeAsObject();

            spotifyVolume = Integer.parseInt(result.toString());
            volumeCache.put("volume", spotifyVolume);
            spotify.put("volume", spotifyVolume);
        } else {
            spotify.put("volume", spotifyVolume);
        }

        SpotifyAPI spotifyAPI = AirliteAdditions.getInstance().getMusicEngine().getSpotifyAPI();
        Track currentTrack = spotifyAPI.getTrack();
        if (currentTrack != null) {
            spotify.put("track", currentTrack.getName());
            spotify.put("artist", currentTrack.getArtist());
            spotify.put("trackId", currentTrack.getId());
            spotify.put("length", currentTrack.getLength());

            String lyrics = AirliteAdditions.getInstance().getLyricsCache().getOrDefault(currentTrack.getName() + " " + currentTrack.getArtist(), null);
            if (lyrics != null) {
                spotify.put("lyrics", lyrics);
            }
        }

        if (spotifyAPI.hasPosition()) {
            spotify.put("position", spotifyAPI.getPosition());
        }
        if (SpotifyTokenManager.getCachedToken() != null) {
            spotify.put("token", SpotifyTokenManager.getCachedToken());
        }

        spotify.put("playing", AirliteAdditions.getInstance().getMusicEngine().isPlaying());
        jsonObject.put("spotify", spotify);

        JSONArray lights = new JSONArray();

        for (Bulb bulb : BulbRegistry.getBulbsByGroup("studio")) {
            JSONObject bulbData = new JSONObject();
            bulbData.put("name", bulb.getName());
            bulbData.put("ip", bulb.getIp());
            bulbData.put("groups", String.join(", ", bulb.getGroups()));
            lights.add(bulbData);
        }

        jsonObject.put("lights", lights);
        return jsonObject;
    }

    @SneakyThrows
    public static void broadcastMessage() {
        EXECUTORS.submit(() -> sessions.stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(getJson().toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}