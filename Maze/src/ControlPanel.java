import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    public ControlPanel(MazePanel mazePanel, SoundManager soundManager) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setBackground(new Color(40, 40, 50));

        JButton genBtn = createButton("🔄 New Maze", new Color(70, 130, 180));
        genBtn.addActionListener(e -> mazePanel.generateNewMaze());
        add(genBtn);

        add(new JLabel(" | ") {{ setForeground(Color.GRAY); }});

        String[] algos = {"BFS", "DFS", "Dijkstra", "A*"};
        Color[] colors = {new Color(34, 139, 34), new Color(184, 134, 11),
                new Color(220, 20, 60), new Color(138, 43, 226)};

        for (int i = 0; i < algos.length; i++) {
            JButton b = createButton(algos[i] + " Hint", colors[i]);
            String algo = algos[i];
            b.addActionListener(e -> mazePanel.setAlgorithm(algo));
            add(b);
        }

        add(new JLabel(" | ") {{ setForeground(Color.GRAY); }});

        JButton compareBtn = createButton("📊 Compare All", new Color(255, 165, 0));
        compareBtn.addActionListener(e -> mazePanel.compareAllAlgorithms());
        add(compareBtn);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}