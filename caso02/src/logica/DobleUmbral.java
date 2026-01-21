package logica;
/*
 * Clase que aplica un doble umbral y la histéresis
 * determina si es borde débil, borde fuerte o no borde
 * y la histéresis permite conservar bordes débiles únicamente si están conectados a bordes fuertes
 */
public class DobleUmbral {

    public static double[][] apply(double[][] img, double low, double high) {
        int h = img.length;
        int w = img[0].length;
        double[][] out = new double[h][w];

        for (int y = 1; y < h - 1; y++)
            for (int x = 1; x < w - 1; x++) {
                if (img[y][x] >= high)
                    out[y][x] = 255;
                else if (img[y][x] >= low)
                    out[y][x] = 128;
            }

        // Histéresis
        for (int y = 1; y < h - 1; y++)
            for (int x = 1; x < w - 1; x++)
                if (out[y][x] == 128)
                    for (int i = -1; i <= 1; i++)
                        for (int j = -1; j <= 1; j++)
                            if (out[y + i][x + j] == 255)
                                out[y][x] = 255;

        return out;
    }
}
