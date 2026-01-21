package logica;

import java.awt.image.BufferedImage;

/*
Se analizan los pixeles, se cuentan los pixeles que fueron modificados en algún proceso y el porcentaje de pixeles
modificados entre dos imagenes seleccionadas desde la gui
*/

public class AnalizarPixeles {

    public static int contarPixelesModificados(
            BufferedImage img1, BufferedImage img2) {

        int ancho = img1.getWidth();
        int alto = img1.getHeight();
        int contador = 0;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    contador++;
                }
            }
        }
        return contador;
    }

    public static double porcentajePixelesModificados(
            BufferedImage img1, BufferedImage img2) {

        int total = img1.getWidth() * img1.getHeight();
        int modificados = contarPixelesModificados(img1, img2);

        return (modificados * 100.0) / total;
    }
}