import java.util.*;

public class MazeGenerator {
    private Cell[][] grid;
    private int rows, cols;
    private Random random;

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.random = new Random();
        this.grid = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell[][] generateMaze() {
        // 1. Prim's Algorithm (Standard)
        List<Wall> walls = new ArrayList<>();
        Cell start = grid[0][0];
        start.setVisited(true);
        addWalls(start, walls);

        while (!walls.isEmpty()) {
            int index = random.nextInt(walls.size());
            Wall wall = walls.remove(index);
            Cell c1 = wall.cell1;
            Cell c2 = wall.cell2;

            if (c1.isVisited() != c2.isVisited()) {
                c1.removeWall(wall.direction);
                c2.removeWall((wall.direction + 2) % 4);
                Cell unvisited = c1.isVisited() ? c2 : c1;
                unvisited.setVisited(true);
                addWalls(unvisited, walls);
            }
        }

        int extraPaths = (rows * cols) / 10;
        for (int i = 0; i < extraPaths; i++) {
            int r = random.nextInt(rows - 2) + 1;
            int c = random.nextInt(cols - 2) + 1;
            int dir = random.nextInt(4);

            // Buka dinding di posisi acak
            grid[r][c].removeWall(dir);
            // Buka dinding tetangganya juga agar sinkron
            if (dir == 0 && r > 0) grid[r-1][c].removeWall(2);
            else if (dir == 1 && c < cols-1) grid[r][c+1].removeWall(3);
            else if (dir == 2 && r < rows-1) grid[r+1][c].removeWall(0);
            else if (dir == 3 && c > 0) grid[r][c-1].removeWall(1);
        }

        assignCellTypes();

        // Reset visited untuk solver
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) grid[i][j].setVisited(false);
        }

        return grid;
    }

    private void addWalls(Cell cell, List<Wall> walls) {
        int[][] directions = {{-1, 0, 0}, {0, 1, 1}, {1, 0, 2}, {0, -1, 3}};
        for (int[] dir : directions) {
            int nR = cell.getRow() + dir[0];
            int nC = cell.getCol() + dir[1];
            if (nR >= 0 && nR < rows && nC >= 0 && nC < cols && !grid[nR][nC].isVisited()) {
                walls.add(new Wall(cell, grid[nR][nC], dir[2]));
            }
        }
    }

    private void assignCellTypes() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double rand = random.nextDouble();
                if (rand < 0.5) grid[i][j].setType(Cell.CellType.GRASS);
                else if (rand < 0.8) grid[i][j].setType(Cell.CellType.MUD);
                else grid[i][j].setType(Cell.CellType.WATER);
            }
        }
        grid[0][0].setType(Cell.CellType.GRASS);
        grid[rows-1][cols-1].setType(Cell.CellType.GRASS);
    }

    private static class Wall {
        Cell cell1, cell2; int direction;
        Wall(Cell c1, Cell c2, int d) { this.cell1 = c1; this.cell2 = c2; this.direction = d; }
    }
}