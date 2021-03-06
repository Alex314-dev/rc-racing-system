package M5Project.RC.Dao;

import M5Project.RC.Resource.DBChallenge;
import M5Project.RC.model.Challenge;

import java.util.List;

public enum ChallengeDao {
    instance;

    public boolean challengeRequest(String challenger, String challengee) {
        return DBChallenge.checkIfFriends(challenger, challengee) && !DBChallenge.alreadyInAChallenge(challenger, challengee);
    }

    public boolean respondToChallenge(int challengeID, String challengee) {
        return DBChallenge.respondToChallenge(challengee, challengeID);
    }

    public boolean startNewChallenge(String challenger, String challengee) {
        return DBChallenge.startNewChallenge(challenger, challengee);
    }

    public boolean changeScoresInvalidRace(int id, String challenger, String challengee, boolean challengerWins) {
        if (challengerWins) {
            DBChallenge.forceCompleteChallenge(id);
            return DBChallenge.updateScores(challenger, challengee, false);
        } else {
            return DBChallenge.updateScores(challengee, challenger, false);
        }
    }

    public boolean changeScores(String challengee) {
        List<Challenge> doneChallenges = DBChallenge.getAllDoneChallenges(challengee);
        if (doneChallenges == null || doneChallenges.size() == 0) {
            return false;
        }
        Challenge mostRecent = doneChallenges.get(0);

        String challenger = mostRecent.getChallenger();
        int challengerRaceID = mostRecent.getRaceIDChallenger();
        int challengeeRaceID = mostRecent.getRaceIDChallengee();
        float challengerTime = DBChallenge.raceTimeFromRaceId(challengerRaceID);
        float challengeeTime = DBChallenge.raceTimeFromRaceId(challengeeRaceID);

        boolean result;
        if (challengerTime > challengeeTime) {
            result = DBChallenge.updateScores(challengee, challenger, false);
        } else if (challengerTime < challengeeTime) {
            result = DBChallenge.updateScores(challenger, challengee, false);
        } else {
            result = DBChallenge.updateScores(challenger, challengee, true);
        }

        return result;
    }

    public List<Challenge> getPendingChallengeRequests(String username) {
        return DBChallenge.getAllChallengeRequests(username, true);
    }

    public List<Challenge> getSentChallengeRequests(String username) {
        return DBChallenge.getAllChallengeRequests(username, false);
    }

    public boolean checkIfChallengeExists(String challenger, String challengee, int id) {
        return DBChallenge.getChallengeFromId(challenger, challengee, id);
    }

    public List<Challenge> getDoneChallenges(String username) {
        return DBChallenge.getAllDoneChallenges(username);
    }
}
