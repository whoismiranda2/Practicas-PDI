package logica;

import java.awt.image.BufferedImage;

/*
 *Clase que implementa los filtros pasabajas:
  -Suavizado
  -Definición
*/

public class PasaBajas {

    //suavizado
    public static BufferedImage suavizado(BufferedImage img, double[][] kernel) {
        return Convolucion.apply(img, kernel);
    }

    //definición
    public static BufferedImage definicion(BufferedImage img, double[][] kernel) {
        return Convolucion.apply(img, kernel);
    }
}