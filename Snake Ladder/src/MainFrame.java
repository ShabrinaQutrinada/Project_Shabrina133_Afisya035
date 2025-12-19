import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

class MainFrame extends JFrame {
    private static Map<String, Integer> globalScores = new ConcurrentHashMap<>();
    private static Map<String, Integer> globalWins = new ConcurrentHashMap<>();
    private AudioManager audioManager = new AudioManager();

    public MainFrame() {
        setTitle("Snake & Ladder Galaxy Pro V41.0");
        setSize(1350, 920);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        showThemeSelection();
    }

    public void showThemeSelection() {
        getContentPane().removeAll();
        audioManager.stopBGM();
        add(new ThemeSelectionPanel(this), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showSetup(GameTheme theme) {
        getContentPane().removeAll();
        add(new SetupPanel(this, theme), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void startGame(GameTheme theme, SoundChoice sound, Queue<Player> players) {
        getContentPane().removeAll();
        audioManager.playBGM(sound);
        GamePanel panel = new GamePanel(this, theme, players, globalScores, globalWins, audioManager);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public AudioManager getAudioManager() { return audioManager; }
}