public class Bot {
    private PlayerManager playerManager;

    public Bot() {
        this.playerManager = new PlayerManager();
        this.playerManager.load();
    }

    public boolean knowsPlayer(String id) {
        return playerManager.containsPlayer(id);
    }

    public void addPlayer(String id, Player player) {
        playerManager.addPlayer(id, player);
    }

    public Player getPlayer(String id) {
        return playerManager.getPlayer(id);
    }

    public void save() {
        playerManager.save();
    }

    public String doCommand(String[] command, String authId, boolean isDungeonMaster) {
        // TODO: implement similarly to dented bot.
        switch (command[0]) {
            case "add":
                if (isDungeonMaster) {
                    return addPride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                else {
                    return "Command only available to dungeon masters.";
                }
            case "remove":
                if (isDungeonMaster) {
                    return removePride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                else {
                    return "Command only available to dungeon masters.";
                }
            case "stats":
                return stats(authId);
            default:
                return "";
        }
    }

    private String stats(String id) {
        Player player = playerManager.getPlayer(id);
        long pride = player.getPride();
        long level = player.getLevel();
        long deaths = player.getDeaths();
        return "Pride: " + pride +
                "\nLevel: " + level +
                "\nDeaths: " + deaths;
    }

    private int parseAdaptiveValue(String text) {
        int count = 0;
        String temp = "";
        boolean hadDDelimiter = false;
        for (char c : text.toCharArray()) {
            if (c == 'd' || c == 'D') {
                count = Integer.parseInt(temp);
                hadDDelimiter = true;
                temp = "";
            }
            else {
                temp += c;
            }
        }
        if (hadDDelimiter) {
            return DiceRoller.roll(Integer.parseInt(temp), count);
        }
        else {
            return count;
        }
    }

    private String addPride(String id, int amount) {
        // TODO: optimize
        Player player = playerManager.getPlayer(id);
        long oldLevel = player.getLevel();
        player.addPride(amount);
        String output = player.getName() + " gained " + amount + " pride.";
        if (player.getLevel() > oldLevel) {
            output += "\nAdditionally, they leveled up to level " + player.getLevel() + "!"; // TODO: add randomized level gain messages?
        }
        return output;
    }

    private String removePride(String id, int amount) {
        // TODO: optimize
        Player player = playerManager.getPlayer(id);
        long oldLevel = player.getLevel();
        long oldDeaths = player.getDeaths();
        player.removePride(amount);
        String output = player.getName() + " lost " + amount + " pride.";
        if (oldDeaths < player.getDeaths()) {
            output += "\nUnfortunately, they have died!"; // TODO: add randomized death messages
            output += "\nThankfully, they have been resurrected by the Great God Steve!";
        }
        else if (oldLevel > player.getLevel()) {
            output += "\nUnfortunately, they have lost a level!"; // TODO: add randomized level loss messages?
        }
        return output;
    }
}
