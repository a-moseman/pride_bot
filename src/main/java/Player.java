public class Player {
    private final String ID;
    private String name;

    private long pride;
    private long ego;
    private long shame;
    private long ascendancy;

    public Player(String id, String name) {
        this.ID = id;
        this.name = name;
        this.pride = 10;
        this.ego = 1;
        this.shame = 0;
        this.ascendancy = 0;
    }

    /**
     * Should be invoked during player instantiation when loading from json.
     */
    public Player loadData(long pride, long level, long shame, long ascendancy) {
        this.pride = pride;
        this.ego = level;
        this.shame = shame;
        this.ascendancy = ascendancy;
        return this;
    }

    public void addPride(long amount) {
        assert amount > 0;
        pride += amount;
        // level up, converting pride into levels, until complete
        while (pride >= convertEgoToPride(ego + 1)) {
            addEgo(convertEgoToPride(ego + 1));
        }
    }

    public void removePride(long amount) {
        assert amount > 0;
        pride -= amount;
        if (pride <= 0) {
            // case in which player dies, reset to level 1 with half of level 1's pride value (10 atm I believe)
            if (ego == 1) {
                shame++;
                pride = convertEgoToPride(1) / 2;
            }
            // otherwise just handle normally
            else {
                removeEgo();
            }
        }
    }

    private void addEgo(long prideToNextEgo) {
        ego++;
        pride -= prideToNextEgo;
    }

    private void removeEgo() {
        ego--;
        // protect from getting nuked
        // but also punish for losing level
        pride = convertEgoToPride(ego) / 2;
    }

    public void ascend() {
        if (convertAscendancyToEgo(ascendancy + 1) < ego) {
            ego -= convertAscendancyToEgo(ascendancy + 1);
            ascendancy++;
        }
    }

    public boolean canAscend() {
        // TODO: test
        return convertAscendancyToEgo(ascendancy + 1) > ego;
    }

    private long convertEgoToPride(long x) {
        return (long) (5 * Math.pow(2, x + 1));
    }

    private long convertAscendancyToEgo(long x) {
        return (long) (Math.pow(2, Math.log10(2) * x) * Math.pow(5, Math.log10(2) * x + 1));
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

    public long getEgo() {
        return ego;
    }

    public long getShame() {
        return shame;
    }

    public long getAscendancy() {
        return ascendancy;
    }
}
