package client;

import chess.ChessGame;
import chess.ChessPiece;
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
                    case "logout", "quit", "salir" -> result = "quit";
                    case "leave" -> result = "leave";
                    default -> result = help();
                }
                System.out.print(result);
            } catch (Throwable e) {
//                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED+"ERROR desconocido ha ocurrido");
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


    public String help() {
        return """
                - help
                - quit or leave (leave current game)
                """;
    }

    private void printGameBoard(ChessGame gameboard) {
        var board = gameboard.getBoard();
        ChessPiece[] row1;
        //Fix this to dynamically get the row not to make random rows.
        row1 = board.getRow(1);
        var row2 = board.getRow(2);
        var row3 = board.getRow(3);
        var row4 = board.getRow(4);
        var row5 = board.getRow(5);
        var row6 = board.getRow(6);
        var row7 = board.getRow(7);
        var row8 = board.getRow(8);
        if (colorSide.equals("white") || colorSide.equals("blanco")) {
            System.out.println(ABCD_ROW);
            System.out.println(whiteSquareFirstRow(row8, " 8 "));
            System.out.println(blackSquareFirstRow(row7, " 7 "));
            System.out.println(whiteSquareFirstRow(row6, " 6 "));
            System.out.println(blackSquareFirstRow(row5, " 5 "));
            System.out.println(whiteSquareFirstRow(row4, " 4 "));
            System.out.println(blackSquareFirstRow(row3, " 3 "));
            System.out.println(whiteSquareFirstRow(row2, " 2 "));
            System.out.println(blackSquareFirstRow(row1, " 1 "));
            System.out.println(ABCD_ROW);
        } else {
            System.out.println(HGFE_ROW);
            System.out.println(blackSquareFirstRow(row1, " 1 "));
            System.out.println(whiteSquareFirstRow(row2, " 2 "));
            System.out.println(blackSquareFirstRow(row3, " 3 "));
            System.out.println(whiteSquareFirstRow(row4, " 4 "));
            System.out.println(blackSquareFirstRow(row5, " 5 "));
            System.out.println(whiteSquareFirstRow(row6, " 6 "));
            System.out.println(blackSquareFirstRow(row7, " 7 "));
            System.out.println(whiteSquareFirstRow(row8, " 8 "));
            System.out.println(HGFE_ROW);
        }



    }

    private void printPrompt() {
        System.out.print("[En el Juego] >>> ");
    }
}
