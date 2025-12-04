package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Collection;

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
            } else if (type.equals(UserGameCommand.CommandType.SHOW_MOVES)) {
                command = new Gson().fromJson(mess, ShowMovesCommand.class);
            } else {
                command = new Gson().fromJson(mess, UserGameCommand.class);
            }
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case LEAVE -> leave(command, ctx.session);
                case MAKE_MOVE -> makeMove(command, ctx.session);
                case RESIGN -> resign(command, ctx.session);
                case SHOW_MOVES -> showMoves(command, ctx.session);
                default -> connections.broadcast(null,
                        new ErrorMessage("Error: Invalid Command Type"), command.getGameID());
            }
        } catch (InvalidMoveException ex) {
            var error = new ErrorMessage(ex.getMessage());
            try {
                connections.singleBroadcast(ctx.session, error);
            } catch (IOException e) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
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
            connections.singleBroadcast(session, new ErrorMessage("Error: Not Properly Logged into this Game"));
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
            if (username.equals(game.whiteUsername())) {
                whiteUser = null;
            } else if (username.equals(game.blackUsername())) {
                blackUser = null;
            }
            //Update the game
            var newGame = new GameData(command.getGameID(), whiteUser, blackUser, game.gameName(), game.game());
            gameDAO.updateGame(command.getGameID(), newGame);

        } catch (Exception e) {
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        //Notify
        var notifStr = String.format("Player %s has left the game", username);
        var notification = new NotificationMessage(notifStr);
        connections.broadcast(session, notification, command.getGameID());
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException, InvalidMoveException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }
        //Get the ChessGame datatype from the DAO
        GameDAO gameDAO;
        AuthDAO authDAO;
        int gameID = command.getGameID();
        ChessMove move = ((MakeMoveCommand) command).getMove();
        ChessGame game;
        String auth = command.getAuthToken();
        String username;
        NotificationMessage checkNotif = null;
        try {
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
            var gameData = gameDAO.getGame(gameID);
            game = gameData.game();
            username = authDAO.getAuth(auth).username();
            ChessGame.TeamColor turn;
            if (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {turn = ChessGame.TeamColor.WHITE;}
            else {turn = ChessGame.TeamColor.BLACK;}

            if (gameData.whiteUsername().equals(username)
                    && turn.equals(ChessGame.TeamColor.WHITE)) {
                game.makeMove(move);

            } else if (gameData.blackUsername().equals(username)
                    && turn.equals(ChessGame.TeamColor.BLACK)) {
                game.makeMove(move);
            } else {
                throw new InvalidMoveException("Cannot make move if not your turn or an observer");
            }
            // Check to see if game in check or checkmate
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                checkNotif = new NotificationMessage(String.format("White %s is in checkmate! Black has won", username));
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                checkNotif = new NotificationMessage(String.format("Black %s is in checkmate! White has won", username));
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                checkNotif = new NotificationMessage(String.format("White %s is in check!", gameData.whiteUsername()));
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                checkNotif = new NotificationMessage(String.format("Black %s is in check!", gameData.blackUsername()));
            }

            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID,
                            gameData.whiteUsername(),
                            gameData.blackUsername(),
                            gameData.gameName(),
                            game));
        } catch (InvalidMoveException ex) {
            throw new InvalidMoveException("Error: Invalid Move " + ex.getMessage());
        } catch (Exception e) {
            connections.singleBroadcast(session, new ErrorMessage("Error: Not properly logged into this game"));
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        String notifStr = String.format("Player %s has made the following move: %s",username, move);
        var loadGameMessage = new LoadGameMessage(game);
        var notificationMessage = new NotificationMessage(notifStr);
        connections.broadcast(session, notificationMessage, gameID);
        connections.broadcast(null, loadGameMessage, gameID);
        if (checkNotif != null) { connections.broadcast(null, checkNotif, gameID);}

    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.RESIGN)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }

        //Resign
        GameDAO gameDAO;
        AuthDAO authDAO;
        int gameID = command.getGameID();
        ChessGame game;
        String auth = command.getAuthToken();
        String username;
        try {
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
            var gameData = gameDAO.getGame(gameID);
            game = gameData.game();
            if (game.gameOver()) {
                connections.singleBroadcast(session, new ErrorMessage("Error: Cannot resign after game is over"));
                throw new IOException("Cannot resign after game is over");
            }
            username = authDAO.getAuth(auth).username();
            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();
            if (username.equals(gameData.whiteUsername())) {
                white = null;
            } else {
                black = null;
            }
            // If observer cannot resign
            if (!username.equals(gameData.blackUsername()) && !username.equals(gameData.whiteUsername()) ) {
                connections.singleBroadcast(session, new ErrorMessage("Error: Cannot resign as an observer"));
                throw new IOException("Cannot resign as an observer");
            }

            //Make the reigning action
            ChessGame.TeamColor resigningColor = (white == null) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            game.resign(resigningColor);
            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID,
                            white,
                            black,
                            gameData.gameName(),
                            game));
        } catch (IOException e) {
            throw new IOException("Error resigning from Game " + e.getMessage());
        } catch (Exception e) {
            connections.singleBroadcast(session, new ErrorMessage("Error: Not Properly Logged into this Game"));
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        //Notify
        var resignerNotif = new NotificationMessage("You have resigned from the game");
        var notification = new NotificationMessage(String.format("Player %s has resigned from the game", username));
        connections.singleBroadcast(session, resignerNotif);
        connections.broadcast(session, notification, command.getGameID());
    }

    private void showMoves(UserGameCommand command, Session session) throws IOException {
        if (!command.getCommandType().equals(UserGameCommand.CommandType.SHOW_MOVES)) {
            connections.broadcast(null,
                    new ErrorMessage("Error: Did not use the right command\n"), command.getGameID());
            return;
        }

        GameDAO gameDAO;
        ChessPosition pos = ((ShowMovesCommand) command).getPosition();
        Collection<ChessMove> valid;
        ChessGame game;
        try {
            gameDAO = new SQLGameDAO();
            var gameData = gameDAO.getGame(command.getGameID());
            game = gameData.game();
            valid = game.validMoves(pos);
        } catch (Exception e) {
            throw new IOException("Error getting Game or Auth from DAOS");
        }

        var validMovesMessage = new ValidMovesMessage(valid, game);
        connections.singleBroadcast(session, validMovesMessage);

    }

}