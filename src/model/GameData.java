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
import java.util.stream.Collectors;

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

        allUpgrades.add(new Upgrade("Mask Mandate", 30, "Decrease spread rate by 3", null));
        allUpgrades.add(new Upgrade("Social Distancing", 60, "Decrease spread rate by 4", gameData -> gameData.getCountries().stream()
                .mapToInt(Country::getInfected)
                .sum() >= 0.05 * gameData.getCountries().stream()
                        .mapToInt(Country::getPopulation)
                        .sum()
        ));
        allUpgrades.add(new Upgrade("Border Closure", 150, "Close all air routes", gameData -> gameData.getCountries().stream()
                .mapToInt(Country::getInfected)
                .sum() >= 0.1 * gameData.getCountries().stream()
                        .mapToInt(Country::getPopulation)
                        .sum()
        ));
        allUpgrades.add(new Upgrade("Partial Lockdown", 175, "Decrease spread rate by 5", gameData -> gameData.getCountries().stream()
                .anyMatch(c -> c.getInfected() >= 0.2 * c.getPopulation())
        ));
        allUpgrades.add(new Upgrade("Free Testing", 50, "Cure 40% of infected globally", gameData -> gameData.getCountries().stream()
                .filter(c -> c.getInfected() > 0)
                .count() >= 3
        ));
        allUpgrades.add(new Upgrade("Airport Screening", 70, "Close all air transport routes and reduce spread rate by 1", gameData -> gameData.getCountries().stream()
                .flatMap(c -> c.getTransportLinks().stream())
                .filter(t -> t.getType().equalsIgnoreCase("Air"))
                .count() > 0
        ));
        allUpgrades.add(new Upgrade("Anti-Viral Drug", 150, "Cure is available in all countries and increases cure rate", gameData -> gameData.getPoints() >= 75  
        && gameData.getCountries().stream()
                .mapToInt(Country::getInfected)
                .sum() >= 0.25 * gameData.getCountries().stream()  
                .mapToInt(Country::getPopulation)
                .sum()
        ));
        allUpgrades.add(new Upgrade("Medical Aid", 125, "Enhanced cure availability(15s)", gameData -> gameData.getCountries().stream()
                .mapToInt(Country::getInfected)
                .sum() > 500
        ));
        allUpgrades.add(new Upgrade("Full Lockdown", 250, "Spread rate becomes 0, also closes routes", gameData -> {
            double infected = gameData.getCountries().stream()
                    .mapToInt(Country::getInfected)
                    .sum();
            double population = gameData.getCountries().stream()
                    .mapToInt(Country::getPopulation)
                    .sum();
            return infected / population > 0.4;
        }
        ));
        allUpgrades.add(new Upgrade("God Bless", 20, "Cure all infected and stop the spread", 
        gameData -> {
            // Only available if:
            double infected = gameData.getCountries().stream()
                    .mapToInt(Country::getInfected)
                    .sum();
            double population = gameData.getCountries().stream()
                    .mapToInt(Country::getPopulation)
                    .sum();
            
            return gameData.getPoints() >= 100 && // Requires points
                   (infected / population) > 0.5 && // global infection
                   gameData.getCountries().stream()
                        .anyMatch(c -> c.getInfected() >= c.getPopulation()); // At least one country fully infected
    }));
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

    public List<Upgrade> getAvailableUpgrades() {
        return allUpgrades.stream()
                .filter(upgrade -> upgrade.isAvailable(this)) // filters based on predicate
                .collect(Collectors.toList());
    }

    public boolean purchaseUpgrade(Upgrade upgrade) {
        if (points >= upgrade.getCost()) {
            points -= upgrade.getCost();
            upgrade.applyEffect(this);
            return true;
        } else {
            return false;
        }
    }

    public List<HighScoreRecord> getHighScores() {
        return highScores;
    }

    public void addHighScore(HighScoreRecord record) {
        highScores.add(record);
        highScores.sort((a, b) -> {
            if (a.getResultType() != b.getResultType()) {
                return a.getResultType() == 'S' ? -1 : 1;
            }

            int scoreComparison = Integer.compare(b.getFinalScore(), a.getFinalScore());
            if (scoreComparison != 0) {
                return scoreComparison;
            }
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
