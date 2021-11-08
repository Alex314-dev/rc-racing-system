package M5Project.RC.JavaClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public enum ClientSocket {
    instance;

    private boolean ongoingGame = false;

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    ClientSocket() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Input the IP address and the port of the RPI: ");
            String[] rpiIP = input.nextLine().split(" ");
            startConn(rpiIP[0], Integer.parseInt(rpiIP[1]));
        } catch (Exception e) {
            System.out.println("Couldn't connect to the Raspberry Pi.");
            e.printStackTrace();
        }
    }

    public void startConn(String ip, int port) throws IOException {
        client = new Socket(ip, port);
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public String startRace() throws IOException, NullPointerException {
        out.println("GO");
        System.out.println("[=>] Starting the race.");
        String resp = in.readLine();
        return resp;
    }

    public boolean isOngoingGame() {
        return ongoingGame;
    }

    public void setOngoingGame(boolean ongoingGame) {
        this.ongoingGame = ongoingGame;
    }
}
