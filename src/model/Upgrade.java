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
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 3));
            case "Social Distancing" ->
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 4));
            case "Border Closure" -> {
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        if (t.getType().equalsIgnoreCase("bus") || 
                            t.getType().equalsIgnoreCase("train")) {  
                            t.setRoadOpen(false);
                        }
                    }
                }
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 1));  
            }
            case "Partial Lockdown" ->
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 5));

            case "Free Testing" -> {
                for (Country ctry : gameData.getCountries()) {
                    ctry.cure((int) (ctry.getInfected() * 0.4)); 
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
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 1));
            }

            case "Anti-Viral Drug" -> {
                for (Country ctry : gameData.getCountries()) {
                    ctry.setCureAvailable(true);
                }
                v.setCureRate(v.getCureRate() + 5);  
            }

            case "Medical Aid" -> {
                final int oldSpreadRate = v.getSpreadRate();
            final int oldCureRate = v.getCureRate();
            
            v.setCureRate(oldSpreadRate + 15);  
            
            for (Country ctry : gameData.getCountries()) {
                ctry.setCureAvailable(true);
                ctry.setCureSymbolVisible(true);
                ctry.cure((int)(ctry.getInfected() * 0.2));  
            }

            new Thread(() -> {
                try {
                    Thread.sleep(15_000);  
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                for (Country ctry : gameData.getCountries()) {
                    ctry.setCureAvailable(false);
                    ctry.setCureSymbolVisible(false);
                }
                v.setCureRate(oldCureRate);
            }).start();
        }
            case "Full Lockdown" -> {
                v.setSpreadRate(0);
            for (Country c : gameData.getCountries()) {
                for (Transport t : c.getTransportLinks()) {
                    t.setRoadOpen(false);
                }
                c.cure((int)(c.getInfected() * 0.2)); 
            }
        }
        case "God Bless" -> {
            
            v.setSpreadRate(0);
            
            
            for (Country ctry : gameData.getCountries()) {
                ctry.cure(ctry.getInfected()); // Cure everyone
                ctry.setCureAvailable(true);
                
                // Close all transport routes
                for (Transport t : ctry.getTransportLinks()) {
                    t.setRoadOpen(false);
                }
            }
            
            // Boost cure rate permanently
            v.setCureRate(v.getCureRate() + 20);
            
            // Visual feedback - make all countries show cure symbol
            for (Country ctry : gameData.getCountries()) {
                ctry.setCureSymbolVisible(true);
            }
        }
            default ->
                throw new IllegalArgumentException("Unknown upgrade effect");
        }
    }
}
