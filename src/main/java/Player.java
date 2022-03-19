public class Player {
    private final String ID;
    private String name;

    private long pride;
    private long level;
    private long deaths;

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
    public Player loadData(long pride, long level, long deaths) {
        this.pride = pride;
        this.level = level;
        this.deaths = deaths;
        return this;
    }

    public void addPride(long amount) {
        assert amount > 0;
        pride += amount;
        // level up, converting pride into levels, until complete
        while (pride >= convertLevelToPride(level + 1)) {
            addLevel(convertLevelToPride(level + 1));
        }
    }

    public void removePride(long amount) {
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

    private void addLevel(long prideToNextLevel) {
        level++;
        pride -= prideToNextLevel;
    }

    private void removeLevel(long prideToNextLevel) {
        level--;
        // protect from getting nuked
        // but also punish for losing level
        pride = convertLevelToPride(level) / 2;
    }

    private long convertLevelToPride(long level) {
        return (long) (5 * Math.pow(2, level + 1));
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

    public long getPride() {
        return pride;
    }

    public long getLevel() {
        return level;
    }

    public long getDeaths() {
        return deaths;
    }
}
