package M5Project.RC.model;

import M5Project.RC.Resource.DBChallenge;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Challenge {
    private int challengeID;
    private Boolean isFinished;
    private String challenger;
    private String challengee;
    private int raceIDChallenger;
    private float challengerTime;
    private int raceIDChallengee;
    private float challengeeTime;

    public Challenge() {
        ;
    }

    public Challenge(int challengeID, Boolean isFinished, String challenger, String challengee, int raceIDChallenger, int raceIDChallengee) {
        this.challengeID = challengeID;
        this.isFinished = isFinished;
        this.challenger = challenger;
        this.challengee = challengee;
        this.raceIDChallenger = raceIDChallenger;
        this.challengerTime = DBChallenge.raceTimeFromRaceId(raceIDChallenger);
        this.raceIDChallengee = raceIDChallengee;
        this.challengeeTime = DBChallenge.raceTimeFromRaceId(raceIDChallengee);
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

    public int getRaceIDChallenger() {
        return raceIDChallenger;
    }

    public void setRaceIDChallenger(int raceIDChallenger) {
        this.raceIDChallenger = raceIDChallenger;
    }

    public int getRaceIDChallengee() {
        return raceIDChallengee;
    }

    public void setRaceIDChallengee(int raceIDChallengee) {
        this.raceIDChallengee = raceIDChallengee;
    }

    public float getChallengerTime() {
        return this.challengerTime;
    }

    public void setChallengerTime(float challengerTime) {
        this.challengerTime = challengerTime;
    }

    public float getChallengeeTime() {
        return challengeeTime;
    }

    public void setChallengeeTime(float challengeeTime) {
        this.challengeeTime = challengeeTime;
    }

    @Override
    public String toString() {
        String toString = "Challenge{" + "id=" + this.challengeID + ", player1:"  + this.challenger + ", raceID Challenger:" + this.raceIDChallenger + ", raceID Challengee:" + this.raceIDChallengee + '\'';
        return toString + "}";
    }

}
