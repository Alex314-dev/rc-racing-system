package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Challenge {
    private int challengeID;
    private String username1;
    private String username2;
    private Timestamp date;
    private Float overallTimeUser1;
    private List<Float> sectorTimeUser1;
    private Float overallTimeUser2;
    private List<Float> sectorTimeUser2;

    public Challenge() {
        ;
    }

    public Challenge(int challengeID, String username1, String username2, Timestamp date, float overallTimeUser1, List<Float> sectorTimeUser1, float overallTimeUser2, List<Float> sectorTimeUser2) {
        this.challengeID = challengeID;
        this.username1 = username1;
        this.username2 = username2;
        this.date = date;
        this.overallTimeUser1 = overallTimeUser1;
        this.sectorTimeUser1 = sectorTimeUser1;
        this.overallTimeUser2 = overallTimeUser2;
        this.sectorTimeUser2 = sectorTimeUser2;
    }

    @Id
    public int getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(int challengeID) {
        this.challengeID = challengeID;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Float getOverallTimeUser1() {
        return overallTimeUser1;
    }

    public void setOverallTimeUser1(Float overallTimeUser1) {
        this.overallTimeUser1 = overallTimeUser1;
    }

    public List<Float> getSectorTimeUser1() {
        return sectorTimeUser1;
    }

    public void setSectorTimeUser1(List<Float> sectorTimeUser1) {
        this.sectorTimeUser1 = sectorTimeUser1;
    }

    public Float getOverallTimeUser2() {
        return overallTimeUser2;
    }

    public void setOverallTimeUser2(Float overallTimeUser2) {
        this.overallTimeUser2 = overallTimeUser2;
    }

    public List<Float> getSectorTimeUser2() {
        return sectorTimeUser2;
    }

    public void setSectorTimeUser2(List<Float> sectorTimeUser2) {
        this.sectorTimeUser2 = sectorTimeUser2;
    }

    @Override
    public String toString() {
        String toString = "Challenge{" + "id=" + this.challengeID + ", date='" + this.date + " player1:"  + this.username1 + ", time:" + this.overallTimeUser1 + '\'';
        for (int i = 0; i < 3; i++) {
            toString += ", sector=" + (i+1) + ", result=" + getSectorTimeUser1().get(i);
        }

        toString += "player2:"  + this.username2 + ", time:" + this.overallTimeUser2 + '\'';
        for (int i = 0; i < 3; i++) {
            toString += ", sector=" + (i+1) + ", result=" + getSectorTimeUser2().get(i);
        }
        return toString + "}";
    }

}
