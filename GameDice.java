import java.util.Random;

class GameDice {
    private int value;
    private boolean isForward;
    private Random random = new Random();

    public void roll() {
        value = random.nextInt(6) + 1;
        isForward = (random.nextDouble() <= 0.7);
    }

    public int getValue() { return value; }
    public boolean isForward() { return isForward; }
}