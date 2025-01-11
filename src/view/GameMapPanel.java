package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.Country;
import model.GameData;
import model.Transport;

public class GameMapPanel extends  JPanel {
    private GameData gameData;
    private Map<Country, Point> countryPositions;

    private Map<Transport, Double> transportProgress;

    private Image planeIcon;
    private Image boatIcon;
    private Image busIcon;

    public GameMapPanel(GameData data){
        this.gameData = data;
        countryPositions = new HashMap<>();
        transportProgress = new HashMap<>();

        planeIcon = new ImageIcon("C:\\Users\\nazar\\OneDrive\\Documents\\PJAIT\\first_year\\GUI\\PROJECT\\Plague_Inc\\resources\\images\\planeIcon.png").getImage();
        boatIcon = new ImageIcon("C:\\Users\\nazar\\OneDrive\\Documents\\PJAIT\\first_year\\GUI\\PROJECT\\Plague_Inc\\resources\\images\\boatIcon.png").getImage();
        busIcon = new ImageIcon("C:\\Users\\nazar\\OneDrive\\Documents\\PJAIT\\first_year\\GUI\\PROJECT\\Plague_Inc\\resources\\images\\busIcon.png").getImage();

        
    }

    public void assignCountryPossitions(){
        int size = gameData.getCountries().size();
        int radius = 200;
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;


        for (int i = 0; i < size; i++){
            double angle = 2 * Math.PI * i / size;
            int x  = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            countryPositions.put(gameData.getCountries().get(i), new Point(x, y));
        }

        for (Country c : gameData.getCountries()){
            for (Transport t : c.getTransportLinks()){
                transportProgress.put(t, Math.random());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if (gameData.getCountries().isEmpty()) return;
        if(countryPositions.isEmpty()) assignCountryPossitions();

        Graphics2D g2 = (Graphics2D) g;

        for(Country c : gameData.getCountries()){
            Point srcPos = countryPositions.get(c);
            for (Transport t : c.getTransportLinks()){
                Point dstPos = countryPositions.get(t.getDestination());
                g2. setColor(t.isRouteOpen() ? Color.LIGHT_GRAY : Color.RED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(srcPos.x, srcPos.y, dstPos.x, dstPos.y);


                double progress = transportProgress.getOrDefault(t, 0.0);
                progress += 0.01;
                if(progress > 1.0){
                    progress = 0.0;
                }
                transportProgress.put(t, progress);

                int iconX = (int) (srcPos.x + (dstPos.x - srcPos.x) * progress);
                int iconY = (int) (srcPos.y + (dstPos.y - srcPos.y) * progress);

                Image icon = planeIcon;
                if (t.getType().equalsIgnoreCase("Bus")) {
                    icon = busIcon;
                } else if (t.getType().equalsIgnoreCase("Boat")) {
                    icon = boatIcon;
                }
                if (t.isRouteOpen()) {
                    g2.drawImage(icon, iconX - 8, iconY - 8, 16, 16, null);
                } 
            }
        }


        for (Country c : gameData.getCountries()){
            Point pos = countryPositions.get(c);
            int radius = 30;
            g2.setColor(Color.GREEN);


            if(c.getInfected() > 0){
                double infectedRatio = (double)c.getInfected()/c.getPopulation();
                int redValue = (int)(255 * infectedRatio);
                int greenValue = (int)(255 * (1 - infectedRatio));
                g2.setColor(new Color(redValue, greenValue, 0));
            }
            g2.fillOval(pos.x - radius/2, pos.y - radius/2, radius, radius);

            g2.setColor(Color.BLACK);
            String info = String.format("%s\n%d/%d", c.getName(), c.getInfected(), c.getPopulation());
            g2.drawString(info, pos.x - 20, pos.y - radius/2 - 5);
        }
    }

    


    
}
