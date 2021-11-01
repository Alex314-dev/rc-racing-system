package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Challenge {
    private int challengeID;
    private Boolean isFinished;
    private Boolean isAccepted;
    private String challenger;
    private String challengee;
    private Integer raceIDChallenger;
    private Integer raceIDChallengee;

    public Challenge() {
        ;
    }

    public Challenge(int challengeID, Boolean isFinished, Boolean isAccepted, String challenger, String challengee, Integer raceIDChallenger, Integer raceIDChallengee) {
        this.challengeID = challengeID;
        this.isFinished = false;
        this.isAccepted = isAccepted;
        this.challenger = challenger;
        this.challengee = challengee;
        this.raceIDChallenger = raceIDChallenger;
        this.raceIDChallengee = raceIDChallengee;
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

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
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

    public Integer getRaceIDChallenger() {
        return raceIDChallenger;
    }

    public void setRaceIDChallenger(Integer raceIDChallenger) {
        this.raceIDChallenger = raceIDChallenger;
    }

    public Integer getRaceIDChallengee() {
        return raceIDChallengee;
    }

    public void setRaceIDChallengee(Integer raceIDChallengee) {
        this.raceIDChallengee = raceIDChallengee;
    }

    @Override
    public String toString() {
        String toString = "Challenge{" + "id=" + this.challengeID + ", player1:"  + this.challenger + ", raceID Challenger:" + this.raceIDChallenger + ", raceID Challengee:" + this.raceIDChallengee + '\'';
        return toString + "}";
    }

}
