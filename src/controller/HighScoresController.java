package controller;

import model.GameData;
import view.HighScoresView;

public class HighScoresController {
    private HighScoresView view;
    private GameData gameData;

    public HighScoresController(HighScoresView view, GameData gameData) {
        this.view = view;
        this.gameData = gameData;
        loadHighScores();
    }

    private void loadHighScores() {
        view.setHighScores(gameData.getHighScores());
    }
}