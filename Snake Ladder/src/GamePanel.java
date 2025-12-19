import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class GamePanel extends JPanel {
    private MainFrame frame;
    private GameTheme theme;
    private Queue<Player> turnQueue;
    private Player currentPlayer;
    private Node[] nodes = new Node[65];
    private Map<Integer, Integer> links = new HashMap<>();
    private AudioManager audio;
    private JSlider volSlider;

    private GameDice dice = new GameDice();
    private boolean rolling = false;
    private boolean moving = false;
    private String statusMsg = "Ready to roll!";
    private List<String> missionLog = new ArrayList<>();

    private float animPulse = 0f;
    private List<DecoObject> decoList = new CopyOnWriteArrayList<>();

    private Map<String, Integer> statsScores;
    private Map<String, Integer> statsWins;

    public GamePanel(MainFrame frame, GameTheme theme, Queue<Player> players, Map<String, Integer> ts, Map<String, Integer> tw, AudioManager audio) {
        this.frame = frame;
        this.theme = theme;
        this.turnQueue = players;
        this.statsScores = ts;
        this.statsWins = tw;
        this.audio = audio;

        setLayout(null);
        initBoard();
        initVolumeControl();

        // Timer untuk animasi global (Pulsing effect dan pergerakan deco)
        new javax.swing.Timer(35, e -> {
            animPulse = (float)(0.5 + Math.sin(System.currentTimeMillis() * 0.005) * 0.5);
            for(DecoObject d : decoList) d.update(getWidth(), getHeight());
            repaint();
        }).start();

        nextTurn();
    }

    private void initBoard() {
        for (int i = 1; i <= 64; i++) nodes[i] = new Node(i);

        // Setup Jalur Wormhole/Link
        links.put(31, 59);
        links.put(18, 36);
        links.put(5, 25);
        links.put(47, 12);
        links.put(3, 21);

        for (Map.Entry<Integer, Integer> entry : links.entrySet()) {
            nodes[entry.getKey()].targetLink = entry.getValue();
        }
        for(int i=0; i<45; i++) decoList.add(new DecoObject(theme));
    }

    private void initVolumeControl() {
        volSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, audio.getVolume());
        volSlider.setBounds(30, 185, 230, 20);
        volSlider.setOpaque(false);
        volSlider.setFocusable(false);
        volSlider.addChangeListener(e -> {
            audio.setVolume(volSlider.getValue());
            repaint();
        });
        add(volSlider);
    }

    private void nextTurn() {
        currentPlayer = turnQueue.poll();
        turnQueue.offer(currentPlayer);
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) if (n % i == 0) return false;
        return true;
    }

    private void addLog(String msg) {
        missionLog.add(msg);
        if (missionLog.size() > 6) missionLog.remove(0);
    }

    private void performRoll() {
        if (rolling || moving) return;
        rolling = true;
        javax.swing.Timer timer = new javax.swing.Timer(60, null);
        final int[] f = {0};
        timer.addActionListener(e -> {
            audio.playSFX("Dice.wav");
            dice.roll();
            f[0]++;
            if (f[0] > 18) {
                timer.stop();
                rolling = false;
                startStepAnimation();
            }
        });
        timer.start();
    }

    private void startStepAnimation() {
        moving = true;
        int steps = dice.getValue();
        boolean forward = dice.isForward();
        boolean fromPrime = isPrime(currentPlayer.getPosition());

        javax.swing.Timer moveTimer = new javax.swing.Timer(400, null);
        final int[] count = {0};

        moveTimer.addActionListener(e -> {
            if (count[0] < steps) {
                if (forward) {
                    int next = currentPlayer.getPosition() + 1;
                    if (next <= 64) {
                        currentPlayer.setPosition(next);
                        currentPlayer.pushMove(next);
                        addLog(currentPlayer.getName() + ": step " + (count[0]+1) + "/" + steps + " Forward");
                        if (fromPrime && links.containsKey(next)) {
                            count[0]++;
                            if (count[0] <= steps) {
                                int jump = links.get(next);
                                currentPlayer.setPosition(jump);
                                currentPlayer.pushMove(jump);
                                addLog(currentPlayer.getName() + ": wormhole jump!");
                            }
                        }
                    }
                } else {
                    currentPlayer.popMove();
                    currentPlayer.setPosition(currentPlayer.peekLastMove());
                    addLog(currentPlayer.getName() + ": step " + (count[0]+1) + "/" + steps + " Backward");
                }
                count[0]++;
                repaint();
            } else {
                moveTimer.stop();
                finalizeTurn();
            }
        });
        moveTimer.start();
    }

    private void finalizeTurn() {
        int pos = currentPlayer.getPosition();
        currentPlayer.addPoints(nodes[pos].points);
        statsScores.put(currentPlayer.getName(), Math.max(statsScores.getOrDefault(currentPlayer.getName(), 0), currentPlayer.getTotalPoints()));

        if (pos >= 64) {
            handleVictory();
            return;
        }

        if (pos % 5 == 0) {
            statusMsg = "BONUS TURN: " + currentPlayer.getName();
            moving = false;
        } else {
            statusMsg = currentPlayer.getName() + " arrived at Node " + pos;
            moving = false;
            nextTurn();
        }
    }

    private void handleVictory() {
        statsWins.put(currentPlayer.getName(), statsWins.getOrDefault(currentPlayer.getName(), 0) + 1);
        PriorityQueue<Player> ranking = new PriorityQueue<>(turnQueue);
        StringBuilder sb = new StringBuilder("🏆 MISSION ACCOMPLISHED! " + currentPlayer.getName() + " WINS!\n\nFINAL SCOREBOARD:\n");
        int r = 1;
        while(!ranking.isEmpty()){
            Player p = ranking.poll();
            sb.append(r++).append(". ").append(p.getName()).append(" - ").append(p.getTotalPoints()).append(" Points\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Victory Announcement", JOptionPane.INFORMATION_MESSAGE);
        frame.showThemeSelection();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawAdvancedBackground(g2);

        int cell = Math.min(getWidth(), getHeight()) / 12;
        int sx = (getWidth() - 8 * cell) / 2 + 100;
        int sy = (getHeight() - 8 * cell) / 2 + 50;

        drawElegantCurvedLinks(g2, sx, sy, cell);

        for (int i = 1; i <= 64; i++) {
            int row = (i - 1) / 8;
            int col = (i - 1) % 8;
            if (row % 2 != 0) col = 7 - col;
            int px = sx + col * cell;
            int py = sy + (7 - row) * cell;
            nodes[i].center = new Point2D.Float(px + cell/2.0f, py + cell/2.0f);
            drawElegantNode(g2, i, px, py, cell);
        }

        for (Player p : turnQueue) drawPlayerVisual(g2, p);
        drawSideHUD(g2);
        drawMissionLog(g2);
    }

    private void drawAdvancedBackground(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        if (theme == GameTheme.OUTERSPACE) {
            g2.setPaint(new GradientPaint(0, 0, new Color(5, 5, 25), w, h, new Color(20, 10, 50)));
            g2.fillRect(0, 0, w, h);
            drawPlanet(g2, w-220, 180, 130, new Color(50, 150, 255), true);
            drawPlanet(g2, 280, h-180, 80, new Color(255, 100, 50), false);
            drawPlanetWithRing(g2, w-180, h-220, 55, new Color(255, 200, 120));
            drawPlanet(g2, 450, 120, 100, new Color(255, 180, 60), false);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(0, 40, 90), w, h, new Color(0, 90, 170)));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new GradientPaint(0,0, new Color(255,255,255,35), 0, h/2, new Color(255,255,255,0)));
            for(int i=0; i<w; i+=250) g2.fillPolygon(new int[]{i, i+90, i-40}, new int[]{0, h, h}, 3);
            g2.setColor(new Color(255, 100, 100, 140));
            for(int i=0; i<w; i+=120) g2.fillRoundRect(i, h-70, 30, 90, 15, 15);
        }
        for(DecoObject d : decoList) d.render(g2, theme);
    }

    private void drawPlanet(Graphics2D g2, int x, int y, int r, Color base, boolean earth) {
        RadialGradientPaint rgp = new RadialGradientPaint(new Point2D.Double(x, y), r, new float[]{0f, 1f},
                new Color[]{base, new Color(0, 0, 0, 140)});
        g2.setPaint(rgp);
        g2.fillOval(x-r, y-r, r*2, r*2);
        if(earth) {
            g2.setColor(new Color(100, 255, 100, 70));
            g2.fillOval(x-r/2, y-r/3, r/2, r/3); g2.fillOval(x+r/5, y+r/4, r/3, r/4);
        }
    }

    private void drawPlanetWithRing(Graphics2D g2, int x, int y, int r, Color base) {
        drawPlanet(g2, x, y, r, base, false);
        g2.setStroke(new BasicStroke(4f));
        g2.setColor(new Color(255, 255, 255, 80));
        g2.drawOval(x-r-25, y-10, (r+25)*2, 20);
    }

    private void drawElegantNode(Graphics2D g2, int id, int x, int y, int size) {
        int m = 14;
        Ellipse2D circle = new Ellipse2D.Double(x + m, y + m, size - m*2, size - m*2);

        // Node Body
        g2.setColor(new Color(20, 20, 65, 210));
        g2.fill(circle);

        // Node Border
        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(theme == GameTheme.OUTERSPACE ? Color.CYAN : Color.WHITE);
        g2.draw(circle);

        // Special Star for multiples of 5
        if (id % 5 == 0) drawShiningStar(g2, x + size/2, y + size/2, 11);

        // Node ID Text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(String.valueOf(id), x + size/2 - 8, y + size/2 + 6);

        // Points Text
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
        g2.setColor(new Color(150, 255, 180));
        g2.drawString("+" + nodes[id].points, x + size/2 - 14, y + size - 12);

        // --- TAMBAHAN ESTETIK: START & FINISH LABELS ---
        if (id == 1 || id == 64) {
            String label = (id == 1) ? "START" : "FINISH";
            Color glowColor = (id == 1) ? new Color(0, 255, 200) : new Color(255, 100, 100);

            g2.setFont(new Font("Verdana", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            // Posisi teks di samping kiri node
            int tx = x - labelWidth - 10;
            int ty = y + size/2 + 5;

            // Efek Glow Menyala (Pulsing)
            float alpha = 0.5f + (0.5f * animPulse);
            for (int i = 3; i > 0; i--) {
                g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), (int)(alpha * 60 / i)));
                g2.drawString(label, tx - i, ty);
                g2.drawString(label, tx + i, ty);
            }

            g2.setColor(Color.WHITE);
            g2.drawString(label, tx, ty);
        }
    }

    private void drawShiningStar(Graphics2D g2, int x, int y, int r) {
        g2.setColor(new Color(255, 255, 100, (int)(animPulse * 180 + 75)));
        int[] xp = new int[10]; int[] yp = new int[10];
        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI / 5 - Math.PI / 2;
            double rad = (i % 2 == 0) ? r * 1.5 : r * 0.7;
            xp[i] = (int)(x + rad * Math.cos(angle)); yp[i] = (int)(y + rad * Math.sin(angle));
        }
        g2.fillPolygon(xp, yp, 10);
    }

    private void drawElegantCurvedLinks(Graphics2D g2, int sx, int sy, int cell) {
        int index = 0;
        List<Map.Entry<Integer, Integer>> sortedLinks = new ArrayList<>(links.entrySet());
        sortedLinks.sort(Comparator.comparingInt(Map.Entry::getKey));

        for (Map.Entry<Integer, Integer> e : sortedLinks) {
            Point2D p1 = getCalculatedCenter(e.getKey(), sx, sy, cell);
            Point2D p2 = getCalculatedCenter(e.getValue(), sx, sy, cell);

            double midX = (p1.getX() + p2.getX()) / 2.0;
            double midY = (p1.getY() + p2.getY()) / 2.0;
            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();
            double len = Math.sqrt(dx * dx + dy * dy);

            if (len == 0) continue;

            double nx = -dy / len;
            double ny = dx / len;
            double curveOffset = 50 + (index * 40);
            if (index % 2 == 0) curveOffset *= -1;

            double ctrlX = midX + nx * curveOffset;
            double ctrlY = midY + ny * curveOffset;

            QuadCurve2D curve = new QuadCurve2D.Double(p1.getX(), p1.getY(), ctrlX, ctrlY, p2.getX(), p2.getY());
            Color baseColor = (theme == GameTheme.OUTERSPACE) ? new Color(220, 100, 255) : new Color(255, 200, 50);

            g2.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 25));
            g2.draw(curve);

            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{20, 15}, 0));
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 180));
            g2.draw(curve);
            index++;
        }
    }

    private Point2D getCalculatedCenter(int id, int sx, int sy, int cell) {
        int row = (id - 1) / 8;
        int col = (id - 1) % 8;
        if (row % 2 != 0) col = 7 - col;
        return new Point2D.Float(sx + col * cell + cell/2.0f, sy + (7 - row) * cell + cell/2.0f);
    }

    private void drawPlayerVisual(Graphics2D g2, Player p) {
        Point2D pos = nodes[p.getPosition()].center;
        if (pos == null) return;
        int cx = (int)pos.getX(), cy = (int)pos.getY();
        g2.setColor(new Color(p.getColor().getRed(), p.getColor().getGreen(), p.getColor().getBlue(), (int)(animPulse * 140)));
        g2.fillOval(cx-26, cy-26, 52, 52);
        g2.setColor(p.getColor());
        g2.fillOval(cx-14, cy-14, 28, 28);
        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx-14, cy-14, 28, 28);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        g2.drawString(p.getName(), cx - 20, cy + 38);
    }

    private void drawSideHUD(Graphics2D g2) {
        g2.setColor(new Color(15, 15, 40, 230));
        g2.fillRoundRect(20, 20, 260, 130, 20, 20);
        g2.setColor(Color.CYAN); g2.drawRoundRect(20, 20, 260, 130, 20, 20);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.CYAN);
        g2.drawString("TURN: " + currentPlayer.getName(), 35, 45);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("Node: " + currentPlayer.getPosition(), 35, 68);
        g2.drawString("Points: " + currentPlayer.getTotalPoints(), 35, 88);
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(new Color(150, 255, 150));
        g2.drawString(statusMsg, 35, 115);

        g2.setColor(rolling ? Color.WHITE : (dice.isForward() ? new Color(0, 255, 100) : new Color(255, 50, 50)));
        g2.fillRoundRect(295, 20, 105, 105, 22, 22);
        g2.setColor(Color.BLACK); g2.setFont(new Font("Monospaced", Font.BOLD, 60));
        String dVal = rolling ? String.valueOf(new Random().nextInt(6)+1) : String.valueOf(dice.getValue());
        g2.drawString(dVal, 328, 92);

        g2.setColor(new Color(15, 15, 40, 230));
        g2.fillRoundRect(20, 160, 260, 60, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("VOLUME CONTROL (" + audio.getVolume() + ")", 35, 180);
    }

    private void drawMissionLog(Graphics2D g2) {
        int x = 20, y = 230, w = 260, h = 135;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, w, h, 20, 20);
        g2.setColor(Color.CYAN);
        g2.drawRoundRect(x, y, w, h, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("MISSION LOG", x + 15, y + 25);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(new Color(180, 255, 200));
        int logY = y + 45;
        for(int i = Math.max(0, missionLog.size()-5); i < missionLog.size(); i++) {
            g2.drawString("> " + missionLog.get(i), x + 15, logY);
            logY += 15;
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (new Rectangle(295, 20, 105, 105).contains(e.getPoint())) performRoll();
            }
        });
    }
}