import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AudioSettingsDialog extends JDialog {
    private boolean soundEnabled;
    private float volumeLevel;
    private boolean confirmed = false;

    public AudioSettingsDialog(JFrame parent) {
        super(parent, "Audio Settings", true);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(40, 40, 50));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(40, 40, 50));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🎵 Audio Configuration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sound enabled checkbox
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        soundPanel.setBackground(new Color(40, 40, 50));

        JCheckBox soundCheckBox = new JCheckBox("Enable Background Music");
        soundCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        soundCheckBox.setForeground(Color.WHITE);
        soundCheckBox.setBackground(new Color(40, 40, 50));
        soundCheckBox.setSelected(true);
        soundCheckBox.setFocusPainted(false);
        soundPanel.add(soundCheckBox);
        mainPanel.add(soundPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Volume control
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.Y_AXIS));
        volumePanel.setBackground(new Color(40, 40, 50));

        JLabel volumeLabel = new JLabel("Volume: 50%");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(new Color(40, 40, 50));
        volumeSlider.setForeground(new Color(70, 130, 180));
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setFont(new Font("Arial", Font.PLAIN, 10));
        volumeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Update label when slider changes
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = volumeSlider.getValue();
                volumeLabel.setText("Volume: " + value + "%");
                volumeSlider.setEnabled(soundCheckBox.isSelected());
            }
        });

        // Enable/disable slider based on checkbox
        soundCheckBox.addActionListener(e -> {
            volumeSlider.setEnabled(soundCheckBox.isSelected());
        });

        volumePanel.add(volumeLabel);
        volumePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        volumePanel.add(volumeSlider);
        mainPanel.add(volumePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info text
        JLabel infoLabel = new JLabel("<html><center>Background music will play throughout the game.<br>" +
                "Path sounds will play during algorithm animation.</center></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(180, 180, 180));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(40, 40, 50));

        JButton startButton = createStyledButton("Start Game", new Color(34, 139, 34));
        startButton.addActionListener(e -> {
            soundEnabled = soundCheckBox.isSelected();
            volumeLevel = volumeSlider.getValue() / 100.0f;
            confirmed = true;
            dispose();
        });

        JButton cancelButton = createStyledButton("Exit", new Color(220, 20, 60));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.brighter(), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getVolumeLevel() {
        return volumeLevel;
    }
}