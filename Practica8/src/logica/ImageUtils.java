package logica;

import java.awt.image.BufferedImage;

/*
 *Clases con operaciones auxiliares para realizar a las imágenes
*/

public class ImageUtils {

    //Convertir imagen a escala de grises
    public static BufferedImage toGray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage gray = new BufferedImage(
                w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int v = (r + g + b) / 3;
                int grayRGB = (v << 16) | (v << 8) | v;
                gray.setRGB(x, y, grayRGB);
            }
        }
        return gray;
    }

    // Operación OR
    public static BufferedImage or(BufferedImage a, BufferedImage b) {
        int w = a.getWidth(), h = a.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int v = Math.max(
                        a.getRGB(x, y) & 0xFF,
                        b.getRGB(x, y) & 0xFF);
                out.setRGB(x, y, (v << 16) | (v << 8) | v);
            }
        return out;
    }
}