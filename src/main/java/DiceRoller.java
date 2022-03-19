import java.util.Random;

public class DiceRoller {
    private final static Random RANDOM = new Random();

    public static int roll(int sides, int count) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += RANDOM.nextInt(sides) + 1;
        }
        return total;
    }
}
