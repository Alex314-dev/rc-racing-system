package M5Project.RC.Test;

import M5Project.RC.Resource.DBFriendship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import M5Project.RC.Resource.DBChallenge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import M5Project.RC.model.Challenge;
import M5Project.RC.model.Race;
import M5Project.RC.model.Player;

public class TestDBChallenge {

    @Test
    void getAllDoneChallenges() {
        List<Challenge> doneChallenges = DBChallenge.getAllDoneChallenges("KaganTheMan");
        assertEquals(2, doneChallenges.size());
        for (Challenge challenge : doneChallenges) {
            assertTrue(challenge.getChallenger().equals("KaganTheMan")
                    || challenge.getChallengee().equals("KaganTheMan"));
        }

    }

    @Test
    void getAllPendingChallenges() {
        List<Challenge> pendingChallenges = DBChallenge.getAllChallengeRequests("LoopingLaurens", true);
        assertEquals(4, pendingChallenges.size());
    }

    @Test
    void getAllSentChallenges() {
        List<Challenge> sentChallenges = DBChallenge.getAllChallengeRequests("LiranTheDude", false);
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
        for (Challenge challenge : pendingChallenges) {
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
    void deleteAllChallengesCorrectPairs() {
        DBChallenge.deleteALlChallengesUsers("LordDebel", "AlexP");
        DBChallenge.deleteALlChallengesUsers("KaganTheMan", "kristian58");

        //Should be 1, because there will be only 1 other
        assertEquals(1, DBChallenge.getAllChallengeRequests("LordDebel", true).size());
        assertEquals(0, DBChallenge.getAllDoneChallenges("kristian58").size());

    }

    @Test
    void checkForceCompleteChallenge() {
        DBChallenge.startNewChallenge("AlexP", "KrisCross");
        List<Challenge> challenges = DBChallenge.getAllChallengeRequests("KrisCross", true);
        assertEquals(1, challenges.size()); //make sure there is an ongoing challenge

        int id = 0;
        for (Challenge challenge : challenges) {
            id = challenge.getChallengeID();
        }

        DBChallenge.forceCompleteChallenge(id);

        assertEquals(1, DBChallenge.getAllDoneChallenges("KrisCross").size());

        DBChallenge.deleteALlChallengesUsers("AlexP", "KrisCross");

    }

    @Test
    void checkGetChallengeFromIDCorrectID() {
        assertTrue(DBChallenge.getChallengeFromId("kristian58", "LoopingLaurens", 28));
    }

    @Test
    void checkGetChallengeFromIDIncorrectID() {
        assertFalse(DBChallenge.getChallengeFromId("kristian58", "LoopingLaurens", 30));
    }

    @Test
    void checkUpdateScoresFriend2Winner() {
        List<Player> friends = DBFriendship.getFriendsWinsLosses("KrisCross");
        int friendWinsBeforeRace = 0;
        for (Player friend : friends) {
            if (friend.getUsername().equals("LiranTheDude")) {
                friendWinsBeforeRace = friend.getWins();
            }
        }

        DBChallenge.updateScores("LiranTheDude", "KrisCross", false);

        List<Player> friends2 = DBFriendship.getFriendsWinsLosses("KrisCross");
        int friendWinsAfterRace = 0;
        for (Player friend : friends2) {
            if (friend.getUsername().equals("LiranTheDude")) {
                friendWinsAfterRace = friend.getWins();
            }
        }

        assertEquals(friendWinsBeforeRace + 1, friendWinsAfterRace);
    }

    @Test
    void checkUpdateScoresFriend1Winner() {
        List<Player> friends = DBFriendship.getFriendsWinsLosses("LiranTheDude");
        int friendWinsBeforeRace = 0;
        for (Player friend : friends) {
            if (friend.getUsername().equals("KrisCross")) {
                friendWinsBeforeRace = friend.getWins();
            }
        }

        DBChallenge.updateScores("KrisCross", "LiranTheDude", false);

        List<Player> friends2 = DBFriendship.getFriendsWinsLosses("LiranTheDude");
        int friendWinsAfterRace = 0;
        for (Player friend : friends2) {
            if (friend.getUsername().equals("KrisCross")) {
                friendWinsAfterRace = friend.getWins();
            }
        }

        assertEquals(friendWinsBeforeRace + 1, friendWinsAfterRace);
    }

    @Test
    void checkUpdateScoresDraw() {
        List<Player> friendsLiran = DBFriendship.getFriendsWinsLosses("LiranTheDude");
        int friend1WinsBeforeRace = 0;
        for (Player friend : friendsLiran) {
            if (friend.getUsername().equals("KrisCross")) {
                friend1WinsBeforeRace = friend.getWins();
            }
        }

        List<Player> friendsKris = DBFriendship.getFriendsWinsLosses("KrisCross");
        int friend2WinsBeforeRace = 0;
        for (Player friend : friendsKris) {
            if (friend.getUsername().equals("LiranTheDude")) {
                friend2WinsBeforeRace = friend.getWins();
            }
        }

        DBChallenge.updateScores("KrisCross", "LiranTheDude", true);

        List<Player> friendsLiran1 = DBFriendship.getFriendsWinsLosses("LiranTheDude");
        int friend1WinsAfterRace = 0;
        for (Player friend : friendsLiran1) {
            if (friend.getUsername().equals("KrisCross")) {
                friend1WinsAfterRace = friend.getWins();
            }
        }

        List<Player> friendsKris1 = DBFriendship.getFriendsWinsLosses("KrisCross");
        int friend2WinsAfterRace = 0;
        for (Player friend : friendsKris1) {
            if (friend.getUsername().equals("LiranTheDude")) {
                friend2WinsAfterRace = friend.getWins();
            }
        }

        assertEquals(friend1WinsBeforeRace + 1, friend1WinsAfterRace);
        assertEquals(friend2WinsBeforeRace + 1, friend2WinsAfterRace);
    }


    @Test
    void checkRaceTimeFromId() {
        float actualTime= DBChallenge.raceTimeFromRaceId(52);
        assertEquals(9.861000061035156, actualTime);
    }

    @Test
    void checkRaceTimeFromIdNoRace() {
        float actualTime= DBChallenge.raceTimeFromRaceId(0);
        assertEquals(0, actualTime);
    }

}

