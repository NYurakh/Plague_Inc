package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameData implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Country> countries;
    private List<Upgrade> allUpgrades;
    private List<HighScoreRecord> highScores;
    private Virus virus;
    private int points;
    private long startTimeMillis;
    private boolean gameRunning;

    public GameData() {
        this.countries = new ArrayList<>();
        this.points = 0;
        this.gameRunning = false;
        this.highScores = new ArrayList<>();
        initUpgrades();
    }

    private void initUpgrades() {
        allUpgrades = new ArrayList<>();

        allUpgrades.add(new Upgrade("Mask Mandate", 10, "Decrease spread rate by 5"));
        allUpgrades.add(new Upgrade("Social Distancing", 20, "Decrease spread rate by 10"));
        allUpgrades.add(new Upgrade("Border Closure", 30, "Close all transport routes"));
        allUpgrades.add(new Upgrade("Partial Lockdown", 40, "Decrease spread rate by 15"));
        allUpgrades.add(new Upgrade("Free Testing", 50, "Cure 20% of infected globally"));
        allUpgrades.add(new Upgrade("Airport Screening", 15, "Close all air transport routes"));
        allUpgrades.add(new Upgrade("Anti-Viral Drug", 100, "Cure is available in all countries"));
        allUpgrades.add(new Upgrade("Medical Aid", 25, "Cure 50% of infected globally"));
        allUpgrades.add(new Upgrade("Full Lockdown", 200, "Spread rate becomes 0"));
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void addCountry(Country c) {
        countries.add(c);
    }

    public Virus getVirus() {
        return virus;
    }

    public void setVirus(Virus virus) {
        this.virus = virus;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pts) {
        this.points += pts;
    }

    public void setGameRunning(boolean running) {
        this.gameRunning = running;
        if (running) {
            startTimeMillis = System.currentTimeMillis();
        }
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public List<Upgrade> getAllUpgrades() {
        return allUpgrades;
    }

    public boolean purchaseUpgrade(Upgrade upgrade){
        if(points >= upgrade.getCost()){
            points -= upgrade.getCost();
            upgrade.applyEffect(this);
            return true;
        }else {
            return false;
        }
    }

    public List<HighScoreRecord> getHighScores() {
        return highScores;
    }

    public void addHighScore(HighScoreRecord record) {
        highScores.add(record);
        highScores.sort((a, b) ->{
            if(a.getResultType() != b.getResultType()){
                return a.getResultType() == 'S' ? -1: 1;
            }

            int scoreComparison = Integer.compare(b.getFinalScore(), a.getFinalScore());
            if(scoreComparison != 0) return scoreComparison;
            return Long.compare(a.getTimePlayedMillis(), b.getTimePlayedMillis());
        });

    }

    public static void saveHighScores(String filePath, GameData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data.getHighScores());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadHighScores(String filePath, GameData data) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            List<HighScoreRecord> loadedScores = (List<HighScoreRecord>) ois.readObject();
            data.getHighScores().clear();
            data.getHighScores().addAll(loadedScores);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
