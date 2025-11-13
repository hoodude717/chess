package client;

import chess.ChessGame;
import chess.ChessPiece;
import exceptions.ResponseException;
import servicerequests.ListGameRequest;
import serviceresults.ListGameResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameplayClient {

    private ServerFacade server;
    private String authToken;
    private Map<Integer, Integer> gameIdToListNum;
    private Map<Integer, Integer> listNumToGameId;

    public GameplayClient(String url, Map<Integer, Integer> idToList, Map<Integer, Integer> listToID) {
        server = new ServerFacade(url);
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
                    case "leave" -> result = "leave";
                    default -> result = help();
                }
                System.out.print(result);
//            } catch (ResponseException ex) {
//                System.out.print(SET_TEXT_COLOR_BLUE + ex.getMessage() + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private ChessGame getChessGame(int userGameID) {
        var realGameID = listNumToGameId.get(userGameID);
        ListGameResult list;
        try {
            var request = new ListGameRequest(authToken);
            list = server.listGames(request);
        } catch (ResponseException ex) {
            System.out.println(SET_TEXT_COLOR_RED + "ERROR: Failed to get board from game" + RESET_GAME + "\n");
        }
        return null;
    }


    public String help() {
        return """
                - help
                - quit or leave (leave current game)
                """;
    }

    private void printGameBoard(ChessGame gameboard) {
//        var board = gameboard.getBoard();
        ChessPiece[] row1;
        //Fix this to dynamically get the row not to make random rows.
        row1 = new ChessPiece[8];
        for (int i =0; i<8; i++) {
            if (i%2 == 0) {
                row1[i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
            } else {
                row1[i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
            }

        }
        System.out.println(ABCD_ROW);
        System.out.println(row1WhiteSide(row1));
        System.out.println(HGFE_ROW);


    }

    private void printPrompt() {
        System.out.print("[GameMode] >>> ");
    }
}
