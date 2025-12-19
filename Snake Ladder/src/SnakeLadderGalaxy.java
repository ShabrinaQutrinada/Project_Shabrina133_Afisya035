import javax.swing.*;

public class SnakeLadderGalaxy {
    public static void main(String[] args) {
        // Optimize graphics rendering for smoothness
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}