package com.jazzkuh.airliteadditions;

import lombok.Getter;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TimerTask;

public class SpotifyTokenManager extends TimerTask {
    @Getter
    private static String cachedToken = null;

    private static long tokenExpiration = 0;
    private static final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void run() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiration) return;
        generateToken();
    }

    @SneakyThrows
    private String generateToken() {
        String cookie = readFileFromResources("cookie.txt");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://open.spotify.com/get_access_token?reason=transport&productType=web_player"))
                .header("Cookie", cookie)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.body());
        String accessToken = jsonObject.get("accessToken").toString();
        long accessTokenExpirationTimestampMs = Long.parseLong(jsonObject.get("accessTokenExpirationTimestampMs").toString());

        cachedToken = accessToken;
        tokenExpiration = accessTokenExpirationTimestampMs;

        System.out.println("Generated new Spotify access token: " + accessToken);
        return accessToken;
    }

    private static String readFileFromResources(String fileName) throws Exception {
        InputStream inputStream = AirliteAdditions.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new Exception("File not found: " + fileName);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContents = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            fileContents.append(line).append(System.lineSeparator());
        }

        reader.close();
        return fileContents.toString().trim();
    }
}