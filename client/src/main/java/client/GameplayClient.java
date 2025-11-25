package client;

import chess.ChessGame;
import chess.ChessPiece;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exceptions.ResponseException;
import servicerequests.JoinGameRequest;
import servicerequests.ListGameRequest;
import serviceresults.ListGameResult;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class GameplayClient implements NotificationHandler {

    private final ServerFacade server;
    private final String url;
    private WebSocketFacade ws;
    private String authToken;
    private Map<Integer, Integer> gameIdToListNum;
    private Map<Integer, Integer> listNumToGameId;
    private String colorSide;

    public GameplayClient(String url, Map<Integer, Integer> idToList, Map<Integer, Integer> listToID)
            throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        gameIdToListNum = idToList;
        listNumToGameId = listToID;
    }

    public void updateMaps(Map<Integer, Integer> newIDMap, Map<Integer, Integer> newListMap) {
        gameIdToListNum = newIDMap;
        listNumToGameId = newListMap;
    }

    public void setAuthToken(String auth) {
        authToken = auth;
    }

    public void run(int gameID, String color) {
        System.out.println(RESET_GAME + BLACK_PAWN + " You are in game "+ gameID + "!" + BLACK_PAWN);
        var curGame = getChessGame(gameID);
        colorSide = color.toLowerCase();
        if (colorSide.equals("empty")) { colorSide = "white"; }

        try{
            if (ws==null) {
                ws = new WebSocketFacade(url, this);
            }
            ws.connectToGame(authToken, gameID);
        } catch (ResponseException ex) {
            System.out.println("Failed to connect to Game");
            return;
        }


        printGameBoard(curGame);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout") && !result.equals("quit") && !result.equals("leave")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                switch (cmd) {
                    case "logout", "quit" -> result = "quit";
                    case "redraw" -> {
                        redraw(gameID);
                        result = "";
                    }
                    case "move" -> result = makeMove(params);
                    case "leave" -> result = leaveGame(gameID);
                    default -> result = help();
                }
                System.out.print(result);
            } catch (Throwable e) {
//                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED+"ERROR Unknown Error has occurred\n" + RESET_GAME);
            }
        }
        System.out.println();
    }

    private ChessGame getChessGame(int userGameID) {
        var realGameID = listNumToGameId.get(userGameID);
        ListGameResult list;
        try {
            //Create the request to get the list
            var request = new ListGameRequest(authToken);
            list = server.listGames(request);
            for (var curGame : list.games()) {
                var id = curGame.gameID();
                //Check the games to see the match
                if (id == realGameID) {
                    return curGame.game();
                }
            }
            throw new Exception();
        } catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + "ERROR: Failed to get board from game" + RESET_GAME + "\n");
        }
        return new ChessGame();
    }


    public String help() {
        return """
                - help
                - redraw (redraws the game board)
                - quit or leave (leave current game)
                """;
    }

    private void printGameBoard(ChessGame gameboard) {
        var board = gameboard.getBoard();
        printBoard(board, colorSide);

    }

    private void redraw(int gameID) {
        var curGame = getChessGame(gameID);
        printGameBoard(curGame);
    }

    private String leaveGame(int gameID) {
        var gameWOPlayer = new JoinGameRequest(authToken, colorSide, gameID);
        // possibly create a new service side thing that adds null to a game where the person was before.

        return "";
    }

    private String makeMove(String[] params) {

        return "";

    }

    private void printPrompt() {
        System.out.print("[GameMode] >>> ");
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + "Print Stuff");
        printPrompt();

    }
}
