package M5Project.RC.Dao;

import M5Project.RC.Resource.Database;
import M5Project.RC.model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PlayerDao {

    instance;

    private Map<String, Player> players = new HashMap<String, Player>();

    public void addPlayer(String sub, Player player) {
        String email = player.getEmail();
        players.put(sub, player);

        Database.addPlayer(player);
    }
    
    public Player getPlayer(String sub) {
        return players.get(sub);
    }

    public List<String> getAllPlayers() {
        List<String> playersUsernames = Database.getAllPlayers();
        return playersUsernames;
    }

    public List<Player> getFriendsOfUser(String sub) {
        String username = this.players.get(sub).getUsername();
        List<Player> friends = Database.getFriendsOfUser(username);
        return friends;
    }

}