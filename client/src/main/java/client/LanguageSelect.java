package client;

import exceptions.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class LanguageSelect {
    private final PreLoginClient client;
    private final PreLoginClientSpan clientSpan;

    public LanguageSelect(String url) {
        client = new PreLoginClient(url);
        clientSpan = new PreLoginClientSpan(url);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_PURPLE +
                SET_TEXT_COLOR_WHITE + "WELCOME TO EN PUISSANT ON THE CROISSANT!");
        System.out.println(RESET+ BLACK_PAWN + BLACK_PAWN + BLACK_PAWN + BLACK_PAWN + BLACK_PAWN +
                BLACK_PAWN + BLACK_PAWN + BLACK_PAWN + BLACK_PAWN+ BLACK_PAWN+ BLACK_PAWN+ BLACK_PAWN+ BLACK_PAWN);
        System.out.println("Select A Language: " + FLAG_USA + FLAG_UK +"English or Spanish" + FLAG_MEXICO + FLAG_DR);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] tokens = line.toLowerCase().split(" ");
                String cmd = (tokens.length > 0) ? tokens[0] : "help";
                String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
                switch (cmd.toLowerCase()) {
                    case "english" -> {
                        client.run();
                        result = "quit";
                    }
                    case "spanish" -> {
                        clientSpan.run();
                        result = "quit";
                    }
                    case "quit" -> result = "quit";
                    default -> result = "English or Spanish";
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + "ERROR Unknown Error has occurred");
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print(RESET + ">>> " );
    }
}
