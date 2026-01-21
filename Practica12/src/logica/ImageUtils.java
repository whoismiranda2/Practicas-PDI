package logica;

import java.awt.image.BufferedImage;

/**
 * Clase que contiene metodos auxiliares para manejar la imagen y el filtrado
 * Convierte la imagen a niveles de grises
 * Permite convertir una imagen a matriz y viceversa con el propósito de poder trabajar con Fourier y posterior al
 * cálculo, recuperar la imagen resultante
 */

public class ImageUtils {

    //a escala de grises
    public static BufferedImage toGray(BufferedImage img) {
        BufferedImage gray = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int grayVal = (r + g + b) / 3;
                int gRGB = (grayVal << 16) | (grayVal << 8) | grayVal;
                gray.setRGB(x, y, gRGB);
            }
        return gray;
    }

    //imagen a matriz
    public static double[][] imageToMatrix(BufferedImage img) {
        double[][] m = new double[img.getWidth()][img.getHeight()];

        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++) {
                double val = img.getRGB(x, y) & 0xff;
                //centrar
                m[x][y] = val * Math.pow(-1, x + y);
            }

        return m;
    }


    //matriz a imagen
    public static BufferedImage matrixToImage(double[][] m) {
        int M = m.length;
        int N = m[0].length;
        BufferedImage img = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);

        double max = 0;
        for (double[] row : m)
            for (double v : row)
                max = Math.max(max, Math.abs(v));

        for (int x = 0; x < M; x++)
            for (int y = 0; y < N; y++) {
                int val = (int) (255 * Math.abs(m[x][y]) / max);
                int rgb = (val << 16) | (val << 8) | val;
                img.setRGB(x, y, rgb);
            }
        return img;
    }

    //magnitud
    public static BufferedImage magnitudeImage(Complex[][] F) {
        int M = F.length;
        int N = F[0].length;
        BufferedImage img = new BufferedImage(M, N, BufferedImage.TYPE_BYTE_GRAY);

        double max = 0;
        double[][] mag = new double[M][N];

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++) {
                mag[i][j] = Math.log(1 + F[i][j].magnitude());
                max = Math.max(max, mag[i][j]);
            }

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++) {
                int val = (int) (255 * mag[i][j] / max);
                int rgb = (val << 16) | (val << 8) | val;
                img.setRGB(i, j, rgb);
            }

        return img;
    }
}