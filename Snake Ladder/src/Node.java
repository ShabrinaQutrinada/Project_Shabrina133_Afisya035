import java.awt.geom.Point2D;
import java.util.Random;

class Node {
    int id;
    int points;
    Integer targetLink = null;
    Point2D center;

    public Node(int id) {
        this.id = id;
        this.points = new Random().nextInt(151) + 50;
    }
}