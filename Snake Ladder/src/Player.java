import java.awt.Color;
import java.util.Stack;

class Player implements Comparable<Player> {
    private String name;
    private int position;
    private Stack<Integer> stepsHistory;
    private int totalPoints;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.position = 1;
        this.stepsHistory = new Stack<>();
        this.stepsHistory.push(1);
        this.totalPoints = 0;
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }
    public int getTotalPoints() { return totalPoints; }

    public void setPosition(int p) { this.position = p; }
    public void addPoints(int p) { this.totalPoints += p; }

    public void pushMove(int p) { stepsHistory.push(p); }
    public int popMove() { return (stepsHistory.size() > 1) ? stepsHistory.pop() : 1; }
    public int peekLastMove() { return stepsHistory.isEmpty() ? 1 : stepsHistory.peek(); }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(o.totalPoints, this.totalPoints);
    }
}