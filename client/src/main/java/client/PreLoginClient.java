package client;

import exceptions.ResponseException;
import servicerequests.LoginRequest;
import servicerequests.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClient {
    private final ServerFacade server;
    private PostLoginClient post;


    public PreLoginClient(String url) {
        server = new ServerFacade(url);
        post = new PostLoginClient(url);
    }

    public void run() {
        System.out.println(RESET+ BLACK_PAWN + " En Puissant on the Croissant" + BLACK_PAWN);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                switch (cmd) {
                    case "login" -> result = login(params);
                    case "register" -> result = register(params);
                    case "quit" -> result = "quit";
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


    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - help
                - quit
                """;
    }

    private String login(String[] params) throws ResponseException {
        if (params.length >= 2) {
            var user = params[0];
            var pass = params[1];
            var request = new LoginRequest(user, pass);

            var result = server.login(request);
            post.setAuthToken(result.authToken());
            System.out.print(SET_TEXT_COLOR_BLUE + "Login Successful\n");
            post.run();
            return "";

        } else  {
            return "Login requires Username and Password\n";
        }
    }

    private String register(String[] params) throws ResponseException {
        if (params.length >= 3) {
            var user = params[0];
            var pass = params[1];
            var email = params[2];
            var request = new RegisterRequest(user, pass, email);
            var result = server.register(request);
            post.setAuthToken(result.authToken());
            post.run();
            return "Register Successful\n";
        } else  {
            return "Register requires Username, Password and Email\n";
        }
    }

    private void printPrompt() {
        System.out.print(RESET + "[Logged Out] >>> " );
    }
}
