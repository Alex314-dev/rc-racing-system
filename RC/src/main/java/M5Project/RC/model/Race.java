package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
public class Race {
    private int raceID;
    private Player player;
    private Timestamp date;
    private Time overallTime;
    private List<Time> sectorTime;

    public Race() {
    }

    public Race(int raceID, Player player, Timestamp date, Time overallTime, List<Time> sectorTime) {
        this.raceID = raceID;
        this.player = player;
        this.date = date;
        this.overallTime = overallTime;
        this.sectorTime = sectorTime;
    }

    @Id
    public int getRaceID() {
        return raceID;
    }

    public void setRaceID(int raceID) {
        this.raceID = raceID;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Time getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(Time overallTime) {
        this.overallTime = overallTime;
    }

    public List<Time> getSectorTime() {
        return sectorTime;
    }

    public void setSectorTime(List<Time> sectorTime) {
        this.sectorTime = sectorTime;
    }

    @Override
    public String toString() {
        return "Race{" + "Id=" + this.raceID + ", date='" + this.date + '\'' + '}';
    }

}
