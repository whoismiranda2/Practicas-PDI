package logica;
/*
 * Esta clase aplica un suavizado gausiano a la imagen, lo que reduce el ruido de la imagen
 * "Es el primer paso del Algoritmo"
 */
public class FiltroGauss {

    private static final double[][] KERNEL = {
        {2, 4, 5, 4, 2},
        {4, 9,12, 9, 4},
        {5,12,15,12, 5},
        {4, 9,12, 9, 4},
        {2, 4, 5, 4, 2}
    };

    public static double[][] apply(double[][] img) {
        int h = img.length;
        int w = img[0].length;
        double[][] out = new double[h][w];
        double sum = 159.0;

        for (int y = 2; y < h - 2; y++) {
            for (int x = 2; x < w - 2; x++) {
                double acc = 0;
                for (int i = -2; i <= 2; i++)
                    for (int j = -2; j <= 2; j++)
                        acc += img[y + i][x + j] * KERNEL[i + 2][j + 2];

                out[y][x] = acc / sum;
            }
        }
        return out;
    }
}