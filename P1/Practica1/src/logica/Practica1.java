package logica;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import javax.swing.*;
import igu.Pantalla;

public class Practica1 {

    public BufferedImage extraerCanalRojo(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage rojo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = imagen.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int valor = (r << 16);
                rojo.setRGB(x, y, valor);
            }
        }
        return rojo;
    }

    public BufferedImage extraerCanalVerde(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage verde = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = imagen.getRGB(x, y);
                int g = (rgb >> 8) & 0xFF;
                int valor = (g << 8);
                verde.setRGB(x, y, valor);
            }
        }
        return verde;
    }

    public BufferedImage extraerCanalAzul(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage azul = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = imagen.getRGB(x, y);
                int b = rgb & 0xFF;
                azul.setRGB(x, y, b);
            }
        }
        return azul;
    }

    public BufferedImage crearImagenGris(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage gris = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = imagen.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int promedio = (r + g + b) / 3;
                int valor = (promedio << 16) | (promedio << 8) | promedio;
                gris.setRGB(x, y, valor);
            }
        }
        return gris;
    }

    public BufferedImage ajustarBrillo(BufferedImage original, int valor, boolean enColor) {
        BufferedImage salida = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Color c = new Color(original.getRGB(x, y));

                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();

                if (!enColor) {
                    // Escala de grises
                    int gris = (r + g + b) / 3;
                    gris = limitar(gris + valor);
                    r = g = b = gris;
                } else {
                    r = limitar(r + valor);
                    g = limitar(g + valor);
                    b = limitar(b + valor);
                }

                salida.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return salida;
    }

    public BufferedImage ajustarContraste(BufferedImage original, int valor, boolean enColor) {
        BufferedImage salida = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Fórmula de contraste
        double factor = (259 * (valor + 255.0)) / (255 * (259 - valor));

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Color c = new Color(original.getRGB(x, y));

                int r, g, b;
                if (!enColor) {
                    int gris = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                    gris = limitar((int) (factor * (gris - 128) + 128));
                    r = g = b = gris;
                } else {
                    r = limitar((int) (factor * (c.getRed() - 128) + 128));
                    g = limitar((int) (factor * (c.getGreen() - 128) + 128));
                    b = limitar((int) (factor * (c.getBlue() - 128) + 128));
                }

                salida.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return salida;
    }

    private int limitar(int val) {
        return Math.min(255, Math.max(0, val));
    }

    public static void mostrarImagen(BufferedImage imagen, String titulo) {
        JFrame ventana = new JFrame(titulo);
        JLabel label = new JLabel(new ImageIcon(imagen));
        JScrollPane scroll = new JScrollPane(label);
        ventana.add(scroll);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
    
    public static void main(String[] args) {
        Pantalla panta = new Pantalla();
        panta.setVisible(true);
        panta.setLocationRelativeTo(null);
    }
}
