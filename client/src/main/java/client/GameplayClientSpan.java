package client;

import chess.ChessGame;

import servicerequests.ListGameRequest;
import serviceresults.ListGameResult;

import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayClientSpan {

    private final ServerFacade server;
    private String authToken;
    private Map<Integer, Integer> gameIdToListNum;
    private Map<Integer, Integer> listNumToGameId;
    private String colorSide;

    public GameplayClientSpan(String url, Map<Integer, Integer> idToList, Map<Integer, Integer> listToID) {
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
        System.out.println(RESET_GAME + BLACK_PAWN + " Estás en juego #"+ gameID + "!" + BLACK_PAWN);
        var curGame = getChessGameSpan(gameID);
        colorSide = color.toLowerCase();

        printGameBoard(curGame);
        System.out.print(ayudar());

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
                    case "logout", "quit", "salir" -> result = "quit";
                    case "leave" -> result = "leave";
                    default -> result = ayudar();
                }
                System.out.print(result);
            } catch (Throwable e) {
//                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED+"ERROR desconocido ha ocurrido");
            }
        }
        System.out.println();
    }

    private ChessGame getChessGameSpan(int userGameID) {
        var realGameID = listNumToGameId.get(userGameID);
        ListGameResult list;
        try {
            var request = new ListGameRequest(authToken);
            list = server.listGames(request);
            for (var game : list.games()) {
                if (game.gameID() == realGameID) {
                    return game.game();
                }
            }
            throw new Exception();
        } catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + "ERROR: No se pudo conseguir tabla de juego" + RESET_GAME + "\n");
        }
        return new ChessGame();
    }


    public String ayudar() {
        return """
                - ayudar
                - salir (salir del juego)
                """;
    }

    private void printGameBoard(ChessGame gameboard) {

        var board = gameboard.getBoard();
        printBoard(board, colorSide, null);
    }

    private void printPrompt() {
        System.out.print("[En el Juego] >>> ");
    }
}
