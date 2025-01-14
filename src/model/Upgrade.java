package model;

import java.io.Serializable;

public class Upgrade implements Serializable {

    private String name;
    private int cost;
    private String effectDescription;

    public Upgrade(String name, int cost, String effectDescription) {
        this.name = name;
        this.cost = cost;
        this.effectDescription = effectDescription;
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
            case "Mask Mandate":
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 5));
                break;
            case "Social Distancing":
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 10));
                break;
            case "Border Closure":
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        if (t.getType().equalsIgnoreCase("air")) {
                            t.setRoadOpen(false);
                        }
                    }
                }
                break;
            case "Partial Lockdown":
                v.setSpreadRate(Math.max(0, v.getSpreadRate() - 15));
                break;

            case "Free Testing":
                for (Country ctry : gameData.getCountries()) {
                    ctry.cure((int) (ctry.getInfected() * 0.3)); // cure 30%
                }
                break;
            case "Airport Screening":
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        if (t.getType().equalsIgnoreCase("air")) {
                            t.setRoadOpen(false);
                        }
                    }
                }
                break;

            case "Anti-Viral Drug":
                for (Country ctry : gameData.getCountries()) {
                    ctry.setCureAvailable(true);
                }
                break;

            case "Medical Aid":
                for (Country ctry : gameData.getCountries()) {
                    ctry.cure((int) (ctry.getInfected() * 0.6));
                }
                break;
            case "Full Lockdown":
                v.setSpreadRate(0);
                for (Country c : gameData.getCountries()) {
                    for (Transport t : c.getTransportLinks()) {
                        t.setRoadOpen(false);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown upgrade effect");
        }
    }
}
