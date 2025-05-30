package model;

import java.io.Serializable;

public class HighScoreRecord implements Serializable{
    private  String playerName;
    private int finalScore;
    private String difficulty;
    private long timePlayedMillis;
    private char resultType; //S or I: Saved, Infected

    public HighScoreRecord(String playerName, int finalScore, String difficulty, long timePlayedMillis, char resultType) {
        this.playerName = playerName;
        this.finalScore = finalScore;
        this.difficulty = difficulty;
        this.timePlayedMillis = timePlayedMillis;
        this.resultType = resultType;
    }

    public char getResultType(){
        return resultType;
    }

    public String getPlayerName(){
        return playerName;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public long getTimePlayedMillis() {
        return timePlayedMillis;
    }

    @Override
    public String toString() {
        return String.format("%s - Score: %d - Difficulty: %s - Time: %.2f sec - Result: %c",
                playerName, finalScore, difficulty, timePlayedMillis / 1000.0, resultType);
    }
}