package M5Project.RC.Dao;

import M5Project.RC.Resource.Database;
import M5Project.RC.model.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PlayerDao {

    instance;

    private Map<String, Player> players = new HashMap<String, Player>();

    public void addPlayer(String sub, Player player) {
        String email = player.getEmail();
        players.put(sub, player);
    }

    public Player getPlayer(String sub) {
        return players.get(sub);
    }

    public String getUsernameByEmail(String email) {
        return Database.getPlayerUsername(email);
    }

    public boolean isPlayerRegistered(String username) {
        return Database.isPlayerRegistered(username);
    }

    public List<String> getAllPlayers() {
        List<String> playersUsernames = Database.getAllUsernames();
        return playersUsernames;
    }

    public List<String> getFriendsOfUser(String sub) {
        String username = this.players.get(sub).getUsername();
        List<String> friends = Database.getAllFriendsUsernames(username);
        return friends;
    }

}