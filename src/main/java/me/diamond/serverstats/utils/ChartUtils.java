package me.diamond.serverstats.utils;

import me.diamond.serverstats.ServerStats;
import me.diamond.serverstats.config.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ChartUtils {

    private static Font loadFont(String path) {
        try (InputStream fontStream = ChartUtils.class.getResourceAsStream(path)) {
            if (fontStream == null) {
                throw new IOException("Font resource not found: " + path);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, 12); // Fallback font
        }
    }

    public static BufferedImage generateBarChart(HashMap<Integer, String> map) {
        ArrayList<Integer> data = new ArrayList<>();
        ArrayList<String> data2 = new ArrayList<>();
        map.forEach((key, value) -> {
            data.add(key);
        });
        map.forEach((key, value) -> {
            data2.add(value);
        });

        Font font = loadFont("/minecraft_font.ttf");
        BufferedImage existingImage = null;

        try (InputStream existingImageStream = ServerStats.class.getResourceAsStream("/Chart_Template.jpg")) {
            if (existingImageStream == null) {
                throw new IOException("Image resource not found: Chart_Template.jpg");
            }
            existingImage = ImageIO.read(existingImageStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Graphics2D g2d = existingImage.createGraphics();

        int textX = 82; // X position of the text
        int textY = 665 + 37; // Y position of the text
        g2d.setColor(Color.BLACK); // Set the text color
        g2d.setFont(font.deriveFont(Font.PLAIN, 27.97F)); // Set the font

        int i = 10;

        for (int tests = 0; tests < (16 * i); tests += i) {
            textY -= 37;
            g2d.drawString(String.valueOf(tests), textX, textY);
        }

        int barWidth = Config.get().getInt("Bar-Width"); // Width of each bar
        int barSpacing = Config.get().getInt("Bar-Spacing"); // Spacing between bars
        int startX = 255; // X-coordinate where the bars start
        int startY = 695; // Y-coordinate where the bars start
        int integer = 0;
        for (int value : data) {
            int barHeight = (37 / i) * value + 37;
            String[] rgb1 = Config.get().getString("Bar-Color").split(", ");
            int r = Integer.parseInt(rgb1[0]);
            int g = Integer.parseInt(rgb1[1]);
            int b = Integer.parseInt(rgb1[2]);

            String[] rgb2 = Config.get().getString("Bar-Outline-Color").split(", ");
            int r1 = Integer.parseInt(rgb2[0]);
            int g1 = Integer.parseInt(rgb2[1]);
            int b1 = Integer.parseInt(rgb2[2]);

            float x2 = (float) data2.get(integer).length() / 2 * 16.22f;
            g2d.setColor(Color.black);
            g2d.setFont(font.deriveFont(Font.PLAIN, 16.22F));
            g2d.drawString(data2.get(integer), startX, 726);
            integer++;

            g2d.setColor(new Color(r, g, b));
            g2d.fillRect(startX, startY - barHeight, barWidth, barHeight);
            g2d.setColor(new Color(r1, g1, g1));
            g2d.setStroke(new BasicStroke(Config.get().getInt("Bar-Outline-Width")));
            g2d.drawRect(startX, startY - barHeight, barWidth, barHeight);
            startX += (barSpacing + barWidth);
        }

        g2d.dispose();
        return existingImage;
    }

    public static BufferedImage generateLineGraph(HashMap<Integer, String> map) {
        ArrayList<Integer> data = new ArrayList<>();
        ArrayList<String> data2 = new ArrayList<>();
        map.forEach((key, value) -> {
            data.add(key);
        });
        map.forEach((key, value) -> {
            data2.add(value);
        });

        Font font = loadFont("/minecraft_font.ttf");
        BufferedImage existingImage = null;

        try (InputStream existingImageStream = ServerStats.class.getResourceAsStream("/Chart_Template.jpg")) {
            if (existingImageStream == null) {
                throw new IOException("Image resource not found: Chart_Template.jpg");
            }
            existingImage = ImageIO.read(existingImageStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Graphics2D g2d = existingImage.createGraphics();

        int textX = 82; // X position of the text
        int textY = 665 + 37; // Y position of the text
        g2d.setColor(Color.BLACK); // Set the text color
        g2d.setFont(font.deriveFont(Font.PLAIN, 27.97F)); // Set the font

        int i = 10;

        for (int tests = 0; tests < (16 * i); tests += i) {
            textY -= 37;
            g2d.drawString(String.valueOf(tests), textX, textY);
        }

        int startY = 695; // Y-coordinate where the bars start
        int valueSpacing = Config.get().getInt("Line-Value-Spacing"); // Spacing between bars
        int starttX = (int) (175 - valueSpacing + Config.get().getInt("Line-Width") / (5 / 1.5)); // X-coordinate where the bars start
        int starttY = (int) (startY - Config.get().getInt("Line-Width") / 2);
        Polygon polygon = new Polygon();
        polygon.addPoint((starttX + valueSpacing), starttY);
        int y;
        int x = 0;
        int integer = 0;
        for (int value : data) {
            int height = (37 / i) * value + 37;
            y = starttY - height;
            x = starttX - valueSpacing;
            g2d.setColor(Color.black);
            g2d.setFont(font.deriveFont(Font.PLAIN));
            float x2 = (float) data2.get(integer).length() / 2 * 16.22f;
            g2d.drawString(data2.get(integer), x, 726);
            integer++;
            starttX -= valueSpacing;
            polygon.addPoint(x, y);
        }
        polygon.addPoint(x, starttY);
        String[] rgb1 = Config.get().getString("Line-Inside-Color").split(", ");
        int r = Integer.parseInt(rgb1[0]);
        int g = Integer.parseInt(rgb1[1]);
        int b = Integer.parseInt(rgb1[2]);

        String[] rgb2 = Config.get().getString("Line-Color").split(", ");
        int r1 = Integer.parseInt(rgb2[0]);
        int g1 = Integer.parseInt(rgb2[1]);
        int b1 = Integer.parseInt(rgb2[2]);
        g2d.setColor(new Color(r, g, b));
        g2d.fillPolygon(polygon);
        g2d.setStroke(new BasicStroke(Config.get().getInt("Line-Width")));
        g2d.setColor(new Color(r1, g1, b1));
        g2d.drawPolygon(polygon);

        g2d.dispose();
        return existingImage;
    }
}
