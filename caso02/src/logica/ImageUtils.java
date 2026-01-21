package logica;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * Esta clase proporciona lo necesario para manejar la imagen
 * Convierte la imagen a grises y reescala la imagen para su visualización
 */
public class ImageUtils {

    public static double[][] toGrayMatrix(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        double[][] gray = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                gray[y][x] = 0.299 * r + 0.587 * g + 0.114 * b;
            }
        }
        return gray;
    }

    public static BufferedImage toImage(double[][] data) {
        int h = data.length;
        int w = data[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = (int) Math.max(0, Math.min(255, data[y][x]));
                int rgb = (v << 16) | (v << 8) | v;
                img.setRGB(x, y, rgb);
            }
        }
        return img;
    }

    // Ajustar imagen al tamaño del JLabel
    public static Image scaleImage(BufferedImage img, int w, int h) {
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
