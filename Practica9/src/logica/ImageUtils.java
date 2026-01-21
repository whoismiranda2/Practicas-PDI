package logica;

import java.awt.image.BufferedImage;

/*
 En esta clase se incluye el metodo para pasar la imagen a niveles de grises
 Se usa Y de YIQ
*/

public class ImageUtils {
    public static BufferedImage convertirAGrises(BufferedImage img) {

        int ancho = img.getWidth();
        int alto = img.getHeight();

        BufferedImage imagenGris = new BufferedImage(
                ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                // Componente Y del modelo YIQ
                int yiq = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                // Asegurar rango [0,255]
                yiq = Math.max(0, Math.min(255, yiq));

                int grisRGB = (0xff << 24) | (yiq << 16) | (yiq << 8) | yiq;
                imagenGris.setRGB(x, y, grisRGB);
            }
        }

        return imagenGris;
    }
}




