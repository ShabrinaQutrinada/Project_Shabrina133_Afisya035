import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

class SetupPanel extends JPanel {
    private MainFrame frame;
    private GameTheme theme;
    private JComboBox<Integer> countBox;
    private JComboBox<String> soundBox;
    private JPanel inputContainer;
    private JTextField[] nameFields = new JTextField[5];
    private JComboBox<ColorIconItem>[] colorBoxes = new JComboBox[5];

    private final ColorIconItem[] COLOR_ITEMS = {
            new ColorIconItem("Blue", Color.BLUE),
            new ColorIconItem("Purple", new Color(128, 0, 128)),
            new ColorIconItem("Red", Color.RED),
            new ColorIconItem("Orange", Color.ORANGE),
            new ColorIconItem("Yellow", Color.YELLOW)
    };

    public SetupPanel(MainFrame frame, GameTheme theme) {
        this.frame = frame;
        this.theme = theme;
        setLayout(new BorderLayout());
        setBackground(theme == GameTheme.OUTERSPACE ? new Color(10, 10, 30) : new Color(0, 20, 50));

        JLabel title = new JLabel("PLAYER & SOUND SETUP", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 42));
        title.setForeground(Color.CYAN);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setOpaque(false);
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));

        JPanel settingsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        settingsRow.setOpaque(false);

        JLabel countLbl = new JLabel("Players: ");
        countLbl.setForeground(Color.WHITE);
        countBox = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        countBox.addActionListener(e -> refreshInputs());

        JLabel soundLbl = new JLabel("BGM Track: ");
        soundLbl.setForeground(Color.WHITE);
        soundBox = new JComboBox<>(new String[]{"No Sound", " Theme 1", " Theme 2"});

        settingsRow.add(countLbl); settingsRow.add(countBox);
        settingsRow.add(soundLbl); settingsRow.add(soundBox);

        mainContent.add(settingsRow);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        inputContainer = new JPanel(new GridLayout(5, 1, 10, 10));
        inputContainer.setOpaque(false);

        for (int i = 0; i < 5; i++) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            p.setOpaque(false);
            nameFields[i] = new JTextField("Explorer_" + (i + 1), 15);
            colorBoxes[i] = new JComboBox<>(COLOR_ITEMS);
            colorBoxes[i].setRenderer(new ColorRenderer());
            colorBoxes[i].setSelectedIndex(i);

            JLabel pLbl = new JLabel("P" + (i + 1) + " Name: ");
            pLbl.setForeground(Color.WHITE);
            p.add(pLbl);
            p.add(nameFields[i]);
            p.add(colorBoxes[i]);
            inputContainer.add(p);
        }
        mainContent.add(inputContainer);

        JButton playBtn = new JButton("START MISSION");
        playBtn.setFont(new Font("Arial Black", Font.BOLD, 26));
        playBtn.setBackground(new Color(0, 150, 255));
        playBtn.setForeground(Color.WHITE);
        playBtn.addActionListener(e -> validateAndLaunch());

        JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPnl.setOpaque(false);
        bottomPnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        bottomPnl.add(playBtn);

        add(mainContent, BorderLayout.CENTER);
        add(bottomPnl, BorderLayout.SOUTH);

        refreshInputs();
    }

    private void refreshInputs() {
        int selected = (int) countBox.getSelectedItem();
        for (int i = 0; i < 5; i++) {
            inputContainer.getComponent(i).setVisible(i < selected);
        }
        revalidate();
        repaint();
    }

    private void validateAndLaunch() {
        int n = (int) countBox.getSelectedItem();
        Set<Color> chosenColors = new HashSet<>();
        Queue<Player> q = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            ColorIconItem selected = (ColorIconItem) colorBoxes[i].getSelectedItem();
            if (chosenColors.contains(selected.color)) {
                JOptionPane.showMessageDialog(this, "Character colors must be unique!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            chosenColors.add(selected.color);
            q.add(new Player(nameFields[i].getText().trim(), selected.color));
        }

        SoundChoice sc = SoundChoice.NO_SOUND;
        int sIdx = soundBox.getSelectedIndex();
        if (sIdx == 1) sc = SoundChoice.MINECRAFT_1;
        else if (sIdx == 2) sc = SoundChoice.MINECRAFT_2;

        frame.startGame(theme, sc, q);
    }

    static class ColorIconItem {
        String name; Color color;
        ColorIconItem(String n, Color c) { name = n; color = c; }
    }

    static class ColorRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
            if (value instanceof ColorIconItem) {
                ColorIconItem item = (ColorIconItem) value;
                label.setText(item.name);
                label.setIcon(new CircleIcon(item.color));
            }
            return label;
        }
    }

    static class CircleIcon implements Icon {
        Color color;
        CircleIcon(Color c) { color = c; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y + 2, 16, 16);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 20; }
    }
}