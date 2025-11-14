package client;


import exceptions.ResponseException;
import model.GameData;

import servicerequests.CreateGameRequest;
import servicerequests.JoinGameRequest;
import servicerequests.ListGameRequest;
import servicerequests.LogoutRequest;


import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginClient {
    private final ServerFacade server;
    private final GameplayClient gameplay;
    private String authToken = "";
    private final Map<Integer, Integer> gameIdToListNum = new HashMap<>();
    private final Map<Integer, Integer> listNumToGameId = new HashMap<>();


    public PostLoginClient(String url) {
        server = new ServerFacade(url);
        var request = new ListGameRequest("hoodoo17");
        try {
            var result = server.listGames(request);
            var list = result.games();
            //Loop through al the games to match the ids to a new list of ordered numbers
            for (var curGame : list) {
                if (gameIdToListNum.isEmpty()) {
                    gameIdToListNum.put(curGame.gameID(), 1);
                    listNumToGameId.put(1, curGame.gameID());
                } else {
                    gameIdToListNum.put(curGame.gameID(), gameIdToListNum.size() + 1);
                    listNumToGameId.put(listNumToGameId.size() + 1, curGame.gameID());
                }
            }
        } catch (ResponseException ex) {
            throw new RuntimeException("Did not set up admin auth");
        }
        gameplay = new GameplayClient(url, gameIdToListNum, listNumToGameId);


    }

    public void setAuthToken(String auth) {
        authToken = auth;
        gameplay.setAuthToken(authToken);
    }

    public void run() {
        System.out.println(RESET + SET_TEXT_COLOR_BLUE + BLACK_PAWN + " You are logged in!" + BLACK_PAWN);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout") && !result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                switch (cmd) {
                    case "logout", "quit" -> result = logout();
                    case "create" -> result = createGame(params);
                    case "list" -> result = listGames();
                    case "play" -> result = playGame(params);
                    case "observe" -> result = observeGame(params);
                    default -> result = help();
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (ResponseException ex) {
                System.out.print(SET_TEXT_COLOR_BLUE + ex.getMessage() + "\n");
            } catch (Throwable e) {

                System.out.print(SET_TEXT_COLOR_RED+ "ERROR Unknown Error has occurred");
            }
        }
        System.out.println();
    }

    private String observeGame(String[] params) throws ResponseException {

        if (params.length >0 ) {

            var gameID = Integer.parseInt(params[0]);
            if (gameID > 0) {
                gameplay.run(gameID, "WHITE");
            } else {
                return SET_TEXT_COLOR_RED + "No Games with that ID" + RESET_POST + help();
            }
        }
        else { return SET_TEXT_COLOR_RED + "Play requires gameID and playerColor \n" + RESET_POST;}

        return "";
    }

    private String playGame(String[] params) throws ResponseException {
        JoinGameRequest request;
        if (params.length >0 ) {
            var gameID = Integer.parseInt(params[0]);
            var color = params[1];
            request = new JoinGameRequest(authToken, color, listNumToGameId.get(gameID));
        }
        else { return SET_TEXT_COLOR_RED + "Play requires gameID and playerColor \n" + RESET_POST;}
        try {
            server.joinGame(request);
            gameplay.run(request.gameID(), request.playerColor());
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage() + " You cannot join this game. You can use observe to watch it\n" + RESET_POST;
        }
        return "";
    }

    private String listGames() throws ResponseException {
        var request = new ListGameRequest(authToken);
        var result = server.listGames(request);
        Collection<GameData> gameList = result.games();
        StringBuilder returnStr = new StringBuilder("Games:\n");

        for (var game : gameList) {
            var id = game.gameID();
            var listID = gameIdToListNum.get(id);
            var name = game.gameName();
            var playerWhite = game.whiteUsername();
            var playerBlack = game.blackUsername();
            returnStr.append(listID.toString())
                    .append(": Name: ")
                    .append(name)
                    .append(" WHITE: ")
                    .append(playerWhite)
                    .append(" BLACK: ")
                    .append(playerBlack)
                    .append("\n");
        }
        if (gameList.isEmpty()) {
            returnStr.append(SET_TEXT_COLOR_RED + "There are no games active. Use create to start a new one\n" + RESET_POST).append(help());
        }

        return returnStr.toString();
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length > 0) {
            var name = params[0];
            var request = new CreateGameRequest(authToken, name);
            var curResult = server.createGame(request);
            if (gameIdToListNum.isEmpty()) {
                //Update the maps every time there is a new game created
                gameIdToListNum.put(curResult.gameID(), 1);
                listNumToGameId.put(1, curResult.gameID());
            } else {
                gameIdToListNum.put(curResult.gameID(), gameIdToListNum.size() + 1);
                listNumToGameId.put(listNumToGameId.size() + 1, curResult.gameID());
                gameplay.updateMaps(gameIdToListNum, listNumToGameId);
            }

            return SET_TEXT_COLOR_DARK_GREEN + "Game Created: " + name + RESET_POST + "\n";
        }
        return  SET_TEXT_COLOR_RED + "No Game name provided\n" + RESET_POST;
    }

    private String logout() throws ResponseException {
        var request = new LogoutRequest(authToken);
        server.logout(request);
        return "logout";
    }


    public String help() {
        return """
                - logout
                - create <gameName> (Create a game without joining it)
                - list (List all games that have been created)
                - play <gameID> <playerColor> (Join specified game as the specified color)
                - observe <gameID> (Join the specified game as a spectator)
                - help
                - quit
                """;
    }

    private void printPrompt() {
        System.out.print("[Logged In] >>> ");
    }


}
