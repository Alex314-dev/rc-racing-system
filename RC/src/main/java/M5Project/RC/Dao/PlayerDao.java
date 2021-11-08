package M5Project.RC.Dao;

import M5Project.RC.Resource.DBFriendship;
import M5Project.RC.Resource.DBRacePlayer;
import M5Project.RC.model.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PlayerDao {

    instance;

    private Map<String, Player> players = new HashMap<String, Player>();


    public void addPlayerToMap(String sub, Player player) {
        String email = player.getEmail();
        players.put(sub, player);
    }

    public boolean addPlayerToDB(Player player) throws SQLException, ClassNotFoundException {
        return DBRacePlayer.insertNewPlayer(player);
    }

    public Player getPlayer(String sub) {
        return players.get(sub);
    }

    public String getUsernameByEmail(String email) {
        return DBRacePlayer.getPlayerUsername(email);
    }

    public boolean isPlayerRegistered(String username) {
        return DBRacePlayer.isPlayerRegistered(username);
    }

    public List<String> getAllPlayers() {
        List<String> playersUsernames = DBRacePlayer.getAllUsernames();
        return playersUsernames;
    }

    public List<Player> getFriendsOfUser(String sub) {
        String username = this.players.get(sub).getUsername();
        List<Player> friends = DBFriendship.getFriendsWinsLosses(username);
        return friends;
    }

    public int removeAccount(String player) {
        return Database.removePlayer(player);
    }
}