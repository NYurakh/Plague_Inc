package model;

import java.io.Serializable;
import java.util.function.Predicate;

public class Upgrade implements Serializable {

    private String name;
    private int cost;
    private String effectDescription;
    private Predicate<GameData> availabilityCondition;

    public Upgrade(String name, int cost, String effectDescription, Predicate<GameData> condition) {
        this.name = name;
        this.cost = cost;
        this.effectDescription = effectDescription;
        this.availabilityCondition = condition;
    }

    public boolean isAvailable(GameData gameData) {
        return availabilityCondition == null || availabilityCondition.test(gameData);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public String getEffectDescription() {
        return effectDescription;
    }

    public boolean isReusable() {
        return true;
    }

    public void applyEffect(GameData gameData) {
        Virus v = gameData.getVirus();
        switch (name) {
            case "Mask Mandate" ->
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 5));
            case "Social Distancing" ->
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 10));
            case "Border Closure" -> {
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        if (t.getType().equalsIgnoreCase("air")) {
                            t.setRoadOpen(false);
                        }
                    }
                }
            }
            case "Partial Lockdown" ->
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 15));

            case "Free Testing" -> {
                for (Country ctry : gameData.getCountries()) {
                    ctry.cure((int) (ctry.getInfected() * 0.3)); // cure 30%
                }
            }
            case "Airport Screening" -> {
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        if (t.getType().equalsIgnoreCase("air")) {
                            t.setRoadOpen(false);
                        }
                    }
                }
            }

            case "Anti-Viral Drug" -> {
                for (Country ctry : gameData.getCountries()) {
                    ctry.setCureAvailable(true);
                }
            }

            case "Medical Aid" -> {
                // Grab current rates so we can revert later
                final int oldSpreadRate = v.getSpreadRate();
                final int oldCureRate = v.getCureRate();

                // Make cure rate > spread rate, e.g. spread + 10
                v.setCureRate(oldSpreadRate + 10);

                // Mark every country as temporarily curable:
                for (Country ctry : gameData.getCountries()) {
                    ctry.setCureAvailable(true);
                    ctry.setCureSymbolVisible(true);
                }

                // Launch a background thread to revert after 10 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(10_000); // 10s
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Turn off the cure:
                    for (Country ctry : gameData.getCountries()) {
                        ctry.setCureAvailable(false);
                        ctry.setCureSymbolVisible(false);
                    }
                    v.setCureRate(oldCureRate); // revert to original cure rate
                }).start();

                break;
            }
            case "Full Lockdown" -> {
                v.setSpreadRate(0);
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        t.setRoadOpen(false);
                    }
                }
            }
            default ->
                throw new IllegalArgumentException("Unknown upgrade effect");
        }
    }
}
