import javax.swing.*;
import java.awt.*;

class ThemeSelectionPanel extends JPanel {
    private MainFrame frame;

    public ThemeSelectionPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        JLabel title = new JLabel("CHOOSE YOUR WORLD ADVENTURE");
        title.setFont(new Font("Arial Black", Font.BOLD, 45));
        title.setForeground(Color.WHITE);

        JButton spaceBtn = createButton("OUTERSPACE", new Color(30, 20, 80));
        JButton seaBtn = createButton("BENEATH THE SEA", new Color(0, 80, 150));

        spaceBtn.addActionListener(e -> frame.showSetup(GameTheme.OUTERSPACE));
        seaBtn.addActionListener(e -> frame.showSetup(GameTheme.BENEATH_THE_SEA));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 70, 0);
        add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(15, 15, 15, 15);
        add(spaceBtn, gbc);
        gbc.gridy = 2;
        add(seaBtn, gbc);
    }

    private JButton createButton(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setPreferredSize(new Dimension(450, 90));
        b.setFont(new Font("Monospaced", Font.BOLD, 26));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        return b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, new Color(10, 10, 30), getWidth(), getHeight(), new Color(30, 0, 60)));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}