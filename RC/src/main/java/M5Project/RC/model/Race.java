package M5Project.RC.model;

import ch.qos.logback.core.net.SyslogOutputStream;

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
    private Float overallTime;
    private List<Float> sectorTime;

    public Race() {
    }

    public Race(int raceID, String username, Timestamp date, float overallTime, List<Float> sectorTime) {
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

    public float getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(float overallTime) {
        this.overallTime = overallTime;
    }

    public List<Float> getSectorTime() {
        return sectorTime;
    }

    public void setSectorTime(List<Float> sectorTime) {
        this.sectorTime = sectorTime;
    }

    @Override
    public String toString() {
        String toString = "Race{" + "Player: " + this.username + " Id=" + this.raceID + ", date='" + this.date + ", time='" + this.overallTime + '\'' + '}';
        for (int i = 0; i < 3; i++) {
            toString += " Sector: " + (i+1) + ", result: " + getSectorTime().get(i);
        }
        return toString;

    }

}
