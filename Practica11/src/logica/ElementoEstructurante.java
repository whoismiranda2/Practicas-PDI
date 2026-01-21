package logica;

/**
 * Aquí se definen los elementos estructurantes posibles para usar
 * Diamante 5x5
 * Diamante 7x7
 * Disco 7x7
 * así como lo necesario para emplearlos
 * Si se deseara modificar el elemento estructurante, se deben modificar manualmente los mencionados anteriormente
 */

public class ElementoEstructurante {

    private int[][] mask;
    private int size;
    private int center;

    public ElementoEstructurante(int[][] mask) {
        this.mask = mask;
        this.size = mask.length;
        this.center = size / 2;
    }

    public int[][] getMask() {
        return mask;
    }

    public int getSize() {
        return size;
    }

    public int getCenter() {
        return center;
    }

    //EE
    public static ElementoEstructurante diamante5x5() {
        return new ElementoEstructurante(new int[][]{
            {0,0,1,0,0},
            {0,1,1,1,0},
            {1,1,1,1,1},
            {0,1,1,1,0},
            {0,0,1,0,0}
        });
    }

    public static ElementoEstructurante diamante7x7() {
        return new ElementoEstructurante(new int[][]{
            {0,0,0,1,0,0,0},
            {0,0,1,1,1,0,0},
            {0,1,1,1,1,1,0},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0},
            {0,0,0,1,0,0,0}
        });
    }

    public static ElementoEstructurante disco7x7() {
        return new ElementoEstructurante(new int[][]{
            {0,0,1,1,1,0,0},
            {0,1,1,1,1,1,0},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0}
        });
    }
}