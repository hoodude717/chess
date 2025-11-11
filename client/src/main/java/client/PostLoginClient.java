package client;

import exceptions.ResponseException;
import model.GameDataSerializeable;
import servicerequests.ListGameRequest;
import servicerequests.LogoutRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginClient {
    private ServerFacade server;
    private GameplayClient gameplay;
    private String authToken = "";
    private Map<Integer, Integer> gameIdToListNum;

    public PostLoginClient(String url) {
        server = new ServerFacade(url);
        gameplay = new GameplayClient(url);
    }

    public void setAuthToken(String auth) {
        authToken = auth;
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
                    case "logout", "quit" -> result = logout(params);
                    case "create" -> result = createGame(params);
                    case "list" -> result = listGames(params);
                    case "play" -> result = playGame(params);
                    case "observe" -> result = observeGame(params);
                    default -> result = help();
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (ResponseException ex) {
                System.out.print(SET_TEXT_COLOR_BLUE + ex.getMessage());
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private String observeGame(String[] params) throws ResponseException {
        return "";
    }

    private String playGame(String[] params) throws ResponseException {
        return "";
    }

    private String listGames(String[] params) throws ResponseException {
        var request = new ListGameRequest(authToken);
        var result = server.listGames(request);
        Collection<GameDataSerializeable> gameList = result.games();
        var returnStr = "Games:\n";

        for (var game : gameList) {
            var ID = game.gameID();
            var name = game.gameName();
            var playerWhite = game.whiteUsername();
            var playerBlack = game.blackUsername();
            returnStr = returnStr + ID +": Name: " + name + " WHITE: " + playerWhite + " BLACK: " + playerBlack + "\n";
        }
        if (gameList.isEmpty()) {
            returnStr += SET_TEXT_COLOR_RED + "There are no games active. Use create to start a new one\n"
                    + RESET_POST + help();
        }

        return returnStr;
    }

    private String createGame(String[] params) throws ResponseException {
        return "";
    }

    private String logout(String[] params) throws ResponseException {
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
