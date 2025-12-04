package client.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.ResponseException;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ShowMovesCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification;
                    if (message.contains("NOTIFICATION")) {
                        notification = new Gson().fromJson(message, NotificationMessage.class);
                    } else if (message.contains("ERROR")) {
                        notification = new Gson().fromJson(message, ErrorMessage.class);
                    } else if (message.contains("LOAD_GAME")) {
                        notification = new Gson().fromJson(message, LoadGameMessage.class);
                    } else if (message.contains("VALID_MOVES")) {
                        notification = new Gson().fromJson(message, ValidMovesMessage.class);
                    } else {
                        notification = new Gson().fromJson(message, ServerMessage.class);
                    }
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection opened");
    }


    public void connectToGame(String authToken, Integer id) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer id) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer id, ChessMove move) throws ResponseException {
        try {
            var action = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, id, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void showMoves(String authToken, Integer id, ChessPosition position) throws ResponseException {
        try {
            var action = new ShowMovesCommand(UserGameCommand.CommandType.SHOW_MOVES, authToken, id, position);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }


//    public void redrawBoard(String authToken, Integer id) throws ResponseException {
//        try {
//
//        } catch (IOException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }


    public void resignGame(String authToken, Integer id) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }



}
