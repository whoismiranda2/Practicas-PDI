package logica;
/*
 * Clase que implementa la supresión no máxima, sirve para delgazar los bordes
 * compara cada píxel con sus vecinos en la dirección del gradiente
 */
public class NonMaxSuppression {

    public static double[][] apply(double[][] mag, double[][] dir) {
        int h = mag.length;
        int w = mag[0].length;
        double[][] out = new double[h][w];

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {

                double angle = dir[y][x] * 180 / Math.PI;
                angle = (angle + 180) % 180;

                double q = 0, r = 0;

                if (angle < 22.5 || angle >= 157.5) {
                    q = mag[y][x + 1];
                    r = mag[y][x - 1];
                } else if (angle < 67.5) {
                    q = mag[y + 1][x - 1];
                    r = mag[y - 1][x + 1];
                } else if (angle < 112.5) {
                    q = mag[y + 1][x];
                    r = mag[y - 1][x];
                } else {
                    q = mag[y - 1][x - 1];
                    r = mag[y + 1][x + 1];
                }

                if (mag[y][x] >= q && mag[y][x] >= r)
                    out[y][x] = mag[y][x];
            }
        }
        return out;
    }
}
