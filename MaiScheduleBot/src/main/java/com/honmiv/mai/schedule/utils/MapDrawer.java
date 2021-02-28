package com.honmiv.mai.schedule.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Update;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class MapDrawer {

    private static Point parseLocation(String location) {

        //Location{longitude=37.537542, latitude=55.892286} - пример Location.toString();

        double x = (int) (Double.parseDouble(location.substring(location.indexOf("=") + 1, location.indexOf(","))) * 1000000);
        double y = (int) (Double.parseDouble(location.substring(location.lastIndexOf("=") + 1, location.lastIndexOf("}"))) * 1000000);

        return new Point(x, y);
    }

    public static String getMaiMapPath() {
        return "maimap.png";
    }

    public static String drawLocation(Update update) {
        Point newXY = coordsToPixels(parseLocation(update.getMessage().getLocation().toString()));
        return drawAndGetPath(newXY.x, newXY.y, update.getMessage().getChatId().toString());
    }

    public static String drawAndGetPath(double x, double y, String fileName) {
        BufferedImage mapImg = null;
        BufferedImage mapMarkerImg = null;

        try {
            mapImg = ImageIO.read(new File("canvas.png"));
            mapMarkerImg = ImageIO.read(new File("map-marker.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (x < 0 || y < 0 || x > mapImg.getHeight() || y > mapImg.getWidth())
            return null;

        Graphics g = mapImg.getGraphics();

        double res = (int) (Math.min(mapImg.getWidth(), mapImg.getHeight()) * 0.1);

        x -= res / 2;
        y -= res;

        g.drawImage(mapMarkerImg, (int) x, (int) y, (int) res, (int) res, null);

        String newFilePath = fileName + ".png";

        File newMapImg = new File(newFilePath);
        try {
            ImageIO.write(mapImg, "png", newMapImg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newMapImg.getPath();
    }

    private static Point coordsToPixels(Point coords) {

        BufferedImage mapImg = null;

        try {
            mapImg = ImageIO.read(new File("canvas.png"));
        } catch (IOException e) {
            log.error("exception was thrown during reading map image: {}" + e.getMessage());
            throw new RuntimeException(e);
        }

        // левый верхний угол карты

        double x0 = 37489791;
        double y0 = 55814840;

        // правый нижний угол карты

        double x1 = 37507495;
        double y1 = 55805647;

        double coordPerX = (x1 - x0) / mapImg.getWidth();
        double coordPerY = (y0 - y1) / mapImg.getHeight();

        double x = (coords.x - x0) / coordPerX;
        double y = (y0 - coords.y) / coordPerY;

        return new Point(x, y);
    }

    static class Point {
        double x;
        double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
