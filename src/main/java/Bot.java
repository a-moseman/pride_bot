import java.util.Collections;
import java.util.List;

public class Bot {
    private final PlayerManager PLAYER_MANAGER;

    public Bot() {
        this.PLAYER_MANAGER = new PlayerManager();
        this.PLAYER_MANAGER.load();
    }

    public boolean knowsPlayer(String id) {
        return PLAYER_MANAGER.containsPlayer(id);
    }

    public void addPlayer(String id, Player player) {
        PLAYER_MANAGER.addPlayer(id, player);
    }

    public Player getPlayer(String id) {
        return PLAYER_MANAGER.getPlayer(id);
    }

    public void save() {
        PLAYER_MANAGER.save();
    }

    public String doCommand(String[] command, String authId, boolean isDungeonMaster) {
        // TODO: implement similarly to dented bot.
        if (command.length < 1) {
            return "Invalid command.";
        }
        switch (command[0]) {
            case "ascend":
                if (command.length > 1) {return "Extra arguments.";}
                return ascend(authId);
            case "add":
                if (command.length < 3) {return "Missing arguments.";}
                if (command.length > 3) {return "Extra arguments.";}
                if (!isDungeonMaster) {return "Command is only available to pride_dms.";}
                if (!Util.isValidMention(command[1])) {return "Invalid user.";}
                try {
                    return addPride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                catch (Exception e) {
                    return "Invalid user.";
                }
            case "remove":
                if (command.length < 3) {return "Missing arguments.";}
                if (command.length > 3) {return "Extra arguments.";}
                if (!isDungeonMaster) {return "Command is only available to pride_dms";}
                if (!Util.isValidMention(command[1])) {return "Invalid user.";}
                try {
                    return removePride(Util.getIdFromMention(command[1]), parseAdaptiveValue(command[2]));
                }
                catch (Exception e) {
                    return "Invalid user.";
                }
            case "stats":
                if (command.length > 2) {return "Extra arguments.";}
                try {
                    return stats(command.length > 1 ? Util.getIdFromMention(command[1]) : authId);
                }
                catch (Exception e) {
                    return "Invalid user.";
                }
            case "help":
                if (command.length > 1) {return "Extra arguments.";}
                return help();
            case "leaderboard":
                if (command.length > 1) {return "Extra arguments.";}
                return leaderboard();
            default:
                return "Invalid command.";
        }
    }

    private String leaderboard() {
        List<Player> players = PLAYER_MANAGER.getPlayers();
        Collections.sort(players);
        StringBuilder output = new StringBuilder("Leaderboard:");
        for (int i = 0; i < (players.size() > 9 ? 10 : players.size()); i++) {
            Player player = players.get(i);
            // handle same rank
            if (i > 0 && players.get(i - 1).compareTo(player) == 0) {
                i--; // stop incrementation of i
            }
            output.append("\n\t").append(i + 1).append(". ").append(player.getName());
        }
        return output.toString();
    }

    private String help() {
        String output = "Commands:";
        output += "\n\tp>stats <mention> - Get user stats. The <mention> is optional";
        output += "\n\tp>add <mention> <adaptive value> - (pride_dm) Add pride to the player. Adaptive value examples: 23, 1d4, 2d6, etc.";
        output += "\n\tp>remove <mention> <adaptive value> - (pride_dm) Remove from a player. See p>add for adaptive value examples.";
        output += "\n\tp>leaderboard - get the leaderboard";
        output += "\n\tp>help - Get a list of commands and information on pride_bot";
        output += "\n\nInfo:";
        output += "\n\tPride can be given or taken from players by a user with a role named pride_dm exactly.";
        output += "\n\tEgo is something gained upon reaching a certain threshold amount of pride, resulting in the pride being converting into the level of ego.";
        output += "\n\tThe threshold and value of a level of ego follows the pattern:  level 2 ego = 40 pride, level 3 ego = 80 pride, level 4 ego = 160 pride, etc.";
        output += "\n\tExample of leveling up ego: a player reaches 50 pride, they level up to ego level 2 and their pride is reduced to 10.";
        output += "\n\tA level of ego is lost upon reaching a pride of 0. Your ego is reduced by 1 and pride set to this lower level of egos half-way-point of pride.";
        output += "\n\tUpon reaching a threshold of ego, player's get the option of ascending.";
        output += "\n\tAscending resets your ego to 1 and pride to 10.";
        output += "\n\tUpon reaching pride less than or equal to 0 and ego is equal to 1, pride is reset to 10 and shame is incremented.";
        output += "\n\tAscendancy and Shame have an effect on adding and removing pride.";
        output += "\n\t\tAdding: pride += Max(amount + ascendancy - shame, 0)";
        output += "\n\t\tRemoving: pride -= Max(amount - ascendancy + shame, 0)";
        output += "\n\tMore features will be added later on.";
        return output;
    }

    private String ascend(String authId) {
        if (PLAYER_MANAGER.getPlayer(authId).canAscend()) {
            PLAYER_MANAGER.getPlayer(authId).ascend();
            return "You have ascended!";
        }
        else {
            return "You do not have enough ego to ascend.";
        }
    }

    private String stats(String id) {
        Player player = PLAYER_MANAGER.getPlayer(id);
        return "Pride: " + player.getPride() +
                "\nEgo: " + player.getEgo() +
                "\nPride to next Ego: " + player.prideToNextEgo() +
                "\nAscendancy: " + player.getAscendancy() +
                "\nEgo to next Ascendancy: " + player.egoToNextAscendancy() +
                "\nShame: " + player.getShame();
    }

    private int parseAdaptiveValue(String text) {
        // TODO: BUG, does not work with non dice amount
        int count = 0;
        StringBuilder temp = new StringBuilder();
        boolean hadDDelimiter = false;
        for (char c : text.toCharArray()) {
            if (c == 'd' || c == 'D') {
                count = Integer.parseInt(temp.toString());
                hadDDelimiter = true;
                temp = new StringBuilder();
            }
            else {
                temp.append(c);
            }
        }
        if (hadDDelimiter) {
            return DiceRoller.roll(Integer.parseInt(temp.toString()), count);
        }
        else {
            return count;
        }
    }

    private String addPride(String id, int amount) {
        // TODO: optimize
        Player player = PLAYER_MANAGER.getPlayer(id);
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
        Player player = PLAYER_MANAGER.getPlayer(id);
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
