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
        if (command.length < 1) {
            return "Invalid command.";
        }
        switch (command[0]) {
            case "add":
                if (command.length < 3) { return "Missing arguments."; }
                if (isDungeonMaster) {
                    return addPride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                else {
                    return "Command only available to dungeon masters.";
                }
            case "remove":
                if (command.length < 3) { return "Missing arguments."; }
                if (isDungeonMaster) {
                    return removePride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                else {
                    return "Command only available to dungeon masters.";
                }
            case "stats":
                if (command.length > 1) {
                    return stats(Util.getIdFromMention(command[1]));
                }
                else {
                    return stats(authId);
                }
            case "help":
                return help();
            default:
                return "Invalid command.";
        }
    }

    private String help() {
        String output = "Commands:";
        output += "\n\tp>stats <mention> - Get user stats. The <mention> is optional";
        output += "\n\tp>add <mention> <adaptive value> - (pride_dm) Add pride to the player. Adaptive value examples: 23, 1d4, 2d6, etc.";
        output += "\n\tp>remove <mention> <adaptive value> - (pride_dm) Remove from a player. See p>add for adaptive value examples.";
        output += "\n\tp>help - Get a list of commands and information on pride_bot";
        output += "\n\nInfo:";
        output += "\n\tPride can be given or taken from players by a user with a role named pride_dm exactly.";
        output += "\n\tEgo is something gained upon reaching a certain threshold amount of pride, resulting in the pride being converting into the level of ego.";
        output += "\n\tThe threshold and value of a level of ego follows the pattern:  level 2 ego = 40 pride, level 3 ego = 80 pride, level 4 ego = 160 pride, etc.";
        output += "\n\tExample of leveling up ego: a player reaches 50 pride, they level up to ego level 2 and their pride is reduced to 10.";
        output += "\n\tA level of ego is lost upon reaching a pride of 0. Your ego is reduced by 1 and pride set to this lower level of egos half-way-point of pride.";
        output += "\n\tMore features will be added later on.";
        return output;
    }

    private String stats(String id) {
        Player player = playerManager.getPlayer(id);
        return "Pride: " + player.getPride() +
                "\nEgo: " + player.getEgo() +
                "\nShame: " + player.getShame();
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
        long oldLevel = player.getEgo();
        player.addPride(amount);
        String output = player.getName() + " gained " + amount + " pride.";
        if (player.getEgo() > oldLevel) {
            output += "\nAdditionally, they leveled up their ego to level " + player.getEgo() + "!"; // TODO: add randomized level gain messages?
        }
        return output;
    }

    private String removePride(String id, int amount) {
        // TODO: optimize
        Player player = playerManager.getPlayer(id);
        long oldLevel = player.getEgo();
        long oldDeaths = player.getShame();
        player.removePride(amount);
        String output = player.getName() + " lost " + amount + " pride.";
        if (oldDeaths < player.getShame()) {
            output += "\nUnfortunately, they have died!"; // TODO: add randomized death messages
            output += "\nThankfully, they have been resurrected by the Great God Steve!";
        }
        else if (oldLevel > player.getEgo()) {
            output += "\nUnfortunately, they have lost a level of ego!"; // TODO: add randomized level loss messages?
        }
        return output;
    }
}
