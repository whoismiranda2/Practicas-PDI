package logica;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 Clase para convertir a niveles de grises la imagen
*/

public class ImageUtils {

    public static BufferedImage toGray(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage gray = new BufferedImage(
                w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                Color c = new Color(img.getRGB(x, y));

                int g = (int) (0.299 * c.getRed()
                             + 0.587 * c.getGreen()
                             + 0.114 * c.getBlue());

                int rgb = (g << 16) | (g << 8) | g;
                gray.setRGB(x, y, rgb);
            }
        }
        return gray;
    }
}