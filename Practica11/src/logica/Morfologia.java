package logica;

import java.awt.image.BufferedImage;

/**
 * Clase que contiene las operaciones morfológicas realizadas
 * Dilatación
 * Eroción
 * Apertura
 * Cierre
 */

public class Morfologia {

    public static BufferedImage dilatacion(BufferedImage img, ElementoEstructurante ee) {

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(
                w, h, BufferedImage.TYPE_BYTE_GRAY);

        int[][] mask = ee.getMask();
        int c = ee.getCenter();

        for (int x = c; x < w - c; x++) {
            for (int y = c; y < h - c; y++) {

                int max = 0;

                for (int i = -c; i <= c; i++) {
                    for (int j = -c; j <= c; j++) {
                        if (mask[i + c][j + c] == 1) {
                            int val = img.getRGB(x + i, y + j) & 0xff;
                            max = Math.max(max, val);
                        }
                    }
                }

                int rgb = (max << 16) | (max << 8) | max;
                out.setRGB(x, y, rgb);
            }
        }
        return out;
    }

    public static BufferedImage erosion(BufferedImage img, ElementoEstructurante ee) {

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage out = new BufferedImage(
                w, h, BufferedImage.TYPE_BYTE_GRAY);

        int[][] mask = ee.getMask();
        int c = ee.getCenter();

        for (int x = c; x < w - c; x++) {
            for (int y = c; y < h - c; y++) {

                int min = 255;

                for (int i = -c; i <= c; i++) {
                    for (int j = -c; j <= c; j++) {
                        if (mask[i + c][j + c] == 1) {
                            int val = img.getRGB(x + i, y + j) & 0xff;
                            min = Math.min(min, val);
                        }
                    }
                }

                int rgb = (min << 16) | (min << 8) | min;
                out.setRGB(x, y, rgb);
            }
        }
        return out;
    }

    public static BufferedImage apertura(BufferedImage img, ElementoEstructurante ee) {

        BufferedImage e = erosion(img, ee);
        return dilatacion(e, ee);
    }

    public static BufferedImage cierre(BufferedImage img, ElementoEstructurante ee) {

        BufferedImage d = dilatacion(img, ee);
        return erosion(d, ee);
    }
}