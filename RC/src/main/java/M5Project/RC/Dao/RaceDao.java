package M5Project.RC.Dao;

import M5Project.RC.Resource.DBRacePlayer;
import M5Project.RC.model.Race;

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




}
