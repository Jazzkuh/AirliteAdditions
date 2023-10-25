package com.jazzkuh.airliteadditions.common.framework.trigger;

import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.URL;

public abstract class TriggerAction implements TriggerActionImpl {
    public abstract void process();

    public void startActions() {
    }


    @SneakyThrows
    public final void sendRequest(String apiUrl) {
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage()); // THis is optional

        connection.disconnect();
    }
}
