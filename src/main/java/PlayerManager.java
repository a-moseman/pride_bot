import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Hashtable;

public class PlayerManager {
    private final String PATH = "pride_bot.players.json";
    private Hashtable<String, Player> players;

    public PlayerManager() {
        this.players = new Hashtable<>();
    }

    public void load() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(PATH));
            for (Object keyObject : jsonObject.keySet()) {
                String key = (String) keyObject;
                Player player = convertJsonToPlayer((JSONObject) jsonObject.get(key));
                players.put(key, player);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            JSONObject json = new JSONObject();
            for (String key : players.keySet()) {
                Player player = players.get(key);
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
        long deaths = (long) jsonObject.get("deaths");
        return new Player(id, name).loadData(pride, level, deaths);
    }

    private JSONObject convertPlayerToJson(Player player) {
        String id = player.getId();
        String name = player.getName();
        long pride = player.getPride();
        long level = player.getEgo();
        long deaths = player.getDeaths();
        JSONObject playerJson = new JSONObject();
        playerJson.put("id", id);
        playerJson.put("name", name);
        playerJson.put("pride", pride);
        playerJson.put("level", level);
        playerJson.put("deaths", deaths);
        return playerJson;
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
