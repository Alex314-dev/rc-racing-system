package M5Project.RC.Dao;

import M5Project.RC.Resource.DBFriendship;
import M5Project.RC.model.Player;

import java.util.List;

public enum FriendDao {
    instance;

    public int sendFriendRequest(String sender, String friendToAdd) {
        return DBFriendship.sendFriendRequest(sender, friendToAdd);
    }

    public int acceptFriendRequest(String current, String friendToAccept) {
        return DBFriendship.respondToRequest(current, friendToAccept);
    }

    public List<String> getPendingRequests(String username) {
        return DBFriendship.getRequests(username, true);
    }

    public List<String> getSentRequests(String username) {
        return DBFriendship.getRequests(username, false);
    }

    public List<Player> getFriendsWinsLosses(String username) {
        return DBFriendship.getFriendsWinsLosses(username);
    }

    public int deleteFriend(String current, String friendToDelete) {
        return DBFriendship.deleteFriend(current, friendToDelete);
    }
}
