package com.jazzkuh.airliteadditions.common.web;

import com.jazzkuh.airliteadditions.AirliteAdditions;
import com.jazzkuh.airliteadditions.common.framework.AirliteFaderStatus;
import org.json.simple.JSONObject;
import spark.Spark;

public class WebServer {

    public WebServer(Integer port) {
        Spark.port(port);
    }

    public void init() {
        this.initAPI();
    }

    private void initAPI() {
        Spark.get("/status", (request, response) -> {
            response.type("application/json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);

            JSONObject faderStatuses = new JSONObject();
            for (int i = 1; i <= 8; i++) {
                JSONObject faderStatus = new JSONObject();
                AirliteFaderStatus airliteFaderStatus = AirliteAdditions.getInstance().getFaderStatuses().get(i);
                faderStatus.put("channelId", airliteFaderStatus.getChannelId());
                faderStatus.put("faderActive", airliteFaderStatus.isFaderActive());
                faderStatus.put("channelOn", airliteFaderStatus.isChannelOn());
                faderStatuses.put(i, faderStatus);
            }
            jsonObject.put("faderStatuses", faderStatuses);

            return jsonObject.toJSONString();
        });
    }
}