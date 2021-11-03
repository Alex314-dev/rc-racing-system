package M5Project.RC.Dao;

import M5Project.RC.Resource.DBChallenge;
import M5Project.RC.model.Challenge;

import java.util.List;

public enum ChallengeDao {
    instance;

    public boolean challengeRequest(String challenger, String challengee) {
        if (DBChallenge.checkIfFriends(challenger, challengee)) {
            return !DBChallenge.alreadyInAChallenge(challenger, challengee);
        }
        return false;
    }

    public boolean respondToChallenge(int challengeID, String challengee) {
        return DBChallenge.respondToChallenge(challengee, challengeID);
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
            result = DBChallenge.updateScores(challenger, challengee, false);
        } else if (challengerTime < challengeeTime) {
            result = DBChallenge.updateScores(challengee, challenger, false);
        } else {
            result = DBChallenge.updateScores(challenger, challengee, true);
        }

        return result;
    }

    public boolean deleteChallenge(String challengee, int id) {
        return DBChallenge.deleteChallenge(challengee, id);
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
}
