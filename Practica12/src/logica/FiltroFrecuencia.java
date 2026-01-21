package logica;

/*
 Clase en donde se implementa y aplica el filtro que seleccioné (el gaussiano pasabajas)
*/

public class FiltroFrecuencia {

    //filtro gaussiano
    public static double[][] gaussiano(int M, int N, double sigma) {
        double[][] H = new double[M][N];
        int u0 = M / 2;
        int v0 = N / 2;

        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                double D = Math.sqrt(Math.pow(u - u0, 2) + Math.pow(v - v0, 2));
                H[u][v] = Math.exp(-(D * D) / (2 * sigma * sigma));
            }
        }
        return H;
    }

    //aplicar el filtro
    public static Complex[][] aplicar(Complex[][] F, double[][] H) {
        int M = F.length;
        int N = F[0].length;
        Complex[][] G = new Complex[M][N];

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                G[i][j] = new Complex(
                        F[i][j].re * H[i][j],
                        F[i][j].im * H[i][j]
                );

        return G;
    }
}