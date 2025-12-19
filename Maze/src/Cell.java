import java.awt.*;

public class Cell {
    private int row, col;
    private boolean visited;
    private boolean[] walls = {true, true, true, true}; // top, right, bottom, left
    private CellType type;

    public enum CellType {
        GRASS(1, new Color(144, 238, 144)),
        MUD(5, new Color(139, 90, 43)),
        WATER(10, new Color(100, 149, 237));

        private final int cost;
        private final Color color;

        CellType(int cost, Color color) {
            this.cost = cost;
            this.color = color;
        }

        public int getCost() { return cost; }
        public Color getColor() { return color; }
    }

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.visited = false;
        this.type = CellType.GRASS;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean[] getWalls() { return walls; }
    public void removeWall(int direction) { walls[direction] = false; }
    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }
    public int getCost() { return type.getCost(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cell)) return false;
        Cell other = (Cell) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return row * 1000 + col;
    }
}