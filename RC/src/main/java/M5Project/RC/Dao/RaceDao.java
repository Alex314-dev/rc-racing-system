package M5Project.RC.Dao;

import M5Project.RC.JavaClientSocket.ClientSocket;
import M5Project.RC.Resource.DBRacePlayer;
import M5Project.RC.model.ErrorMessage;
import M5Project.RC.model.Race;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public enum RaceDao {

    instance;

    //if null get all races
    public List<Race> getRaces(String username) {
        List<Race> races = new ArrayList<>();

        races = DBRacePlayer.getRacesByUser(username);

        return races;
    }

    public void addRaceToDB(String username, Float raceTime, List<Float> sectorTimes) {
        DBRacePlayer.addNewRace(username, raceTime, sectorTimes);
    }


    public float initiateARace(Principal principal) {
        if (ClientSocket.instance.isOngoingGame()) {
            return ErrorMessage.ONGOING_RACE;
        }

        ClientSocket.instance.setOngoingGame(true);
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();

        String result = "";
        try {
            result = ClientSocket.instance.startRace();
        } catch (IOException | NullPointerException e) {
            ClientSocket.instance.setOngoingGame(false);
            e.printStackTrace();
            return ErrorMessage.SERVER_ERROR;
        }

        if (result.contains("Invalid")) {
            ClientSocket.instance.setOngoingGame(false);
            return ErrorMessage.INVALID_RACE;
        }

        String[] resultStrArr = result.split("~");
        List<Float> times = new ArrayList<Float>();
        for (String r : resultStrArr) {
            times.add(Float.parseFloat(r));
        }
        float overallTime = times.get(times.size() - 1);
        times.remove(times.size() - 1);

        RaceDao.instance.addRaceToDB(username, overallTime, times);
        ClientSocket.instance.setOngoingGame(false);
        return overallTime;
    }
}
