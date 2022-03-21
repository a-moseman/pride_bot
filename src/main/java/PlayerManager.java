import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

public class PlayerManager {
    private final String PATH = "pride_bot.players.json";
    private final Hashtable<String, Player> PLAYERS;

    public PlayerManager() {
        this.PLAYERS = new Hashtable<>();
    }

    public void load() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(PATH));
            for (Object keyObject : jsonObject.keySet()) {
                String key = (String) keyObject;
                Player player = convertJsonToPlayer((JSONObject) jsonObject.get(key));
                PLAYERS.put(key, player);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            JSONObject json = new JSONObject();
            for (String key : PLAYERS.keySet()) {
                Player player = PLAYERS.get(key);
                JSONObject playerJson = convertPlayerToJson(player);
                json.put(key, playerJson);
            }
            FileWriter fileWriter = new FileWriter(PATH);
            fileWriter.write(json.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Player convertJsonToPlayer(JSONObject jsonObject) {
        String id = (String) jsonObject.get("id");
        String name = (String) jsonObject.get("name");
        long pride = (long) jsonObject.get("pride");
        long level = (long) jsonObject.get("level");
        long shame = (long) jsonObject.get("shame");
        long ascendancy = (long) jsonObject.get("ascendancy");
        return new Player(id, name).loadData(pride, level, shame, ascendancy);
    }

    private JSONObject convertPlayerToJson(Player player) {
        String id = player.getId();
        String name = player.getName();
        long pride = player.getPride();
        long level = player.getEgo();
        long shame = player.getShame();
        long ascendancy = player.getAscendancy();
        JSONObject playerJson = new JSONObject();
        playerJson.put("id", id);
        playerJson.put("name", name);
        playerJson.put("pride", pride);
        playerJson.put("level", level);
        playerJson.put("shame", shame);
        playerJson.put("ascendancy", ascendancy);
        return playerJson;
    }

    public void addPlayer(String id, Player player) {
        PLAYERS.put(id, player);
    }

    public Player getPlayer(String id) {
        return PLAYERS.get(id);
    }

    public boolean containsPlayer(String id) {
        return getPlayer(id) != null;
    }

    public void addPride(String id, int amount) {
        PLAYERS.get(id).addPride(amount);
    }

    public void removePride(String id, int amount) {
        PLAYERS.get(id).removePride(amount);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : PLAYERS.values()) {
            players.add(player);
        }
        return players;
    }
}
