package com.jazzkuh.airliteadditions.common.udp;


import com.jazzkuh.airliteadditions.common.web.WebServer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketHandler {
    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        WebServer.sessions.add(session);
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        WebServer.sessions.remove(session);
        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress());
    }
}