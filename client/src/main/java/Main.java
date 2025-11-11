import chess.*;
import client.PreLoginClient;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        var client = new PreLoginClient(serverUrl);

        client.run();;
    }
}