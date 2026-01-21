package logica;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * La clase contiene los métodos necesarios para que la imagen se muestre en Pantalla
 * se reescala o redimensiona para que, independientemente de las dimensiones de la imagen, 
 * se muestre perfectamente en pantalla.
 * también se obtiene el canal gris de la imagen para, posteriormente, umbralizar o binarizar la imagen
 */

public class ProcesarImagen {

    // Reescalar imagen 
    public static BufferedImage reescalar(BufferedImage original, int width, int height) {
        Image tmp = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage reescalar = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = reescalar.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return reescalar;
    }

    // Convertir RGB a YIQ para obtener gris (Y)
    public static BufferedImage grises(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage gris = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                // Componente Y (de YIQ)
                int Y = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                int grisRGB = (Y << 16) | (Y << 8) | Y;
                gris.setRGB(x, y, grisRGB);
            }
        }
        return gris;
    }

    // Binarización con umbral
    public static BufferedImage binariza(BufferedImage img, int umbral) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage bin = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y) & 0xff;

                int val = (rgb < umbral) ? 0 : 255;
                int binRGB = (val << 16) | (val << 8) | val;
                bin.setRGB(x, y, binRGB);
            }
        }

        return bin;
    }
}