package logica;
/*
 * Esta clase calcula el gradiente de la imagen, usando kernels unidimensionales
 * se obtiene la dirección y magnitud del gradiente
 */
public class Gradiente {

    public static class Gradient {
        public double[][] magnitude;
        public double[][] direction;
    }

    public static Gradient apply(double[][] img) {
        int h = img.length;
        int w = img[0].length;

        double[][] gx = new double[h][w];
        double[][] gy = new double[h][w];
        double[][] mag = new double[h][w];
        double[][] dir = new double[h][w];

        // g_x(x,y) = f(x+1,y) - f(x,y)
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w - 1; x++)
                gx[y][x] = img[y][x + 1] - img[y][x];

        // g_y(x,y) = f(x,y+1) - f(x,y)
        for (int y = 0; y < h - 1; y++)
            for (int x = 0; x < w; x++)
                gy[y][x] = img[y + 1][x] - img[y][x];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                mag[y][x] = Math.hypot(gx[y][x], gy[y][x]);
                dir[y][x] = Math.atan2(gy[y][x], gx[y][x]);
            }

        Gradient g = new Gradient();
        g.magnitude = mag;
        g.direction = dir;
        return g;
    }
}