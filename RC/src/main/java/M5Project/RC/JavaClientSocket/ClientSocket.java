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
    private boolean challenge = false;
    private String racer = "";
    private String challengee = "";

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    private ClientSocket() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Input the IP address of the RPI: ");
            String rpiIP = input.nextLine();
            startConn(rpiIP, 8890);
        } catch (IOException e) {
            System.out.println("Couldn't connect to the Raspberry Pi.");
            e.printStackTrace();
        }
    }

    public void startConn(String ip, int port) throws IOException {
        client = new Socket(ip, port);
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public String startRace() throws IOException {
        out.println("GO");
        System.out.println("[=>] Starting the race.");
        String resp = in.readLine();
        return resp;
    }

    public void closeConn() throws IOException {
        in.close();
        out.close();
        client.close();
    }

    public boolean isOngoingGame() {
        return ongoingGame;
    }

    public void setOngoingGame(boolean ongoingGame) {
        this.ongoingGame = ongoingGame;
    }

    public boolean isChallenge() {
        return challenge;
    }

    public void setChallenge(boolean challenge) {
        this.challenge = challenge;
    }

    public String getRacer() {
        return racer;
    }

    public void setRacer(String racer) {
        this.racer = racer;
    }

    public String getChallengee() {
        return challengee;
    }

    public void setChallengee(String challengee) {
        this.challengee = challengee;
    }
}
