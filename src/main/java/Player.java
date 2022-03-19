public class Player {
    private final String ID;
    private String name;

    private int pride;
    private int level;
    private int deaths;

    public Player(String id, String name) {
        this.ID = id;
        this.name = name;
        this.pride = 0;
        this.level = 1;
        this.deaths = 0;
    }

    /**
     * Should be invoked during player instantiation when loading from json.
     */
    public Player loadData(int pride, int level, int deaths) {
        this.pride = pride;
        this.level = level;
        this.deaths = deaths;
        return this;
    }

    public void addPride(int amount) {
        assert amount > 0;
        pride += amount;
        // level up, converting pride into levels, until complete
        while (pride >= convertLevelToPride(level + 1)) {
            addLevel(convertLevelToPride(level + 1));
        }
    }

    public void removePride(int amount) {
        // TODO: test
        assert amount > 0;
        pride -= amount;
        if (pride < 0) {
            // case in which player dies, reset to level 1 with half of level 1's pride value (10 atm I believe)
            if (level == 1) {
                deaths++;
                pride = convertLevelToPride(1) / 2;
            }
            // otherwise just handle normally
            else {
                removeLevel(convertLevelToPride(level));
            }
        }
    }

    private void addLevel(int prideToNextLevel) {
        level++;
        pride -= prideToNextLevel;
    }

    private void removeLevel(int prideToNextLevel) {
        level--;
        pride += prideToNextLevel;
        // protect player from getting pride nuked
        // just gives a bit of a buffer as a nicety
        if (pride < convertLevelToPride(level) / 2) {
            pride = convertLevelToPride(level) / 2;
        }
    }

    private int convertLevelToPride(int level) {
        return (int) (5 * Math.pow(2, level + 1));
    }

    //___Getter Methods___\\

    public String getId() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPride() {
        return pride;
    }

    public int getLevel() {
        return level;
    }

    public int getDeaths() {
        return deaths;
    }
}
