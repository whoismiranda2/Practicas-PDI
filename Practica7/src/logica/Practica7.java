package logica;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Practica7 {

    public static int[] calcularHistograma(BufferedImage img) {
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int gris = (int) ((0.299 * ((rgb >> 16) & 0xFF))
                        + (0.587 * ((rgb >> 8) & 0xFF))
                        + (0.114 * (rgb & 0xFF)));
                hist[gris]++;
            }
        }
        return hist;
    }

    //Probabilidades
    public static double[] calcularProbabilidad(int[] hist, int totalPixeles) {
        double[] p = new double[256];
        for (int i = 0; i < 256; i++) {
            p[i] = (double) hist[i] / totalPixeles;
        }
        return p;
    }

    //Distribución acumulada
    public static double[] calcularDistribucionAcumulada(double[] p) {
        double[] D = new double[256];
        D[0] = p[0];
        for (int i = 1; i < 256; i++) {
            D[i] = D[i - 1] + p[i];
        }
        return D;
    }

    //Ecualización (varios tipos)
    public static BufferedImage ecualizar(BufferedImage img, String tipo, double alpha, double pot) {
        int[] hist = calcularHistograma(img);
        double[] p = calcularProbabilidad(hist, img.getWidth() * img.getHeight());
        double[] D = calcularDistribucionAcumulada(p);

        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        double fmin = 1.0;
        double fmax = 255.0;
        double epsilon = 1e-8; // evitar log(0)

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int gris = (int) ((0.299 * ((rgb >> 16) & 0xFF))
                        + (0.587 * ((rgb >> 8) & 0xFF))
                        + (0.114 * (rgb & 0xFF)));
                double dp = D[gris];
                double nuevo = 0;

                switch (tipo) {     //formulas de la tabla
                    case "Uniforme":
                        nuevo = fmin + (fmax - fmin) * dp;
                        break;

                    case "Exponencial":
                        if (alpha <= 0) alpha = 1.0;
                        nuevo = fmin - (1.0 / alpha) * Math.log(1.0 - dp + epsilon);
                        break;

                    case "Rayleigh":
                        if (alpha <= 0) alpha = 1.0;
                        nuevo = fmin + Math.sqrt(2 * alpha * alpha * Math.log(1.0 / (1.0 - dp + epsilon)));
                        break;

                    case "Hiperbólica Raíces":
                        nuevo = Math.pow(((Math.pow(fmax, 1.0 / pot) - Math.pow(fmin, 1.0 / pot)) * dp
                                + Math.pow(fmin, 1.0 / pot)), pot);
                        break;
                    
                    case "Hiperbólica Logarítmica":
                        nuevo = fmin * Math.pow((fmax/fmin), dp);
                        break;

                    default:
                        nuevo = fmin + (fmax - fmin) * dp;
                }

                int nivel = (int) Math.max(0, Math.min(255, Math.round(nuevo)));
                salida.setRGB(x, y, new Color(nivel, nivel, nivel).getRGB());
            }
        }

        return salida;
    }
}