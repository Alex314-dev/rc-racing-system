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
    private String username;
    private Timestamp date;
    private Time overallTime;
    private List<Time> sectorTime;

    public Race() {
    }

    public Race(int raceID, String username, Timestamp date, Time overallTime, List<Time> sectorTime) {
        this.raceID = raceID;
        this.username = username;
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

    public String getPlayer() {
        return username;
    }

    public void setPlayer(String username) {
        this.username = username;
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
        return "Race{" + "Id=" + this.raceID + ", date='" + this.date + ", time='" + this.overallTime + '\'' + '}';
    }

}
