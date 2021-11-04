package M5Project.RC.Test;

import M5Project.RC.Resource.DBFriendship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import M5Project.RC.Resource.DBChallenge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import M5Project.RC.model.Challenge;

public class TestDBChallenge {
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db_dev";

    static final String USER = "dab_di20212b_100";
    static final String PASS = System.getenv("RC_DB_PASS");


    @Test
    void getAllDoneChallenges(){
        List<Challenge> doneChallenges= DBChallenge.getAllDoneChallenges("KaganTheMan");
        assertEquals(2, doneChallenges.size());
        for (Challenge challenge: doneChallenges) {
            assertTrue(challenge.getChallenger().equals("KaganTheMan")
                            || challenge.getChallengee().equals("KaganTheMan"));
        }

    }

    @Test
    void getAllPendingChallenges() {
        List<Challenge> pendingChallenges =  DBChallenge.getAllChallengeRequests("LoopingLaurens", true);
        assertEquals(4, pendingChallenges.size());
    }

    @Test
    void getAllSentChallenges() {
        List<Challenge> sentChallenges =  DBChallenge.getAllChallengeRequests("LiranTheDude", false);
        assertEquals(2, sentChallenges.size());
    }

    @Test
    void checkIfFriendsTrue() {
        assertTrue(DBChallenge.checkIfFriends("KaganTheMan", "AlexP"));
    }

    @Test
    void checkIfFriendsFalse() {
        assertFalse(DBChallenge.checkIfFriends("kristian58", "AlexP"));
    }

    @Test
    void checkIfInChallengeTrue() {
        assertTrue(DBChallenge.alreadyInAChallenge("AlexP", "LoopingLaurens"));
    }

    @Test
    void checkIfInChallengeFalse() {
        assertFalse(DBChallenge.alreadyInAChallenge("AlexP", "KaganTheMan"));
    }

    @Test
    void requestChallengeExistingUser() {
        DBChallenge.startNewChallenge("AlexP", "LordDebel");
        DBChallenge.startNewChallenge("KaganTheMan", "kristian58");
        List<Challenge> challenges = DBChallenge.getAllChallengeRequests("kristian58", true);
        assertEquals(1, challenges.size());
    }

    @Test
    void requestChallengeNonExistingUser() {
        DBChallenge.startNewChallenge("KrisCross", "TestUser");
        List<Challenge> challenges = DBChallenge.getAllChallengeRequests("KrisCross", false);
        assertEquals(0, challenges.size());
    }

    @Test
    void respondExistingChallenge() {
        List<Challenge> pendingChallenges = DBChallenge.getAllChallengeRequests("kristian58", true);
        int id = 0;
        for (Challenge challenge: pendingChallenges) {
            id = challenge.getChallengeID();
        }

        DBChallenge.respondToChallenge("kristian58", id);
        List<Challenge> doneChallenges = DBChallenge.getAllDoneChallenges("kristian58");
        assertEquals(1, doneChallenges.size());
    }

    /**
     * Test responding to a challenge that this user is not part of
     */
    @Test
    void respondNonExistingChallengee() {
        //tying to alter LiranTheDude's challenge to LoopingLaurens
        DBChallenge.respondToChallenge("kristian58", 31);
        List<Challenge> challenges = DBChallenge.getAllDoneChallenges("LiranTheDude");
        assertEquals(2, challenges.size()); //The number was 2 and should be 2 after the attempted forgery.
    }

    @Test
    void deleteChallengeCorrectChallengee(){
        List<Challenge> pendingChallenges = DBChallenge.getAllChallengeRequests("LordDebel", true);
        int id = 0;
        for (Challenge challenge: pendingChallenges) {
            id = challenge.getChallengeID();
        }
        DBChallenge.deleteChallenge("LordDebel", id);

        //Should be 0, because no other pending requests
        assertEquals(0, DBChallenge.getAllChallengeRequests("LordDebel", true).size());

    }

    @Test
    void deleteChallengeInCorrectChallengee(){
        //Existing challenge with incorrect challengee
        DBChallenge.deleteChallenge("LordDebel", 30);
        assertEquals(4, DBChallenge.getAllChallengeRequests("LoopingLaurens", true).size());
    }
}

