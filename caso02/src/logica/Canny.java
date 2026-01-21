package logica;

import java.awt.image.BufferedImage;

 /*
 * En esta clase se unifican las demás clases necesarias para implementar el Algoritmo
 * ejecuta en orden los pasos para Canny
 * se fijan los valores del doble umbral
*/
public class Canny {

    public static BufferedImage process(BufferedImage img) {

        double[][] gray = ImageUtils.toGrayMatrix(img);
        double[][] smooth = FiltroGauss.apply(gray);

        Gradiente.Gradient g = Gradiente.apply(smooth);
        double[][] nms = NonMaxSuppression.apply(g.magnitude, g.direction);
        double[][] edges = DobleUmbral.apply(nms, 10,90);

        return ImageUtils.toImage(edges);
    }
}
