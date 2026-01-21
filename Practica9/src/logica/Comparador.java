package logica;

import java.awt.image.BufferedImage;

/*
Aquí se hace el comparador entre las imagenes que se seleccione desde la gui
marca los pixeles diferentes en rojo
*/

public class Comparador {

    public static BufferedImage comparar(BufferedImage a, BufferedImage b) {
        int w = a.getWidth();
        int h = a.getHeight();
        BufferedImage salida = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    salida.setRGB(x, y, 0xFF0000); // rojo
                } else {
                    int gris = a.getRGB(x, y) & 0xff;
                    salida.setRGB(x, y, (gris << 16) | (gris << 8) | gris);
                }
            }
        }
        return salida;
    }
}