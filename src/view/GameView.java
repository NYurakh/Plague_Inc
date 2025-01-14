package view;

import java.awt.*;
import javax.swing.*;
import model.GameData;

public class GameView extends JFrame {

    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JButton upgradeButton;
    private GameMapPanel mapPanel;
    private UpgradesView upgradesView;

    public GameView(GameData gameData) {
        setTitle(
                "Antiplague Coronavirus - Game"
        );
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        timerLabel = new JLabel("Time: 0s");
        scoreLabel = new JLabel("Score: 0");
        upgradeButton = new JButton("Buy Upgrades");
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(upgradeButton);

        mapPanel = new GameMapPanel(gameData);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);

        // Add action listener to trigger UpgradesView as a dialog
        upgradeButton.addActionListener(e -> {
            if (upgradesView == null) {
                upgradesView = new UpgradesView(this, gameData.getAllUpgrades());
                upgradesView.setVisible(true);
    
                upgradesView.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        upgradesView = null;
                    }
    
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        upgradesView = null;
                    }
                });
            }
        });
    }

    public void updateTimerLabel(long seconds) {
        timerLabel.setText("Time: " + seconds + "s");
    }

    public void updateScoreLabel(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public JButton getUpgradeButton() {
        return upgradeButton;
    }

    public GameMapPanel getMapPanel() {
        return mapPanel;
    }
}
