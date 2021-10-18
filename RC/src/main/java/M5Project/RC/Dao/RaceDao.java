package M5Project.RC.Dao;

import M5Project.RC.Resource.Database;
import M5Project.RC.model.Race;

import java.util.ArrayList;
import java.util.List;

public enum RaceDao {

    instance;

    public List<Race> getRaces(String username) {
        List<Race> races = new ArrayList<>();

        races = Database.getRacesByUser(username);

        return races;
    }





}
