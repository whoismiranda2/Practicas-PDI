package logica;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el algoritno de esqueletizado de Zhang–Suen
 * resulta en un dibujo de un píxel de ancho de la silueta blanca de la imagen
 * 
*/

public class Esqueletizado {

    public static BufferedImage esqueleto(BufferedImage img) {

        int w = img.getWidth();
        int h = img.getHeight();
        int[][] p = new int[h][w];

        // leer imagen
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                p[y][x] = (img.getRGB(x,y) & 0xff) == 255 ? 1 : 0;

        boolean cambio;
        do {
            cambio = false;
            List<int[]> aMover = new ArrayList<>();

            // Paso 1
            for (int y = 1; y < h-1; y++) {
                for (int x = 1; x < w-1; x++) {
                    int P = p[y][x];
                    if (P != 1) continue;

                    int[] nb = vecinos(p, x, y);
                    int bp = transicion(nb);
                    int count = sum(nb);

                    if (count >= 2 && count <= 6 &&
                        bp == 1 &&
                        nb[0]*nb[2]*nb[4] == 0 &&
                        nb[2]*nb[4]*nb[6] == 0) {

                        aMover.add(new int[]{x,y});
                    }
                }
            }

            if (!aMover.isEmpty()) cambio = true;
            for (int[] pix : aMover) p[pix[1]][pix[0]] = 0;
            aMover.clear();

            // Paso 2
            for (int y = 1; y < h-1; y++) {
                for (int x = 1; x < w-1; x++) {
                    int P = p[y][x];
                    if (P != 1) continue;

                    int[] nb = vecinos(p, x, y);
                    int bp = transicion(nb);
                    int count = sum(nb);

                    if (count >= 2 && count <= 6 &&
                        bp == 1 &&
                        nb[0]*nb[2]*nb[6] == 0 &&
                        nb[0]*nb[4]*nb[6] == 0) {

                        aMover.add(new int[]{x,y});
                    }
                }
            }

            for (int[] pix : aMover) p[pix[1]][pix[0]] = 0;

        } while (cambio);

        // convertir de vuelta
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                out.setRGB(x, y, p[y][x] == 1 ? 0xffffff : 0);

        return out;
    }

    private static int[] vecinos(int[][] p, int x, int y) {
        return new int[]{
            p[y-1][x],   // P2
            p[y-1][x+1], // P3
            p[y][x+1],   // P4
            p[y+1][x+1], // P5
            p[y+1][x],   // P6
            p[y+1][x-1], // P7
            p[y][x-1],   // P8
            p[y-1][x-1]  // P9
        };
    }

    private static int transicion(int[] nb) {
        int count = 0;
        for (int i = 0; i < 7; i++)
            if (nb[i] == 0 && nb[i+1] == 1) count++;
        if (nb[7] == 0 && nb[0] == 1) count++;
        return count;
    }

    private static int sum(int[] nb) {
        int s = 0;
        for (int v : nb) s += v;
        return s;
    }
}