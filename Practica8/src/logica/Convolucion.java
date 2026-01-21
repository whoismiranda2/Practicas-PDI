package logica;

import java.awt.image.BufferedImage;

/*
 *Clase en donde se implementa la convolución
  Se emplea para los demás filtros
  Implementa una normalización para evitar mascaras no normalizadas que arrojen valores superiores a 255
*/

public class Convolucion {

    public static BufferedImage apply(BufferedImage img, double[][] kernel) {
        int w = img.getWidth();
        int h = img.getHeight();
        int k = kernel.length / 2;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        
        //NORMALIZACIÓN (solo si suma != 0) (usado para el suavizado)
        double sum = 0;
        for (double[] row : kernel)
            for (double v : row)
                sum += v;

        if (sum != 0) {
            for (int i = 0; i < kernel.length; i++)
                for (int j = 0; j < kernel[0].length; j++)
                    kernel[i][j] /= sum;
        }
        
        for (int y = k; y < h - k; y++) {
            for (int x = k; x < w - k; x++) {
                sum = 0;
                for (int j = -k; j <= k; j++)
                    for (int i = -k; i <= k; i++)
                        sum += (img.getRGB(x+i,y+j)&0xFF) * kernel[j+k][i+k];

                int v = clamp((int)Math.abs(sum));
                out.setRGB(x,y,(v<<16)|(v<<8)|v);
            }
        }
        return out;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}