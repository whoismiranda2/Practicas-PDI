package logica;

import java.awt.image.BufferedImage;

/**
 * Clase para implementar las operaciones morfológicas
 * se emplea un elemento estructurante fijo: un disco de 5x5
 * Métodos realizados: Eroción, Dilatación, Apertura y Cierre
 * en todos los casos, recibe la imagen binaria y regresa el resultado según la opeeración
 */

public class Morf {

    private static final int[][] eEstruct = {   //disco
        {0,0,1,0,0},
        {0,1,1,1,0},
        {1,1,1,1,1},
        {0,1,1,1,0},
        {0,0,1,0,0}
    };

    public static BufferedImage erosion(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 1; y < h-1; y++) {
            for (int x = 1; x < w-1; x++) {
                boolean quedar = true;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        if (eEstruct[ky+1][kx+1] == 1) {
                            int val = (img.getRGB(x+kx, y+ky) & 0xff);

                            if (val == 0) { // si algún pixel es negro, se erosiona
                                quedar = false;
                                break;
                            }
                        }
                    }
                }
                out.setRGB(x, y, quedar ? 0xffffff : 0x000000);
            }
        }

        return out;
    }

    public static BufferedImage dilatacion(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 1; y < h-1; y++) {
            for (int x = 1; x < w-1; x++) {

                boolean blanco = false;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        if (eEstruct[ky+1][kx+1] == 1) {
                            int val = (img.getRGB(x+kx, y+ky) & 0xff);
                            if (val == 255) {
                                blanco = true;
                                break;
                            }
                        }
                    }
                }
                out.setRGB(x, y, blanco ? 0xffffff : 0x000000);
            }
        }

        return out;
    }

    public static BufferedImage apertura(BufferedImage img) {
        return dilatacion(erosion(img));
    }

    public static BufferedImage cierre(BufferedImage img) {
        return erosion(dilatacion(img));
    }
}