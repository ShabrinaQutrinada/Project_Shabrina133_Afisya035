import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MazePanel extends JPanel {
    private Cell[][] grid;
    private int rows, cols, cellSize = 25;
    private int playerRow = 0, playerCol = 0;
    private MazeGenerator generator;
    private MazeSolver solver;
    private List<Cell> hintPath;
    private String currentAlgorithm = "";
    private Color algoColor = Color.YELLOW;
    private SoundManager soundManager;
    private boolean finished = false;
    private final int wallThickness = 6;
    private Map<String, MazeSolver.PathInfo> algorithmStats = new HashMap<>();

    public MazePanel(int rows, int cols, SoundManager soundManager) {
        this.rows = rows; this.cols = cols;
        this.soundManager = soundManager;
        setPreferredSize(new Dimension(cols * cellSize + 270, rows * cellSize + 1));
        setBackground(new Color(30, 30, 40));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!finished) handleMovement(e.getKeyCode());
            }
        });
        generateNewMaze();
    }

    public void generateNewMaze() {
        generator = new MazeGenerator(rows, cols);
        grid = generator.generateMaze();
        solver = new MazeSolver(grid);
        playerRow = 0; playerCol = 0;
        finished = false; hintPath = null;
        currentAlgorithm = "";
        algorithmStats.clear();
        repaint();
        requestFocusInWindow();
    }

    private void handleMovement(int code) {
        int dr = 0, dc = 0, wallIdx = -1;
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) { dr = -1; wallIdx = 0; }
        else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) { dc = 1; wallIdx = 1; }
        else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) { dr = 1; wallIdx = 2; }
        else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) { dc = -1; wallIdx = 3; }

        if (wallIdx != -1 && !grid[playerRow][playerCol].getWalls()[wallIdx]) {
            playerRow += dr; playerCol += dc;
            soundManager.playPathSound("src/sounds/step.wav");
            if (!currentAlgorithm.isEmpty()) updateHint();
            if (playerRow == rows - 1 && playerCol == cols - 1) {
                finished = true;
                soundManager.playCompleteSound("src/sounds/complete.wav");
                JOptionPane.showMessageDialog(this, "Finish! Maze Solved!");
            }
            repaint();
        }
    }

    public void setAlgorithm(String algo) {
        this.currentAlgorithm = algo;
        updateHint();
        repaint();
        requestFocusInWindow();
    }

    private void updateHint() {
        MazeSolver.PathInfo info = null;
        switch (currentAlgorithm) {
            case "BFS":
                info = solver.solveBFSWithInfo(playerRow, playerCol);
                algoColor = Color.GREEN;
                break;
            case "DFS":
                info = solver.solveDFSWithInfo(playerRow, playerCol);
                algoColor = Color.ORANGE;
                break;
            case "Dijkstra":
                info = solver.solveDijkstraWithInfo(playerRow, playerCol);
                algoColor = Color.RED;
                break;
            case "A*":
                info = solver.solveAStarWithInfo(playerRow, playerCol);
                algoColor = Color.MAGENTA;
                break;
        }

        if (info != null) {
            hintPath = info.path;
            algorithmStats.put(currentAlgorithm, info);
        }
    }

    public void compareAllAlgorithms() {
        algorithmStats.clear();

        algorithmStats.put("BFS", solver.solveBFSWithInfo(playerRow, playerCol));
        algorithmStats.put("DFS", solver.solveDFSWithInfo(playerRow, playerCol));
        algorithmStats.put("Dijkstra", solver.solveDijkstraWithInfo(playerRow, playerCol));
        algorithmStats.put("A*", solver.solveAStarWithInfo(playerRow, playerCol));

        repaint();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Gambar Lantai
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2.setColor(grid[r][c].getType().getColor());
                g2.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }

        // 2. Gambar Jalur Petunjuk (Hint)
        if (hintPath != null) {
            g2.setColor(new Color(algoColor.getRed(), algoColor.getGreen(), algoColor.getBlue(), 120));
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < hintPath.size() - 1; i++) {
                Cell c1 = hintPath.get(i), c2 = hintPath.get(i+1);
                g2.drawLine(c1.getCol()*cellSize+cellSize/2, c1.getRow()*cellSize+cellSize/2,
                        c2.getCol()*cellSize+cellSize/2, c2.getRow()*cellSize+cellSize/2);
            }
        }

        // 3. Gambar Dinding 3D
        draw3DWalls(g2);

        // 4. Player & Goal
        g2.setColor(Color.RED);
        g2.fillRect((cols-1)*cellSize + 6, (rows-1)*cellSize + 6, cellSize-12, cellSize-12);

        g2.setColor(Color.CYAN);
        g2.fillOval(playerCol*cellSize + 6, playerRow*cellSize + 6, cellSize-12, cellSize-12);
        g2.setColor(Color.WHITE);
        g2.drawOval(playerCol*cellSize + 6, playerRow*cellSize + 6, cellSize-12, cellSize-12);

        // 5. Draw Algorithm Comparison Panel
        if (!algorithmStats.isEmpty()) {
            drawComparisonPanel(g2);
        }
    }

    private void draw3DWalls(Graphics2D g2) {
        Color wallTop = new Color(70, 70, 80);
        Color wallSide = new Color(40, 40, 50);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * cellSize, y = r * cellSize;
                boolean[] w = grid[r][c].getWalls();
                if (w[0]) drawBlock(g2, x, y - wallThickness/2, cellSize, wallThickness, wallTop, wallSide);
                if (w[1]) drawBlock(g2, x + cellSize - wallThickness/2, y, wallThickness, cellSize, wallTop, wallSide);
                if (w[2]) drawBlock(g2, x, y + cellSize - wallThickness/2, cellSize, wallThickness, wallTop, wallSide);
                if (w[3]) drawBlock(g2, x - wallThickness/2, y, wallThickness, cellSize, wallTop, wallSide);
            }
        }
    }

    private void drawBlock(Graphics2D g2, int x, int y, int w, int h, Color top, Color side) {
        g2.setColor(side); g2.fillRect(x + 1, y + 1, w, h);
        g2.setColor(top); g2.fillRect(x, y, w, h);
    }

    private void drawComparisonPanel(Graphics2D g2) {
        int panelX = cols * cellSize + 10;
        int panelY = 10;
        int panelWidth = 250;
        int panelHeight = 280;

        // Background panel
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Algorithm Comparison", panelX + 20, panelY + 30);

        // Draw each algorithm stats
        String[] algorithms = {"BFS", "DFS", "Dijkstra", "A*"};
        Color[] colors = {Color.GREEN, Color.ORANGE, Color.RED, Color.MAGENTA};
        int yOffset = 60;

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < algorithms.length; i++) {
            String algo = algorithms[i];
            MazeSolver.PathInfo info = algorithmStats.get(algo);

            if (info != null) {
                // Color indicator
                g2.setColor(colors[i]);
                g2.fillRect(panelX + 15, panelY + yOffset - 10, 20, 20);

                // Algorithm name
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString(algo, panelX + 45, panelY + yOffset + 3);

                // Stats
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(200, 200, 200));
                g2.drawString("Steps: " + info.pathLength, panelX + 45, panelY + yOffset + 18);
                g2.drawString("Cost: " + info.totalCost, panelX + 150, panelY + yOffset + 18);

                yOffset += 50;
            }
        }

        // Find best algorithm
        String bestCost = findBestAlgorithm("cost");
        String bestSteps = findBestAlgorithm("steps");

        // Draw recommendation
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Recommendations:", panelX + 15, panelY + yOffset + 20);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(Color.YELLOW);
        g2.drawString("• Lowest Cost: " + bestCost, panelX + 15, panelY + yOffset + 38);
        g2.drawString("• Shortest Path: " + bestSteps, panelX + 15, panelY + yOffset + 53);
    }

    private String findBestAlgorithm(String criteria) {
        String best = "";
        int bestValue = Integer.MAX_VALUE;

        for (Map.Entry<String, MazeSolver.PathInfo> entry : algorithmStats.entrySet()) {
            int value = criteria.equals("cost") ? entry.getValue().totalCost : entry.getValue().pathLength;
            if (value < bestValue) {
                bestValue = value;
                best = entry.getKey();
            }
        }

        return best + " (" + bestValue + ")";
    }
}