package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Challenge {
    private int challengeID;
    private Boolean isFinished;
    private String challenger;
    private String challengee;
    private Integer raceIDUser1;
    private Integer raceIDUser2;

    public Challenge() {
        ;
    }

    public Challenge(int challengeID, Boolean isFinished, String challenger, String challengee, Integer raceIDUser1, Integer raceIDUser2) {
        this.challengeID = challengeID;
        this.isFinished = isFinished;
        this.challenger = challenger;
        this.challengee = challengee;
        this.raceIDUser1 = raceIDUser1;
        this.raceIDUser2 = raceIDUser2;
    }


    @Id
    public int getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(int challengeID) {
        this.challengeID = challengeID;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public String getChallenger() {
        return challenger;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public String getChallengee() {
        return challengee;
    }

    public void setChallengee(String challengee) {
        this.challengee = challengee;
    }

    public Integer getRaceIDUser1() {
        return raceIDUser1;
    }

    public void setRaceIDUser1(Integer raceIDUser1) {
        this.raceIDUser1 = raceIDUser1;
    }

    public Integer getRaceIDUser2() {
        return raceIDUser2;
    }

    public void setRaceIDUser2(Integer raceIDUser2) {
        this.raceIDUser2 = raceIDUser2;
    }

    @Override
    public String toString() {
        String toString = "Challenge{" + "id=" + this.challengeID + ", player1:"  + this.challenger + " , player2: " + this.challengee + ", raceID User1:" + this.raceIDUser1 + ", raceID User2:" + this.raceIDUser2 + '\'';
        return toString + "}";
    }

}
