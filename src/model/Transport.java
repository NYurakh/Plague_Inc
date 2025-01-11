package model;

import java.io.Serializable;

public class Transport implements Serializable {

    private String type;
    private Country source;
    private Country destination;
    private boolean isRouteOpen;
    private double closureThreshold;

    public Transport(String type, Country source, Country destination) {
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.isRouteOpen = true;
        this.closureThreshold = getDefaultThreshold(type);
    }

    private double getDefaultThreshold(String type) {
        switch (type.toLowerCase()) {
            case "air":
                return 0.6;
            case "boat":
                return 0.7;
            case "bus":
                return 0.8;
            default:
                return 0.75;
        }
    }

    public String getType() {
        return type;
    }

    public Country getSource() {
        return source;
    }

    public Country getDestination() {
        return destination;
    }

    public boolean isRouteOpen() {
        return isRouteOpen;
    }

    public void setRoadOpen(boolean isRouteOpen) {
        this.isRouteOpen = isRouteOpen;
    }

    public void checkAndCloseRouteIfNeeded() {
        double sourceRatio = (double) source.getInfected()/source.getPopulation();
        double destRatio = (double) destination.getInfected()/destination.getPopulation();

        if(sourceRatio > closureThreshold || destRatio > closureThreshold){
            this.isRouteOpen = false;
        }
    }
}
