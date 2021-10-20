package M5Project.RC.Dao;

import M5Project.RC.model.Player;

import java.util.HashMap;
import java.util.Map;

public enum PlayerDao {

    instance;

    private Map<String, Player> players = new HashMap<String, Player>();

    public void addPlayer(String sub, Player player) {
        players.put(sub, player);
    }

    public Player getPlayer(String sub) {
        return players.get(sub);
    }

    public Map<String, Player> getPlayers() {
        return players;
    }


}
