package client;

import exceptions.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginClient {
    private ServerFacade server;
    private GameplayClient gameplay;
    private String authToken = "";

    public PostLoginClient(String url) {
        server = new ServerFacade(url);
        gameplay = new GameplayClient();
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
        return "";
    }

    private String createGame(String[] params) throws ResponseException {
        return "";
    }

    private String logout(String[] params) throws ResponseException {
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
