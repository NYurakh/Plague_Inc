package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Country implements Serializable{

    private String name;
    private int population;
    private int infected;
    private boolean isCureAvailable;
    private List<Transport> transportLinks;

    public Country(String name, int popolation) {
        this.name = name;
        this.population = popolation;
        this.infected = 0;
        this.isCureAvailable = false;
        this.transportLinks = new ArrayList<>();

    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getInfected() {
        return infected;
    }

    public boolean isCureAvailable() {
        return isCureAvailable;
    }

    public void setCureAvailable(boolean cureAvailable) {
        isCureAvailable = cureAvailable;
    }

    public void infect(int number) {
        this.infected = Math.min(population, infected + number);
    }

    public void cure(int number) {
        this.infected = Math.max(0, infected - number);
    }

    public void addTransportLink(Transport transport) {
        transportLinks.add(transport);
    }

    public List<Transport> getTransportLinks() {
        return transportLinks;
    }

    public boolean isFullyInfected(){
        return infected >= population;
    }

    public boolean isSafe(){
        return infected == 0;
    }
}
