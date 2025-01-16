package model;

import java.io.Serializable;

public class Virus implements Serializable {

    private int spreadRate;
    private int initialInfectionRate;
    private int cureRate;

    public Virus(int spreadRate, int initialInfectionRate) {
        this.spreadRate = spreadRate;
        this.initialInfectionRate = initialInfectionRate;
        this.cureRate = 0;
    }

    public void setCureRate(int rate){
        this.cureRate = rate;
    }

    public int getCureRate(){
        return cureRate;
    }

    public int getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(int spreadRate) {
        this.spreadRate = spreadRate;
    }

    public int getInitialInfectionRate() {
        return initialInfectionRate;
    }

    public void spreadLocally(Country country) {

        if (spreadRate == 0) {
            if (country.isCureAvailable()) {
                int cured = (int) Math.ceil(country.getInfected() * (cureRate / 50.0));
                country.cure(cured);
                country.setCureSymbolVisible(true);
            }
            return;
        }
        if (country.isCureAvailable()) {
            // If cure is available, reduce infections while also curing
            int newInfections = (int) Math.ceil(country.getInfected() * (spreadRate / 200.0)); // Halved spread rate
            int cured = (int) Math.ceil(country.getInfected() * (cureRate / 100.0));
            country.infect(newInfections);
            country.cure(cured);
            country.setCureSymbolVisible(true);
        } else {
            // Normal spread without cure
            int newInfections = (int) Math.ceil(country.getInfected() * (spreadRate / 100.0));
            country.infect(newInfections);
        }
    }

    public void spreadToNeighbors(Country source) {
        if (spreadRate == 0) {
            return;
        }

        for (Transport t : source.getTransportLinks()) {
            if (t.isRouteOpen()) {
                Country dest = t.getDestination();
                // Calculate spread based on transport type and source infection ratio
                double sourceInfectionRatio = (double) source.getInfected() / source.getPopulation();
                double spreadMultiplier = getTransportSpreadMultiplier(t.getType());
                int infectionSpread = (int) (source.getInfected() * spreadMultiplier * sourceInfectionRatio);
                dest.infect(infectionSpread);
            }
        }
    }

    private double getTransportSpreadMultiplier(String transportType) {
        switch (transportType.toLowerCase()) {
            case "air":
                return 0.15;
            case "boat":
                return 0.12;
            case "bus":
                return 0.08;
            default:
                return 0.05;
        }
    }
}
