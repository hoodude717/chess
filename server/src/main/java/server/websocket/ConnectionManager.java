package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer id) {
        connections.put(session, id);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void singleBroadcast(Session session, ServerMessage notification) throws IOException {
        session.getRemote().sendString(new Gson().toJson(notification));
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException {
        var gson = new Gson();
        String msg = gson.toJson(notification);
        for (var s: connections.keySet()) {
            if (!connections.get(s).equals(gameID)) {
                return;
            }
            if (s.isOpen()) {
                if (!s.equals(excludeSession)) {
                    s.getRemote().sendString(msg);
                }
            }
        }
    }

}