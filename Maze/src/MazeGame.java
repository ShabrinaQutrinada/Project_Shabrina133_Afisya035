import javax.swing.*;
import java.awt.*;

public class MazeGame extends JFrame {
    private MazePanel mazePanel;
    private ControlPanel controlPanel;
    private SoundManager soundManager;

    public MazeGame() {
        AudioSettingsDialog audioDialog = new AudioSettingsDialog(this);
        audioDialog.setVisible(true);

        if (!audioDialog.isConfirmed()) {
            System.exit(0);
            return;
        }

        soundManager = new SoundManager(audioDialog.isSoundEnabled());
        soundManager.setVolume(audioDialog.getVolumeLevel());

        setTitle("Maze Solver Game - Prim's Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mazePanel = new MazePanel(25, 25, soundManager);
        controlPanel = new ControlPanel(mazePanel, soundManager);

        add(mazePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        soundManager.playBackgroundMusic("src/sounds/background.wav");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                soundManager.dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeGame());
    }
}