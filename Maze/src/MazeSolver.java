import java.util.*;

public class MazeSolver {
    private Cell[][] grid;
    private int rows, cols;

    public MazeSolver(Cell[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    public List<Cell> solveBFS(int startR, int startC) {
        resetVisited();
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell start = grid[startR][startC];
        Cell end = grid[rows-1][cols-1];
        queue.offer(start);
        start.setVisited(true);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(end)) return reconstructPath(parent, end);
            for (Cell neighbor : getNeighbors(current)) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    parent.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveDFS(int startR, int startC) {
        resetVisited();
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell start = grid[startR][startC];
        Cell end = grid[rows-1][cols-1];
        stack.push(start);
        start.setVisited(true);
        parent.put(start, null);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();
            if (current.equals(end)) return reconstructPath(parent, end);
            for (Cell neighbor : getNeighbors(current)) {
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    parent.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveDijkstra(int startR, int startC) {
        resetVisited();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Cell, Integer> distance = new HashMap<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell start = grid[startR][startC];
        Cell end = grid[rows-1][cols-1];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) distance.put(grid[i][j], Integer.MAX_VALUE);
        }

        distance.put(start, 0);
        pq.offer(new Node(start, 0));
        parent.put(start, null);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Cell cell = current.cell;
            if (cell.isVisited()) continue;
            cell.setVisited(true);
            if (cell.equals(end)) return reconstructPath(parent, end);
            for (Cell neighbor : getNeighbors(cell)) {
                int newDist = distance.get(cell) + neighbor.getCost();
                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    parent.put(neighbor, cell);
                    pq.offer(new Node(neighbor, newDist));
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Cell> solveAStar(int startR, int startC) {
        resetVisited();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Cell, Integer> gScore = new HashMap<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell start = grid[startR][startC];
        Cell end = grid[rows-1][cols-1];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) gScore.put(grid[i][j], Integer.MAX_VALUE);
        }

        gScore.put(start, 0);
        pq.offer(new Node(start, heuristic(start, end)));
        parent.put(start, null);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Cell cell = current.cell;
            if (cell.isVisited()) continue;
            cell.setVisited(true);
            if (cell.equals(end)) return reconstructPath(parent, end);
            for (Cell neighbor : getNeighbors(cell)) {
                int tentativeG = gScore.get(cell) + neighbor.getCost();
                if (tentativeG < gScore.get(neighbor)) {
                    gScore.put(neighbor, tentativeG);
                    parent.put(neighbor, cell);
                    pq.offer(new Node(neighbor, tentativeG + heuristic(neighbor, end)));
                }
            }
        }
        return new ArrayList<>();
    }

    // NEW: Methods with PathInfo
    public PathInfo solveBFSWithInfo(int startR, int startC) {
        List<Cell> path = solveBFS(startR, startC);
        return new PathInfo(path, calculatePathCost(path));
    }

    public PathInfo solveDFSWithInfo(int startR, int startC) {
        List<Cell> path = solveDFS(startR, startC);
        return new PathInfo(path, calculatePathCost(path));
    }

    public PathInfo solveDijkstraWithInfo(int startR, int startC) {
        List<Cell> path = solveDijkstra(startR, startC);
        return new PathInfo(path, calculatePathCost(path));
    }

    public PathInfo solveAStarWithInfo(int startR, int startC) {
        List<Cell> path = solveAStar(startR, startC);
        return new PathInfo(path, calculatePathCost(path));
    }

    private int calculatePathCost(List<Cell> path) {
        int cost = 0;
        for (Cell cell : path) {
            cost += cell.getCost();
        }
        return cost;
    }

    private int heuristic(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0, 0}, {0, 1, 1}, {1, 0, 2}, {0, -1, 3}};
        for (int[] dir : directions) {
            if (!cell.getWalls()[dir[2]]) {
                int nR = cell.getRow() + dir[0];
                int nC = cell.getCol() + dir[1];
                if (nR >= 0 && nR < rows && nC >= 0 && nC < cols) neighbors.add(grid[nR][nC]);
            }
        }
        return neighbors;
    }

    private List<Cell> reconstructPath(Map<Cell, Cell> parent, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        while (current != null) {
            path.add(0, current);
            current = parent.get(current);
        }
        return path;
    }

    private void resetVisited() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) grid[i][j].setVisited(false);
        }
    }

    private static class Node implements Comparable<Node> {
        Cell cell; int priority;
        Node(Cell cell, int priority) { this.cell = cell; this.priority = priority; }
        @Override public int compareTo(Node o) { return Integer.compare(this.priority, o.priority); }
    }

    // NEW: PathInfo class
    public static class PathInfo {
        public List<Cell> path;
        public int totalCost;
        public int pathLength;

        public PathInfo(List<Cell> path, int totalCost) {
            this.path = path;
            this.totalCost = totalCost;
            this.pathLength = path.size();
        }
    }
}