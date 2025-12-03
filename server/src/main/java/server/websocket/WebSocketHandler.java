package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
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
            String mess = ctx.message();
            JsonObject json = JsonParser.parseString(mess).getAsJsonObject();
            String commandType = json.get("commandType").getAsString();
            UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(commandType);
            UserGameCommand command;
            if (type.equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                command = new Gson().fromJson(mess, MakeMoveCommand.class);
            }else {
                command = new Gson().fromJson(mess, UserGameCommand.class);
            }
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
        GameData game;
        try {
            GameDAO gameDAO = new SQLGameDAO();
            AuthDAO authDAO = new SQLAuthDAO();
            var data = authDAO.getAuth(auth);
            username = data.username();
            game = gameDAO.getGame(command.getGameID());
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
        connections.singleBroadcast(session, new LoadGameMessage(game.game()));
        connections.broadcast(session, notification, command.getGameID());
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        connections.remove(session);
        //Leave Action
        var auth = command.getAuthToken();
        var username = "";
        try {
            GameDAO gameDAO = new SQLGameDAO();
            AuthDAO authDAO = new SQLAuthDAO();
            var game = gameDAO.getGame(command.getGameID());
            username = authDAO.getAuth(auth).username();
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            //Figure out the color of the player leaving
            if (game.whiteUsername().equals(username)) {
                whiteUser = null;
            } else if (game.blackUsername().equals(username)) {
                blackUser = null;
            }
            //Update the game
            var newGame = new GameData(command.getGameID(), whiteUser,blackUser, game.gameName(), game.game());
            gameDAO.updateGame(command.getGameID(), newGame);

        } catch (Exception e) {
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        //Notify
        var notifStr = String.format("Player %s has left the game", username);
        var notification = new NotificationMessage(notifStr);
        connections.broadcast(session, notification, command.getGameID());
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        //Get the ChessGame datatype from the DAO
        GameDAO gameDAO;
        int gameID = command.getGameID();
        ChessMove move = ((MakeMoveCommand) command).getMove();
        ChessGame game;
        try {
            gameDAO = new SQLGameDAO();
            var gameData = gameDAO.getGame(gameID);
            game = gameData.game();
            //Make the move
            game.makeMove(move);
            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID,
                            gameData.whiteUsername(),
                            gameData.blackUsername(),
                            gameData.gameName(),
                            game));
        } catch (InvalidMoveException ex) {
            throw new IOException("Error: Invalid Move" + ex.getMessage());
        } catch (Exception e) {
            throw new IOException("Error getting Game or Auth from DAOS");
        }


        var loadGameMessage = new LoadGameMessage(game);

        connections.broadcast(null, loadGameMessage, gameID);
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