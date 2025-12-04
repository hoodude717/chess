package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exceptions.ResponseException;
import servicerequests.ListGameRequest;
import serviceresults.ListGameResult;
import websocket.messages.*;

import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class GameplayClient implements NotificationHandler {

    private final ServerFacade server;
    private final String url;
    private WebSocketFacade ws;
    private String authToken;
    private Map<Integer, Integer> listNumToGameId;
    private String colorSide;

    public GameplayClient(String url, Map<Integer, Integer> idToList, Map<Integer, Integer> listToID)
            throws ResponseException {
        server = new ServerFacade(url);
        this.url = url;
        listNumToGameId = listToID;
    }

    public void updateMaps(Map<Integer, Integer> newIDMap, Map<Integer, Integer> newListMap) {
        listNumToGameId = newListMap;
    }

    public void setAuthToken(String auth) {
        authToken = auth;
    }

    public void run(int gameID, String color) {
        System.out.println(RESET_GAME + BLACK_PAWN + " You are in game "+ gameID + "!" + BLACK_PAWN);
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


//        System.out.print(printGameBoard(curGame));
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
                    case "logout" -> result = "quit";
                    case "redraw" -> {
                        result = redraw(gameID);
                    }
                    case "move" -> result = makeMove(params, gameID);
                    case "show_moves" -> result = showMoves(params, gameID);
                    case "leave", "quit" -> result = leaveGame(gameID);
                    case "resign" -> resignGame(gameID);
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
                - move <from> <to> (make a move from and to format is A3 or F1)
                - redraw (redraws the game board)
                - resign (quit the game and the opponent wins)
                - show_moves <piece> (highlight possible moves for specified piece)
                - help
                - quit or leave (leave current game)
                """;
    }

    private String printGameBoard(ChessGame gameboard) {
        var board = gameboard.getBoard();
        var turn = gameboard.getTeamTurn();
        String turnStr = turn.equals(ChessGame.TeamColor.WHITE) ? "White's Turn\n" : "Black's Turn\n";
        return printBoard(board, colorSide, null) + turnStr;

    }

    private String printValidMoves(Collection<ChessMove> moves, ChessGame gameboard) {
        var board = gameboard.getBoard();
        return printBoard(board, colorSide, moves);
    }

    private String redraw(int gameID) {
        var curGame = getChessGame(gameID);
        return printGameBoard(curGame);
    }

    private String leaveGame(int gameID) throws ResponseException {
        ws.leaveGame(authToken, gameID);
        return "leave";
    }

    private void resignGame(int gameID) throws ResponseException {
        ws.resignGame(authToken, gameID);
    }
    private String showMoves(String[] params, Integer gameID) throws ResponseException {

        String piece;
        if (params.length > 0) {
            piece = params[0];
        } else {
            return "Please include Piece location";
        }
        piece = piece.toUpperCase();
        Map<Character, Integer> colMap;
        if (colorSide.equals("white")){
            colMap = Map.of(
                    'A', 1, 'B', 2, 'C', 3, 'D', 4,
                    'E', 5, 'F', 6, 'G', 7, 'H', 8);
        } else {
            colMap = Map.of(
                    'A', 8, 'B', 7, 'C', 6, 'D', 5,
                    'E', 4, 'F', 3, 'G', 2, 'H', 1);
        }
        int col = colMap.get(piece.charAt(0));
        int row = Integer.parseInt(piece.substring(1));
        var pos = new ChessPosition(row, col);

        ws.showMoves(authToken, gameID, pos);

        return "Showing Possible Moves";
    }

    private String makeMove(String[] params, Integer gameID) throws ResponseException{
        //Reading the parameters
        String from, to, promotion;
        if (params.length >= 2) {
            from = params[0];
            to = params[1];
            if (params.length == 3) {
                promotion = params[2];
            } else {
                promotion = "";
            }
        } else {
            return "Invalid Move Format";
        }

        //Getting promotion type
        ChessPiece.PieceType pieceType;
        switch (promotion.toLowerCase()) {
            case "rook" -> pieceType = ChessPiece.PieceType.ROOK;
            case "knight" -> pieceType = ChessPiece.PieceType.KNIGHT;
            case "bishop" -> pieceType = ChessPiece.PieceType.BISHOP;
            case "queen" -> pieceType = ChessPiece.PieceType.QUEEN;
            default -> pieceType = null;
        }
        //Extracting rows and columns from the string
        from = from.toUpperCase();
        to = to.toUpperCase();
        Map<Character, Integer> colMap;
        if (colorSide.equals("white")){
            colMap = Map.of(
                    'A', 1, 'B', 2, 'C', 3, 'D', 4,
                    'E', 5, 'F', 6, 'G', 7, 'H', 8);
        } else {
            colMap = Map.of(
                    'A', 8, 'B', 7, 'C', 6, 'D', 5,
                    'E', 4, 'F', 3, 'G', 2, 'H', 1);
        }
        int fromCol = colMap.get(from.charAt(0));
        int fromRow = Integer.parseInt(from.substring(1));
        int toCol = colMap.get(to.charAt(0));
        int toRow = Integer.parseInt(to.substring(1));
        var startPosition = new ChessPosition(fromRow, fromCol);
        var endPosition = new ChessPosition(toRow, toCol);
        var curMove = new ChessMove(startPosition, endPosition, pieceType);

        ws.makeMove(authToken, gameID, curMove);

        return "";

    }

    private void printPrompt() {
        System.out.print("[GameMode] >>> ");
    }

    @Override
    public void notify(ServerMessage notification) {
        String msg;
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            msg = ((NotificationMessage) notification).getMessage();
        } else if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
            msg = ((ErrorMessage) notification).getMessage();
        } else if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
            msg = printGameBoard(((LoadGameMessage) notification).getGame());
        } else if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.VALID_MOVES)) {
            msg = printValidMoves(((ValidMovesMessage) notification).getValidMoves(),
                    ((ValidMovesMessage) notification).getGame());
        } else {
            msg = "Unknown Message";
        }
        System.out.println(SET_TEXT_COLOR_RED + msg + RESET_GAME);
        printPrompt();

    }
}
