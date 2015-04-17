package com.citrix;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chatrooms/{roomName}")
public class Server {

    private static final Map<String, Collection<Session>> rooms = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam("roomName") String roomName, Session session) {
        Collection<Session> sessions = rooms.get(roomName);
        if(sessions == null) {
            sessions = new ArrayList<>();
            rooms.put(roomName, sessions);
        }

        sessions.add(session);
    }

    @OnClose
    public void onClose(@PathParam("roomName") String roomName, Session session) {
        Collection<Session> sessions = rooms.get(roomName);
        sessions.remove(session);

        if(sessions.isEmpty()) {
            rooms.remove(roomName);
        }
    }

    @OnMessage
    public void onMessage(@PathParam("roomName") String roomName, String message) throws IOException {
        Collection<Session> sessions = rooms.get(roomName);
        for(Session target : sessions) {
            target.getBasicRemote().sendText(message);
        }
    }

}
