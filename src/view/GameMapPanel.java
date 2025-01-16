package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.Country;
import model.GameData;
import model.Transport;

public class GameMapPanel extends JPanel {

    private GameData gameData;
    private Map<Country, Point2D.Double> normalizedPositions = new HashMap<>();

    private Map<Transport, Double> transportProgress;

    private Image planeIcon;
    private Image boatIcon;
    private Image busIcon;
    private Image backgroundImage;

    public GameMapPanel(GameData data) {
        this.gameData = data;
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = currentDir.getParentFile();
        try {
            backgroundImage = new ImageIcon(projectRoot + "/resources/images/worldMap.png").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        transportProgress = new HashMap<>();

        try {
            // Get the path relative to where we are
            // Go up one level from src

            // Build the paths using the project root
            planeIcon = new ImageIcon(projectRoot + "/resources/images/planeIcon.png").getImage();
            boatIcon = new ImageIcon(projectRoot + "/resources/images/boatIcon.png").getImage();
            busIcon = new ImageIcon(projectRoot + "/resources/images/busIcon.png").getImage();

        } catch (Exception e) {
            e.printStackTrace();
        }

        initNormalizedPositions();
    }

    private void initNormalizedPositions() {
        // For each country, store normalized (x, y) = (pixelX / 2036, pixelY / 1492)
        for (Country c : gameData.getCountries()) {
            switch (c.getName()) {
                case "USA" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(470.0 / 2036.0, 747.0 / 1492.0));
                case "Canada" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(246.0 / 2036.0, 521.0 / 1492.0));
                case "Mexico" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(371.0 / 2036.0, 795.0 / 1492.0));
                case "Brazil" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(673.0 / 2036.0, 982.0 / 1492.0));
                case "UK" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1040.0 / 2036.0, 490.0 / 1492.0));
                case "France" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1030.0 / 2036.0, 640.0 / 1492.0));
                case "Germany" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1130.0 / 2036.0, 610.0 / 1492.0));
                case "Moskovia" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1680.0 / 2036.0, 590.0 / 1492.0));
                case "China" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1840.0 / 2036.0, 680.0 / 1492.0));
                case "Australia" ->
                    normalizedPositions.put(c,
                            new Point2D.Double(1870.0 / 2036.0, 1080.0 / 1492.0));
                default ->
                    normalizedPositions.put(c, new Point2D.Double(0.5, 0.5));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g
    ) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Draw the background image, scaled to fit the panel:
        if (backgroundImage != null) {
            float transparency = 0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        if (gameData.getCountries().isEmpty()) {
            return;
        }

        // --- Draw transport lines/vehicles ---
        for (Country c : gameData.getCountries()) {
            Point2D.Double srcNorm = normalizedPositions.get(c);
            if (srcNorm == null) {
                continue;
            }

            // Convert normalized -> actual pixels
            int srcX = (int) (srcNorm.x * getWidth());
            int srcY = (int) (srcNorm.y * getHeight());

            for (Transport t : c.getTransportLinks()) {
                Country dest = t.getDestination();
                Point2D.Double dstNorm = normalizedPositions.get(dest);
                if (dstNorm == null) {
                    continue;
                }

                int dstX = (int) (dstNorm.x * getWidth());
                int dstY = (int) (dstNorm.y * getHeight());

                // draw line
                g2.setColor(t.isRouteOpen() ? Color.LIGHT_GRAY : Color.RED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(srcX, srcY, dstX, dstY);

                // animate transport icon
                double progress = transportProgress.getOrDefault(t, 0.0);
                progress += 0.01;
                if (progress > 1.0) {
                    progress = 0.0;
                }
                transportProgress.put(t, progress);

                int iconX = (int) (srcX + (dstX - srcX) * progress);
                int iconY = (int) (srcY + (dstY - srcY) * progress);

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

        // --- Draw country circles & labels ---
        for (Country c : gameData.getCountries()) {
            Point2D.Double norm = normalizedPositions.get(c);
            if (norm == null) {
                continue;
            }

            int x = (int) (norm.x * getWidth());
            int y = (int) (norm.y * getHeight());
            int radius = 30;

            // infection color
            double infectedRatio = (double) c.getInfected() / c.getPopulation();
            int redValue = (int) (255 * infectedRatio);
            int greenValue = (int) (255 * (1 - infectedRatio));
            g2.setColor(new Color(redValue, greenValue, 0));
            g2.fillOval(x - radius / 2, y - radius / 2, radius, radius);

            // cure symbol if needed
            if (c.isCureSymbolVisible()) {
                g2.setColor(Color.BLUE);
                g2.drawString("⚕", x - radius / 2, y + radius / 2);
            }

            // country info
            g2.setColor(Color.BLACK);
            String info = String.format("%s\n%d/%d", c.getName(), c.getInfected(), c.getPopulation());
            g2.drawString(info, x - 20, y - radius / 2 - 5);
        }
    }
}
