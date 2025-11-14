package client;

import chess.ChessGame;
import chess.ChessPiece;
import servicerequests.ListGameRequest;
import serviceresults.ListGameResult;

import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class GameplayClient {

    private final ServerFacade server;
    private String authToken;
    private Map<Integer, Integer> gameIdToListNum;
    private Map<Integer, Integer> listNumToGameId;
    private String colorSide;

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
        colorSide = color.toLowerCase();

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
//                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                switch (cmd) {
                    case "logout", "quit" -> result = "quit";
                    case "leave" -> result = "leave";
                    default -> result = help();
                }
                System.out.print(result);
            } catch (Throwable e) {
//                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED+"ERROR Unknown Error has occurred");
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
                - quit or leave (leave current game)
                """;
    }

    private void printGameBoard(ChessGame gameboard) {
        var board = gameboard.getBoard();
        printBoard(board, colorSide);

    }

    private void printPrompt() {
        System.out.print("[GameMode] >>> ");
    }
}
