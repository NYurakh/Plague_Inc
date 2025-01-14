package controller;

import model.*;
import view.GameView;
import view.MainMenuView;
import view.UpgradesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController {

    private GameView gameView;
    private GameData gameData;
    private String difficulty;

    // Timers/threads
    private Timer gameLoopTimer;  // For virus spread
    private Timer uiRefreshTimer; // For UI (time, animations, etc.)

    private long lastUpdateMillis;

    public GameController(GameView gameView, GameData gameData, String difficulty) {
        this.gameView = gameView;
        this.gameData = gameData;
        this.difficulty = difficulty;

        // Key binding for Ctrl+Shift+Q
        bindExitShortcut();

        setupUpgradeButtonListener();
        startGameLoop();
        startUIRefreshLoop();
    }

    private void startGameLoop() {
        lastUpdateMillis = System.currentTimeMillis();
        gameLoopTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameData.isGameRunning()) {
                    gameLoopTimer.stop();
                    return;
                }
                updateGame();
            }
        });
        gameLoopTimer.start();
    }

    private void startUIRefreshLoop() {
        uiRefreshTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameData.isGameRunning()) {
                    uiRefreshTimer.stop();
                    return;
                }
                // Update time label
                long elapsed = (System.currentTimeMillis() - gameData.getStartTimeMillis()) / 1000;
                gameView.updateTimerLabel(elapsed);
                gameView.updateScoreLabel(gameData.getPoints());

                // repaint the map for animations
                gameView.getMapPanel().repaint();
            }
        });
        uiRefreshTimer.start();
    }

    private void updateGame() {
        // Spread virus
        Virus virus = gameData.getVirus();
        for (Country c : gameData.getCountries()) {
            virus.spreadLocally(c);
        }
        // Spread to neighbors
        for (Country c : gameData.getCountries()) {
            virus.spreadToNeighbors(c);
        }
        // Award points for each non-infected person
        // Award points based on saved population percentage
        int totalPopulation = gameData.getCountries().stream().mapToInt(Country::getPopulation).sum();
        int nonInfected = gameData.getCountries().stream().mapToInt(c -> c.getPopulation() - c.getInfected()).sum();
        int savedPercentage = (nonInfected * 100) / totalPopulation;

        // Reward fewer points as savedPercentage increases
        gameData.addPoints(Math.max(1, savedPercentage / 10));

        // Check end conditions
        checkEndConditions();
    }

    private void checkEndConditions() {
        boolean allInfected = true;
        boolean allSafe = true;

        for (Country c : gameData.getCountries()) {
            if (!c.isFullyInfected()) {
                allInfected = false;
            }
            if (!c.isSafe()) {
                allSafe = false;
            }
        }

        if (allInfected || allSafe) {
            // Game ends
            gameData.setGameRunning(false);
            String message = allInfected ? "All countries infected!" : "All countries saved!";
            char resultType = allInfected ? 'I' : 'S';
            JOptionPane.showMessageDialog(gameView, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

            // Prompt for name
            String name = JOptionPane.showInputDialog(gameView, "Enter your name for High Scores:");
            if (name != null && !name.trim().isEmpty()) {
                long timePlayed = System.currentTimeMillis() - gameData.getStartTimeMillis();
                int finalScore = gameData.getPoints();
                HighScoreRecord rec = new HighScoreRecord(name, finalScore, difficulty, timePlayed, resultType);
                gameData.addHighScore(rec);
                GameData.saveHighScores("highscores.dat", gameData);
            }
            // Return to main menu
            gameView.dispose();
            //show main menu again here or let user relaunch

            MainMenuView mainMenu = new MainMenuView();
            new MainMenuController(mainMenu);
            mainMenu.setVisible(true);
        }
    }

    private void bindExitShortcut() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control shift Q");
        gameView.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(keyStroke, "EXIT_SHORTCUT");
        gameView.getRootPane().getActionMap().put("EXIT_SHORTCUT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Return to main menu
                gameData.setGameRunning(false);
                gameView.dispose();
                // show the main menu again:
                MainMenuView mainMenu = new MainMenuView();
                new MainMenuController(mainMenu);
                mainMenu.setVisible(true);
            }
        });
    }

    private void setupUpgradeButtonListener() {
        // Add action listener to the upgrade button in GameView
        gameView.getUpgradeButton().addActionListener(e -> {
            // Create a new UpgradesView dialog
            UpgradesView upgradesView = new UpgradesView(gameView, gameData.getAllUpgrades());

            // Add action listener for the buy button inside the upgrades dialog
            upgradesView.getBuyButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = upgradesView.getSelectedUpgradeIndex();
                    if (selectedIndex >= 0) {
                        Upgrade selectedUpgrade = gameData.getAllUpgrades().get(selectedIndex);
                        if (gameData.getPoints() >= selectedUpgrade.getCost()) {
                            gameData.addPoints(-selectedUpgrade.getCost()); // Deduct points
                            selectedUpgrade.applyEffect(gameData); // Apply effect
                            upgradesView.updatePointsDisplay(gameData.getPoints());
                            upgradesView.showSuccessMessage("Upgrade purchased: " + selectedUpgrade.getName());

                            // Remove non-reusable upgrades
                            if (!selectedUpgrade.isReusable()) {
                                upgradesView.removeUpgrade(selectedIndex);
                                gameData.getAllUpgrades().remove(selectedIndex);
                            }
                        } else {
                            upgradesView.showErrorMessage("Not enough points for: " + selectedUpgrade.getName());
                        }
                    } else {
                        upgradesView.showErrorMessage("No upgrade selected!");
                    }
                }
            });

            // Show the dialog
            upgradesView.setVisible(true);
        });
    }

}
