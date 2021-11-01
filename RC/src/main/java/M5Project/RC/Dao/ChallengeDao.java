package M5Project.RC.Dao;

import M5Project.RC.Resource.DBChallenge;
import M5Project.RC.model.Challenge;

public enum ChallengeDao {
    instance;

    public boolean challengeRequest(String challenger, String challengee) {
        if (DBChallenge.checkIfFriends(challenger, challengee)) {
            if (!DBChallenge.isAlreadyChallenge(challenger, challengee))
                DBChallenge.startNewChallenge(challenger, challengee);
                return true;
        }
        return false;
    }

}
