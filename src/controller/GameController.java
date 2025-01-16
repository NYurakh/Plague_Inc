package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.*;
import view.GameView;
import view.MainMenuView;
import view.UpgradesView;

public class GameController {

    private GameView gameView;
    private GameData gameData;
    private String difficulty;

    // threads
    private  GameUpdateThread gameUpdateThread;  // For virus spread
    private UIUpdateThread uiUpdateThread; // For UI 


    public GameController(GameView gameView, GameData gameData, String difficulty) {
        this.gameView = gameView;
        this.gameData = gameData;
        this.difficulty = difficulty;

        // Ctrl+Shift+Q to exit
        bindExitShortcut();

        setupUpgradeButtonListener();
        startThreads();
    }

    // Getters/Setters
    public GameData getGameData() {
        return gameData;
    }
    
    public GameView getGameView() {
        return gameView;
    }

    // Methods
    private void startThreads() {
        gameUpdateThread = new GameUpdateThread(this);
        uiUpdateThread = new UIUpdateThread(this);
        
        gameUpdateThread.start();
        uiUpdateThread.start();
    }

    public void updateGame() {
        // Spread virus
        Virus virus = gameData.getVirus();
        for (Country c : gameData.getCountries()) {
            virus.spreadLocally(c);
        }
        // Spread to neighbors
        for (Country c : gameData.getCountries()) {
            virus.spreadToNeighbors(c);
        }
        // Award points based on saved population percentage
        int totalPopulation = gameData.getCountries().stream().mapToInt(Country::getPopulation).sum();
        int nonInfected = gameData.getCountries().stream().mapToInt(c -> c.getPopulation() - c.getInfected()).sum();
        int savedPercentage = (nonInfected * 100) / totalPopulation;

        // fewer points as savedPercentage increases
        gameData.addPoints(Math.max(1, savedPercentage / 10));

        //end conditions check
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

            //show main menu again
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
        
        gameView.getUpgradeButton().addActionListener(e -> {
            // new UpgradesView dialg
            UpgradesView upgradesView = new UpgradesView(gameView, gameData.getAvailableUpgrades());

            // action listener for the buy button inside the upgrades dialog
            upgradesView.getBuyButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = upgradesView.getSelectedUpgradeIndex();
                    if (selectedIndex >= 0) {
                        Upgrade selectedUpgrade = gameData.getAvailableUpgrades().get(selectedIndex);
                        if (gameData.getPoints() >= selectedUpgrade.getCost()) {
                            gameData.addPoints(-selectedUpgrade.getCost()); // minuse points for purchase
                            selectedUpgrade.applyEffect(gameData); 
                            
                            upgradesView.showSuccessMessage("Upgrade purchased: " + selectedUpgrade.getName());

                            // Remove non-reusable upgrades
                            // I Have all upgredes reusable, just didn't implement the unreusable ones
                            // So this do nothing:)
                            if (!selectedUpgrade.isReusable()) {
                                upgradesView.removeUpgrade(selectedIndex);
                                gameData.getAvailableUpgrades().remove(selectedIndex);
                            }
                        } else {
                            upgradesView.showErrorMessage("Not enough points for: " + selectedUpgrade.getName());
                        }
                    } else {
                        upgradesView.showErrorMessage("No upgrade selected!");
                    }
                }
            });

            // show the dialog
            upgradesView.setVisible(true);
        });
    }

}
