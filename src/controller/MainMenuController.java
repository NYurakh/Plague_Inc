package controller;

import model.Country;
import model.GameData;
import model.Transport;
import model.Virus;
import view.DifficultySelectionView;
import view.GameView;
import view.HighScoresView;
import view.MainMenuView;

public class MainMenuController {
    private MainMenuView mainMenuView;
    private GameData gameData;  

    public MainMenuController(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        this.gameData = new GameData();
        // load previous high scores if any
        GameData.loadHighScores("highscores.dat", this.gameData);

        setupListeners();
    }

    private void setupListeners() {
        mainMenuView.getNewGameButton().addActionListener(e -> onNewGame());
        mainMenuView.getHighScoresButton().addActionListener(e -> onHighScores());
        mainMenuView.getExitButton().addActionListener(e -> System.exit(0));
    }

    private void onNewGame() {
        DifficultySelectionView dialog = new DifficultySelectionView(mainMenuView);
        dialog.setVisible(true);
        String selectedDifficulty = dialog.getSelectedDifficulty();
        if (selectedDifficulty == null) {
            return; 
        }

        // Some changes between the difficulties like initial spread rate and initial infection rate
        Virus virus;
        switch (selectedDifficulty) {
            case "Easy":
                virus = new Virus(2, 10);
                break;
            case "Medium":
                virus = new Virus(5, 15);
                break;
            case "Hard":
            default:
                virus = new Virus(10, 20);
                break;
        }
        gameData.setVirus(virus);

        // Clear any old countries, add new ones
        gameData.getCountries().clear();
        createWorld();

        // Infect the initial country randomly
        int randomIndex = (int) (Math.random() * gameData.getCountries().size());
        gameData.getCountries().get(randomIndex).infect(virus.getInitialInfectionRate());

        // Start game
        GameView gameView = new GameView(gameData);
        new GameController(gameView, gameData, selectedDifficulty);
        gameData.setGameRunning(true);

        mainMenuView.setVisible(false);
        gameView.setVisible(true);
    }

    private void onHighScores() {
        HighScoresView hsv = new HighScoresView();
        hsv.setHighScores(gameData.getHighScores());
        hsv.setVisible(true);
    }

    // Some countries creation
    private void createWorld() {
        Country c1 = new Country("USA", 1000);
        Country c2 = new Country("Canada", 800);
        Country c3 = new Country("Mexico", 900);
        Country c4 = new Country("Brazil", 1200);
        Country c5 = new Country("UK", 600);
        Country c6 = new Country("France", 700);
        Country c7 = new Country("Germany", 750);
        Country c8 = new Country("Moskovia", 1200);
        Country c9 = new Country("China", 2000);
        Country c10 = new Country("Australia", 500);

        // Add them to gameData
        gameData.addCountry(c1);
        gameData.addCountry(c2);
        gameData.addCountry(c3);
        gameData.addCountry(c4);
        gameData.addCountry(c5);
        gameData.addCountry(c6);
        gameData.addCountry(c7);
        gameData.addCountry(c8);
        gameData.addCountry(c9);
        gameData.addCountry(c10);

        // some transport routes
        c1.addTransportLink(new Transport("Air", c1, c2));
        c2.addTransportLink(new Transport("Air", c2, c1));
        c2.addTransportLink(new Transport("Bus", c2, c1));
        c2.addTransportLink(new Transport("Air", c2, c3));
        c2.addTransportLink(new Transport("Boat", c2, c5));
        c3.addTransportLink(new Transport("Air", c3, c10));
        c3.addTransportLink(new Transport("Boat", c3, c4));
        c4.addTransportLink(new Transport("Boat", c4, c5));
        c4.addTransportLink(new Transport("Bus", c4, c3));
        c5.addTransportLink(new Transport("Air", c5, c9));
        c5.addTransportLink(new Transport("Bus", c5, c6));
        c5.addTransportLink(new Transport("Boat", c5, c6));
        c6.addTransportLink(new Transport("Boat", c6, c7));
        c6.addTransportLink(new Transport("Air", c6, c7));
        c7.addTransportLink(new Transport("Air", c7, c3));
        c7.addTransportLink(new Transport("Bus", c7, c8));
        c8.addTransportLink(new Transport("Boat", c8, c5));
        c8.addTransportLink(new Transport("Air", c8, c5));
        c9.addTransportLink(new Transport("Air", c9, c8));
        c10.addTransportLink(new Transport("Air", c10, c9));
        c10.addTransportLink(new Transport("Air", c10, c3));
        c10.addTransportLink(new Transport("Air", c10, c2));
    }
}
