package com.jazzkuh.airliteadditions.common.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import com.jazzkuh.airliteadditions.common.udp.WebSocketHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Spark;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Getter
public class WebServer {
    public static Set<Session> sessions = new HashSet<>();
    private static Cache<String, Long> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MILLISECONDS).build();

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
    }

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));

        jsonObject.put("micOnTime", dateFormat.format(elapsed));
        jsonObject.put("onAir", AirliteAdditions.getInstance().getFaderStatuses().values().stream().anyMatch(faderStatus -> faderStatus.isChannelOn() && faderStatus.isFaderActive()));
        return jsonObject;
    }

    @SneakyThrows
    public static void broadcastMessage() {
        sessions.stream().filter(Session::isOpen).forEach(session -> {
            if (cache.getIfPresent(session.getRemoteAddress().getAddress().getHostName()) != null) return;
            cache.put(session.getRemoteAddress().getAddress().getHostName(), System.currentTimeMillis());

            try {
                session.getRemote().sendString(getJson().toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}