import java.util.Hashtable;

public class PlayerManager {
    private Hashtable<String, Player> players;

    public PlayerManager() {
        this.players = new Hashtable<>();
    }

    public void load() {
        // TODO: implement
    }

    public void save() {
        // TODO: implement
    }

    public void addPlayer(String id, Player player) {
        players.put(id, player);
    }

    public Player getPlayer(String id) {
        return players.get(id);
    }

    public boolean containsPlayer(String id) {
        return getPlayer(id) != null;
    }

    public void addPride(String id, int amount) {
        players.get(id).addPride(amount);
    }

    public void removePride(String id, int amount) {
        players.get(id).removePride(amount);
    }
}
