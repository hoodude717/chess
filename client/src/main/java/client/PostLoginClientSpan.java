package client;

import exceptions.ResponseException;
import model.GameData;
import servicerequests.CreateGameRequest;
import servicerequests.JoinGameRequest;
import servicerequests.ListGameRequest;
import servicerequests.LogoutRequest;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginClientSpan {
    private final ServerFacade server;
    private final GameplayClientSpan gameplay;
    private String authToken = "";
    private final Map<Integer, Integer> gameIdToListNum = new HashMap<>();
    private final Map<Integer, Integer> listNumToGameId = new HashMap<>();


    public PostLoginClientSpan(String url) {
        server = new ServerFacade(url);
        var request = new ListGameRequest("hoodoo17");
        try {
            var result = server.listGames(request);

            var list = result.games();
            for (var game : list) {
                if (gameIdToListNum.isEmpty()) {
                    gameIdToListNum.put(game.gameID(), 1);
                    listNumToGameId.put(1, game.gameID());
                } else {
                    gameIdToListNum.put(game.gameID(), gameIdToListNum.size() + 1);
                    listNumToGameId.put(listNumToGameId.size() + 1, game.gameID());
                }
            }
        } catch (ResponseException ex) {
            throw new RuntimeException("No puso usuario de admin");
        }
        gameplay = new GameplayClientSpan(url, gameIdToListNum, listNumToGameId);


    }

    public void setAuthToken(String auth) {
        authToken = auth;
        gameplay.setAuthToken(authToken);
    }

    public void run() {
        System.out.println(RESET + SET_TEXT_COLOR_BLUE + BLACK_PAWN + " Inició Sesión!" + BLACK_PAWN);
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
                    case "logout", "quit", "salir" -> result = logout();
                    case "create", "crear" -> result = crearJuego(params);
                    case "list", "listar" -> result = listGames();
                    case "play", "jugar" -> result = playGame(params);
                    case "observe", "mirar" -> result = observeGame(params);
                    default -> result = help();
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (ResponseException ex) {
                System.out.print(SET_TEXT_COLOR_BLUE + ex.getMessage() + "\n");
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED+ "Error desconocido ha ocurrido");
            }
        }
        System.out.println();
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length >0 ) {
            var gameID = Integer.parseInt(params[0]);
            if (gameID > 0) {
                gameplay.run(gameID, "BLANCO");
            } else {
                return SET_TEXT_COLOR_RED + "No Hay Juegos con ese ID\n" + RESET_POST + help();
            }
        }
        else { return SET_TEXT_COLOR_RED + "Mirar requiere ID de Juego\n" + RESET_POST;}

        return "";
    }

    private String playGame(String[] params) throws ResponseException {
        JoinGameRequest request;
        if (params.length >0 ) {
            var gameID = Integer.parseInt(params[0]);
            var color = params[1];
            request = new JoinGameRequest(authToken, color, listNumToGameId.get(gameID));
        }
        else { return SET_TEXT_COLOR_RED + "Jugar requiere ID de Juego y Color (blanco o negro)\n" + RESET_POST;}
        try {
            server.joinGame(request);
            gameplay.run(request.gameID(), request.playerColor());
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage() + " No puedes jugar en este juego. Puedes mirarlo con mirar <ID>\n" + RESET_POST;
        }
        return "";
    }

    private String listGames() throws ResponseException {
        var request = new ListGameRequest(authToken);
        var result = server.listGames(request);
        Collection<GameData> gameList = result.games();
        StringBuilder returnStr = new StringBuilder("Juegos:\n");

        for (var game : gameList) {
            var iD = game.gameID();
            var listID = gameIdToListNum.get(iD);
            var name = game.gameName();
            var playerWhite = game.whiteUsername();
            var playerBlack = game.blackUsername();
            returnStr.append(listID.toString())
                    .append(": Nombre: ")
                    .append(name)
                    .append(" BLANCO: ")
                    .append(playerWhite)
                    .append(" NEGRO: ")
                    .append(playerBlack)
                    .append("\n");
        }
        if (gameList.isEmpty()) {
            returnStr.append(SET_TEXT_COLOR_RED + "No hay juegos activos. Usa crear para crear uno nuevo\n" + RESET_POST).append(help());
        }

        return returnStr.toString();
    }

    private String crearJuego(String[] params) throws ResponseException {
        if (params.length > 0) {
            var name = params[0];
            var request = new CreateGameRequest(authToken, name);
            var result = server.createGame(request);
            if (gameIdToListNum.isEmpty()) {
                gameIdToListNum.put(result.gameID(), 1);
                listNumToGameId.put(1, result.gameID());
            } else {
                gameIdToListNum.put(result.gameID(), gameIdToListNum.size() + 1);
                listNumToGameId.put(listNumToGameId.size() + 1, result.gameID());
                gameplay.updateMaps(gameIdToListNum, listNumToGameId);
            }

            return SET_TEXT_COLOR_DARK_GREEN + "Juego Creado: " + name + RESET_POST + "\n";
        }
        return  SET_TEXT_COLOR_RED + "Ningún nombre incluido\n" + RESET_POST;
    }

    private String logout() throws ResponseException {
        var request = new LogoutRequest(authToken);
        server.logout(request);
        return "quit";
    }


    public String help() {
        return """
                - salir (cerrar sesión)
                - crear <nombreDeJuego> (Crear un juego sin jugar)
                - listar (listar todos los juegos que han sido creados)
                - jugar <juegoID> <BLANCO | NEGRO> (Jugar en juego especifcado con color especificado)
                - observe <juegoID> (mirar especifico juego como espectador)
                - ayuda (mostrar información)
                """;
    }

    private void printPrompt() {
        System.out.print("[Sesión Iniciada] >>> ");
    }


}
