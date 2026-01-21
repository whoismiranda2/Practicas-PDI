package logica;

/**
 *Aquí se implementa la transformada de fourier y su inversa
 */

public class Fourier {
    public static Complex[][] dft2D(double[][] img) {
        int M = img.length;
        int N = img[0].length;
        Complex[][] F = new Complex[M][N];

        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                double sumRe = 0, sumIm = 0;
                for (int x = 0; x < M; x++) {
                    for (int y = 0; y < N; y++) {
                        double angle = -2 * Math.PI *
                            ((double)u*x/M + (double)v*y/N);
                        sumRe += img[x][y] * Math.cos(angle);
                        sumIm += img[x][y] * Math.sin(angle);
                    }
                }
                F[u][v] = new Complex(sumRe, sumIm);
            }
        }
        return F;
    }
    
    public static double[][] idft2D(Complex[][] F) {
        int M = F.length;
        int N = F[0].length;
        double[][] img = new double[M][N];

        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                double sum = 0;
                for (int u = 0; u < M; u++) {
                    for (int v = 0; v < N; v++) {
                        double angle = 2 * Math.PI *
                            ((double)u*x/M + (double)v*y/N);
                        sum += F[u][v].re * Math.cos(angle)
                             - F[u][v].im * Math.sin(angle);
                    }
                }
                img[x][y] = sum / (M * N);
            }
        }
        return img;
    }
}
