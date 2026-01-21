package logica;

/*
 *Esta clase contiene los distintos kernels (máscaras) disponibles para los filtros pasabajas y pasaaltas
  Si se desea modificar los valores de los kernels, se debe hacer manualmente modificando los valores de las
  matrices dentro de esta clase
*/

public class CargarKernel {

    public static double[][] suavizado7x7() {
        return new double[][]{
            {0,0,1,1,1,0,0},
            {0,1,1,1,1,1,0},
            {1,1,1,1,1,1,1},
            {1,1,1,6,1,1,1},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0}
        };
    }

    public static double[][] suavizado9x9() {
        return new double[][]{
            {0,0,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1},
            {1,1,1,1,9,1,1,1,1},
            {1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1},
            {0,1,1,1,1,1,1,1,0},
            {0,0,1,1,1,1,1,0,0}
        };
    }

    public static double[][] suavizado11x11() {
        return new double[][]{
            {0,0,1,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,9,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1},
            {0,1,1,1,1,1,1,1,1,1,0},
            {0,0,1,1,1,1,1,1,1,0,0}
        };
    }

    public static double[][] definicionSuave() {
        return new double[][]{
            {1,-2,1},
            {-2,5,-2},
            {1,-2,1}
        };
    }

    public static double[][] definicionMedia() {
        return new double[][]{
            {0,-1,0},
            {-1,5,-1},
            {0,-1,0}
        };
    }
    
    public static double[][] definicionFuerte() {
        return new double[][]{
            {-1,-1,-1},
            {-1,9,-1},
            {-1,-1,-1}
        };
    }

    public static double[][] sobelX() {
        return new double[][]{
            {1,0,-1},
            {2,0,-2},
            {1,0,-1}
        };
    }

    public static double[][] sobelY() {
        return new double[][]{
            {-1,-2,-1},
            {0,0,0},
            {1,2,1}
        };
    }

    public static double[][] prewittX() {
        return new double[][]{
            {1,0,-1},
            {1,0,-1},
            {1,0,-1}
        };
    }

    public static double[][] prewittY() {
        return new double[][]{
            {-1,-1,-1},
            {0,0,0},
            {1,1,1}
        };
    }
    
    public static double[][] robertsX() {
        return new double[][]{
            {0,0,-1},
            {0,1,0},
            {0,0,0}
        };
    }

    public static double[][] robertsY() {
        return new double[][]{
            {-1,0,0},
            {0,1,0},
            {0,0,0}
        };
    }

    public static double[][] freiChenX() {
        double s = Math.sqrt(2);
        return new double[][]{
            {1,0,-1},
            {s,0,-s},
            {1,0,-1}
        };
    }

    public static double[][] freiChenY() {
        double s = Math.sqrt(2);
        return new double[][]{
            {-1,-s,-1},
            { 0, 0, 0},
            { 1, s, 1}
        };
    }
    public static double[][] laplacian() {
        return new double[][]{
            {0,-1,0},
            {-1,4,-1},
            {0,-1,0}
        };
    }
    
    

}