package client;

import exceptions.ResponseException;
import servicerequests.LoginRequest;
import servicerequests.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClientSpan {
    private final ServerFacade server;
    private final PostLoginClientSpan post;


    public PreLoginClientSpan(String url) {
        server = new ServerFacade(url);
        post = new PostLoginClientSpan(url);
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
                    case "login", "iniciar" -> result = login(params);
                    case "register", "registrar" -> result = registrar(params);
                    case "quit", "salir", "clear" -> result = "quit";
                    default -> result = help();
                }
                System.out.print(SET_TEXT_COLOR_GREEN + result + "\n");
            } catch (ResponseException ex) {
                System.out.print(SET_TEXT_COLOR_RED + ex.getMessage() + "\n");
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED + "ERROR Un error desconocido ha occurrido");
            }
        }
        System.out.println();
    }


    public String help() {
        return """
                - registrar <usuario> <contraseña> <correo> (crear un usuario nuevo)
                - iniciar <usuario> <contraseña> (entrar en la aplicación con usuario preexistente)
                - ayuda (muestra las opciones)
                - salir (terminar la aplicación)
                """;
    }



    private String login(String[] params) throws ResponseException {
        if (params.length >= 2) {
            var user = params[0];
            var pass = params[1];
            var request = new LoginRequest(user, pass);

            var result = server.login(request);
            post.setAuthToken(result.authToken());
            System.out.print(SET_TEXT_COLOR_BLUE + "Sesión Iniciada\n");
            post.run();
            return "\n";
        } else  {
            return SET_TEXT_COLOR_RED;
        }
    }

    private String registrar(String[] params) throws ResponseException {
        if (params.length >= 3) {
            var user = params[0];
            var pass = params[1];
            var email = params[2];
            var request = new RegisterRequest(user, pass, email);
            var result = server.register(request);
            post.setAuthToken(result.authToken());
            post.run();
            return "Registrado\n";
        } else  {
            return "Registrar requiere Usuario, Contraseña y Correo\n";
        }
    }

    private void printPrompt() {
        System.out.print(RESET + "[Sessión No Iniciada] >>> " );
    }
}
