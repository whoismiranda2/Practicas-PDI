package logica;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class FiltrosNoLineales {

    // =========================================================
    // ===================== UTILIDADES ========================
    // =========================================================

    private static int limitar(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static int rgbGris(int v) {
        return (0xff << 24) | (v << 16) | (v << 8) | v;
    }

    private static int[] obtenerMascara(BufferedImage img, int x, int y, int k) {
        int tam = (2 * k + 1);
        int[] m = new int[tam * tam];
        int idx = 0;

        for (int j = -k; j <= k; j++) {
            for (int i = -k; i <= k; i++) {
                int px = Math.min(Math.max(x + i, 0), img.getWidth() - 1);
                int py = Math.min(Math.max(y + j, 0), img.getHeight() - 1);
                m[idx++] = img.getRGB(px, py) & 0xff;
            }
        }
        return m;
    }

    // =========================================================
    // ===================== MEDIA ARITMÉTICA ==================
    // =========================================================
    // Ecuación (3.27)

    public static BufferedImage mediaAritmetica(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;
        int n = tam * tam;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                int suma = 0;
                for (int v : m) suma += v;
                int media = suma / n;
                out.setRGB(x, y, rgbGris(media));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== MEDIANA ===========================
    // =========================================================
    // Ecuación (3.29)

    public static BufferedImage mediana(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                Arrays.sort(m);
                out.setRGB(x, y, rgbGris(m[m.length / 2]));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== MÁXIMO ============================
    // =========================================================
    // Ecuación (3.13)

    public static BufferedImage maximo(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                int max = 0;
                for (int v : m) max = Math.max(max, v);
                out.setRGB(x, y, rgbGris(max));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== MÍNIMO ============================
    // =========================================================
    // Ecuación (3.15)

    public static BufferedImage minimo(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                int min = 255;
                for (int v : m) min = Math.min(min, v);
                out.setRGB(x, y, rgbGris(min));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== PUNTO MEDIO ======================
    // =========================================================
    // Ecuación (3.17)

    public static BufferedImage puntoMedio(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                int min = 255, max = 0;
                for (int v : m) {
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
                out.setRGB(x, y, rgbGris((min + max) / 2));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== ALFA TRIMMED =====================
    // =========================================================
    // Ecuaciones (3.10) – (3.11)

    public static BufferedImage alfaTrimmed(BufferedImage img, int tam, int P) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                Arrays.sort(m);

                int suma = 0;
                int cont = 0;
                for (int i = P; i < m.length - P; i++) {
                    suma += m[i];
                    cont++;
                }
                out.setRGB(x, y, rgbGris(suma / cont));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== ARMÓNICO =========================
    // =========================================================
    // Ecuaciones (3.18) – (3.19)

    public static BufferedImage armonico(BufferedImage img, int tam) {

        int ancho = img.getWidth();
        int alto = img.getHeight();
        int offset = tam / 2;

        BufferedImage salida = new BufferedImage(
                ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = offset; y < alto - offset; y++) {
            for (int x = offset; x < ancho - offset; x++) {

                double suma = 0.0;
                int contador = 0;

                for (int j = -offset; j <= offset; j++) {
                    for (int i = -offset; i <= offset; i++) {

                        int valor = img.getRaster().getSample(x + i, y + j, 0);

                        //para evitar la división entre cero
                        if (valor > 0) {
                            suma += 1.0 / valor;
                            contador++;
                        }
                    }
                }

                int nuevoValor;

                if (contador > 0 && suma > 0) {
                    nuevoValor = (int) ((double) contador / suma);
                } else {
                    nuevoValor = 0;
                }

                nuevoValor = Math.max(0, Math.min(255, nuevoValor));
                salida.getRaster().setSample(x, y, 0, nuevoValor);
            }
        }

        return salida;
    }


    // =========================================================
    // ===================== CONTRA ARMÓNICO ==================
    // =========================================================
    // Ecuaciones (3.20) – (3.22)

    public static BufferedImage contraArmonico(BufferedImage img, int tam, double Q) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                double num = 0.0, den = 0.0;

                for (int v : m) {
                    num += Math.pow(v, Q + 1);
                    den += Math.pow(v, Q);
                }
                int r = (int) (num / den);
                out.setRGB(x, y, rgbGris(limitar(r)));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== GEOMÉTRICO =======================
    // =========================================================
    // Ecuación (3.23)

    public static BufferedImage geometrico(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;
        int n = tam * tam;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                double prod = 1.0;
                for (int v : m) {
                    prod *= Math.max(v, 1);
                }
                int g = (int) Math.pow(prod, 1.0 / n);
                out.setRGB(x, y, rgbGris(limitar(g)));
            }
        }
        return out;
    }

    // =========================================================
    // ===================== MÁXIMO – MÍNIMO ==================
    // =========================================================
    // Ecuaciones (3.25) – (3.26)

    public static BufferedImage maximoMinimo(BufferedImage img, int tam) {
        BufferedImage out = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int k = tam / 2;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int[] m = obtenerMascara(img, x, y, k);
                int min = 255, max = 0;

                for (int v : m) {
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }

                int centro = img.getRGB(x, y) & 0xff;
                int dMax = Math.abs(max - centro);
                int dMin = Math.abs(centro - min);

                out.setRGB(x, y, rgbGris(dMax <= dMin ? max : min));
            }
        }
        return out;
    }
}