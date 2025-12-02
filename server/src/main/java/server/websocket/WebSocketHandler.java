package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import exceptions.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case LEAVE -> leave(command, ctx.session);
                case MAKE_MOVE -> makeMove(command, ctx.session);
                case RESIGN -> resign(command, ctx.session);
                default -> connections.broadcast(null,
                        new ErrorMessage("Error: Invalid Command Type"), command.getGameID());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        // Connect
        connections.add(session, command.getGameID());
        var auth = command.getAuthToken();
        var username = "";
        var color = "";
        try {
            GameDAO gameDAO = new SQLGameDAO();
            AuthDAO authDAO = new SQLAuthDAO();
            var data = authDAO.getAuth(auth);
            username = data.username();
            var game = gameDAO.getGame(command.getGameID());
            if (game.whiteUsername().equals(username)) {
                color = "white";
            } else if (game.blackUsername().equals(username)) {
                color = "black";
            } else {
                color = "an observer";
            }
        } catch (Exception e) {
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        var notifStr = String.format("Player %s has joined the game as %s", username, color);

        //Notify
        var notification = new NotificationMessage(notifStr);
        connections.broadcast(session, notification, command.getGameID());
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        //Leave Action


        //Notify
        var notification = new NotificationMessage("Player Has Left the Game\n");
        connections.broadcast(session, notification, command.getGameID());
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        //Make the move

    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.RESIGN)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }

        //Resign

        //Notify
        var notification = new NotificationMessage("Player Has Resigned from the Game\n");
        connections.broadcast(session, notification, command.getGameID());
    }

}