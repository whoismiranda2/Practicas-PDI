package logica;

import java.awt.image.BufferedImage;
import java.util.Random;

 /**
  * Clase encargada de aplicar ruido de sal o pimienta a la imagen
  * recibe ña imagen binarizada y el porcentaje tomado desde el slider de la pantalla
  */


public class AplicaRuido {

    private static Random rnd = new Random();

    // ruido sal: píxeles blancos
    public static BufferedImage sal(BufferedImage img, int porcentaje) {
        int w = img.getWidth();
        int h = img.getHeight();
        int n = w * h * porcentaje / 100;

        for (int i = 0; i < n; i++) {
            int x = rnd.nextInt(w);
            int y = rnd.nextInt(h);
            img.setRGB(x, y, 0xffffff);
        }
        return img;
    }

    // ruido pimienta: píxeles negros
    public static BufferedImage pimienta(BufferedImage img, int porcentaje) {
        int w = img.getWidth();
        int h = img.getHeight();
        int n = w * h * porcentaje / 100;

        for (int i = 0; i < n; i++) {
            int x = rnd.nextInt(w);
            int y = rnd.nextInt(h);
            img.setRGB(x, y, 0x000000);
        }

        return img;
    }
}