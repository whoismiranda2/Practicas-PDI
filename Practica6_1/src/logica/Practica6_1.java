package logica;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Practica6_1 {

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

    public static BufferedImage desplazar(BufferedImage img, int valor) {
        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = Math.min(255, Math.max(0, c.getRed() + valor));
                int g = Math.min(255, Math.max(0, c.getGreen() + valor));
                int b = Math.min(255, Math.max(0, c.getBlue() + valor));
                salida.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return salida;
    }

    public static BufferedImage ajustarContraste(BufferedImage img, double factor) {
        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int r = (int) (128 + factor * (c.getRed() - 128));
                int g = (int) (128 + factor * (c.getGreen() - 128));
                int b = (int) (128 + factor * (c.getBlue() - 128));
                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
                salida.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return salida;
    }

    public static double[] calcularProbabilidad(int[] hist, int totalPixeles) {
        double[] p = new double[256];
        for (int i = 0; i < 256; i++) {
            p[i] = (double) hist[i] / totalPixeles;
        }
        return p;
    }

    public static double[] calcularDistribucionAcumulada(double[] p) {
        double[] D = new double[256];
        D[0] = p[0];
        for (int i = 1; i < 256; i++) {
            D[i] = D[i - 1] + p[i];
        }
        return D;
    }

    public static BufferedImage ecualizar(BufferedImage img, String tipo, double alpha, double pot) {
        int[] hist = calcularHistograma(img);
        double[] p = calcularProbabilidad(hist, img.getWidth() * img.getHeight());
        double[] D = calcularDistribucionAcumulada(p);

        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        double fmin = 0.0;
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

                switch (tipo) {  //formulas de la tabla
                    case "Uniforme":
                        nuevo = fmin + (fmax - fmin) * dp - fmin;
                        break;

                    case "Exponencial":
                        nuevo = fmin - (1.0 / alpha) * Math.log(1.0 - dp + epsilon);
                        break;

                    case "Rayleigh":
                        nuevo = fmin + Math.sqrt(2 * alpha * alpha * Math.log(1.0 / (1.0 - dp + epsilon)));
                        break;

                    case "Hiperbólica Raíces":
                        nuevo = Math.pow(((Math.pow(fmax, 1.0 / pot) - Math.pow(fmin, 1.0 / pot)) * dp
                                + Math.pow(fmin, 1.0 / pot)), pot);
                        break;
                }

                int nivel = (int) Math.max(0, Math.min(255, nuevo));
                salida.setRGB(x, y, new Color(nivel, nivel, nivel).getRGB());
            }
        }

        return salida;
    }
    
    public static BufferedImage correspondencia(BufferedImage img1, BufferedImage img2) {
        int[] h1 = calcularHistograma(img1);
        int[] h2 = calcularHistograma(img2);
        double[] p1 = calcularProbabilidad(h1, img1.getWidth() * img1.getHeight());
        double[] p2 = calcularProbabilidad(h2, img2.getWidth() * img2.getHeight());
        double[] D1 = calcularDistribucionAcumulada(p1);
        double[] D2 = calcularDistribucionAcumulada(p2);

        int[] mapa = new int[256];
        for (int i = 0; i < 256; i++) {
            double val = D1[i];
            int j = 0;
            while (j < 255 && D2[j] < val) j++;
            mapa[i] = j;
        }

        BufferedImage salida = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                int rgb = img1.getRGB(x, y);
                int gris = (int) ((0.299 * ((rgb >> 16) & 0xFF))
                        + (0.587 * ((rgb >> 8) & 0xFF))
                        + (0.114 * (rgb & 0xFF)));
                int nuevo = mapa[gris];
                salida.setRGB(x, y, new Color(nuevo, nuevo, nuevo).getRGB());
            }
        }
        return salida;
    }
}