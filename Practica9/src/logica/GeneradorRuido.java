package logica;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *Clase que contiene los ruidos necesarios para implementar los filtros no lineales mencionados
 */
public class GeneradorRuido {
 
    private static final Random generadorAleatorio = new Random();

    // ===================== RUIDO GAMMA =====================
    public static BufferedImage ruidoGamma(BufferedImage imagenEntrada, double alpha, double beta) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double valorRuido = generarValorGamma(alpha, beta) * 20;
                int valorNuevo = limitarValor((int) (valorGris + valorRuido));
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static double generarValorGamma(double alpha, double beta) {
        if (alpha < 1) {
            return generarValorGamma(alpha + 1, beta) *
                    Math.pow(generadorAleatorio.nextDouble(), 1.0 / alpha);
        }

        double d = alpha - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9 * d);

        while (true) {
            double x, v;
            do {
                x = generadorAleatorio.nextGaussian();
                v = 1 + c * x;
            } while (v <= 0);

            v = v * v * v;
            double u = generadorAleatorio.nextDouble();

            if (u < 1 - 0.0331 * x * x * x * x) return d * v / beta;
            if (Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v)))
                return d * v / beta;
        }
    }

    // ===================== RUIDO GAUSSIANO =====================
    public static BufferedImage ruidoGaussiano(BufferedImage imagenEntrada, double media, double desviacion) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = imagenEntrada.getRGB(columna, fila) & 0xff;
                double ruido = media + desviacion * generadorAleatorio.nextGaussian();
                int nuevoValor = limitarValor((int) (valorGris + ruido));
                imagenSalida.setRGB(columna, fila, rgbGris(nuevoValor));
            }
        }
        return imagenSalida;
    }

    // ===================== RUIDO EXPONENCIAL NEGATIVO =====================
    public static BufferedImage ruidoExponencial(BufferedImage imagenEntrada, double lambda) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = imagenEntrada.getRGB(columna, fila) & 0xff;
                double ruido = -Math.log(1 - generadorAleatorio.nextDouble()) / lambda;
                int nuevoValor = limitarValor((int) (valorGris + ruido));
                imagenSalida.setRGB(columna, fila, rgbGris(nuevoValor));
            }
        }
        return imagenSalida;
    }

    // ===================== RUIDO RAYLEIGH =====================
    public static BufferedImage ruidoRayleigh(BufferedImage imagenEntrada, double sigma) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = imagenEntrada.getRGB(columna, fila) & 0xff;
                double u = generadorAleatorio.nextDouble();
                double ruido = sigma * Math.sqrt(-2 * Math.log(1 - u));
                int nuevoValor = limitarValor((int) (valorGris + ruido));
                imagenSalida.setRGB(columna, fila, rgbGris(nuevoValor));
            }
        }
        return imagenSalida;
    }

    // ===================== RUIDO SAL Y PIMIENTA =====================
    public static BufferedImage ruidoSalPimienta(BufferedImage imagenEntrada, double probabilidad) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                if (generadorAleatorio.nextDouble() < probabilidad) {
                    int v = generadorAleatorio.nextBoolean() ? 0 : 255;
                    imagenSalida.setRGB(columna, fila, rgbGris(v));
                } else {
                    imagenSalida.setRGB(columna, fila, imagenEntrada.getRGB(columna, fila));
                }
            }
        }
        return imagenSalida;
    }

    // ===================== RUIDO UNIFORME =====================
    public static BufferedImage ruidoUniforme(BufferedImage imagenEntrada, int minimo, int maximo) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = imagenEntrada.getRGB(columna, fila) & 0xff;
                int ruido = minimo + generadorAleatorio.nextInt(maximo - minimo + 1);
                int nuevoValor = limitarValor(valorGris + ruido);
                imagenSalida.setRGB(columna, fila, rgbGris(nuevoValor));
            }
        }
        return imagenSalida;
    }

    // ===================== UTILIDADES =====================
    private static int limitarValor(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static int rgbGris(int v) {
        return (0xff << 24) | (v << 16) | (v << 8) | v;
    }
    
}
