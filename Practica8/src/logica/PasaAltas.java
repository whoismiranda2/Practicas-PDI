package logica;

import java.awt.image.BufferedImage;

/*
 *Clase que implementa los filtros pasaaltas con los diferentes métodos vistos
*/

public class PasaAltas {

    public static BufferedImage homogeneity(BufferedImage img) {
        int w=img.getWidth(), h=img.getHeight();
        BufferedImage out=new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);

        for(int y=1;y<h-1;y++)
            for(int x=1;x<w-1;x++){
                int c=img.getRGB(x,y)&0xFF, max=0;
                for(int j=-1;j<=1;j++)
                    for(int i=-1;i<=1;i++)
                        if(i!=0||j!=0)
                            max=Math.max(max,Math.abs(c-(img.getRGB(x+i,y+j)&0xFF)));
                out.setRGB(x,y,(max<<16)|(max<<8)|max);
            }
        return out;
    }

    public static BufferedImage difference(BufferedImage img) {
        int w=img.getWidth(), h=img.getHeight();
        BufferedImage out=new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);

        for(int y=1;y<h-1;y++)
            for(int x=1;x<w-1;x++){
                int c=img.getRGB(x,y)&0xFF;
                int d=Math.max(
                    Math.abs(c-(img.getRGB(x-1,y)&0xFF)),
                    Math.abs(c-(img.getRGB(x,y+1)&0xFF))
                );
                out.setRGB(x,y,(d<<16)|(d<<8)|d);
            }
        return out;
    }

    public static BufferedImage sobelOR(BufferedImage img) {
        return ImageUtils.or(
            Convolucion.apply(img, CargarKernel.sobelX()),
            Convolucion.apply(img, CargarKernel.sobelY())
        );
    }

    public static BufferedImage prewittOR(BufferedImage img) {
        return ImageUtils.or(
            Convolucion.apply(img, CargarKernel.prewittX()),
            Convolucion.apply(img, CargarKernel.prewittY())
        );
    }

    public static BufferedImage laplacian(BufferedImage img) {
        return Convolucion.apply(img, CargarKernel.laplacian());
    }
    
    public static BufferedImage robertsOR(BufferedImage img) {
        BufferedImage gx = Convolucion.apply(img, CargarKernel.robertsX());
        BufferedImage gy = Convolucion.apply(img, CargarKernel.robertsY());
        return ImageUtils.or(gx, gy);
    }

    public static BufferedImage freiChenOR(BufferedImage img) {
        BufferedImage gx = Convolucion.apply(img, CargarKernel.freiChenX());
        BufferedImage gy = Convolucion.apply(img, CargarKernel.freiChenY());
        return ImageUtils.or(gx, gy);
    }

    public static BufferedImage gradienteCompas(
        BufferedImage img, double[][][] masks) {

        BufferedImage result = Convolucion.apply(img, masks[0]);

        for (int i = 1; i < masks.length; i++) {
            BufferedImage temp = Convolucion.apply(img, masks[i]);
            result = ImageUtils.or(result, temp);
        }
        return result;
    }
    
    public static BufferedImage prewittCompas(BufferedImage img) {
        return gradienteCompas(img, CompasM.prewitt());
    }

    public static BufferedImage kirschCompas(BufferedImage img) {
        return gradienteCompas(img, CompasM.kirsch());
    }
    
    public static BufferedImage robinson3Compas(BufferedImage img) {
        return gradienteCompas(img, CompasM.robinson3());
    }
    
    public static BufferedImage robinson5Compas(BufferedImage img) {
        return gradienteCompas(img, CompasM.robinson5());
    }
    
    public static BufferedImage[] aplicarMultiples(
        BufferedImage img, double[][][] masks) {

        BufferedImage[] results = new BufferedImage[masks.length];

        for (int i = 0; i < masks.length; i++) {
            results[i] = Convolucion.apply(img, masks[i]);
        }
        return results;
    }

    public static BufferedImage[] prewittOrientaciones(BufferedImage img) {
        return aplicarMultiples(img, CompasM.prewitt());
    }

    public static BufferedImage[] kirschOrientaciones(BufferedImage img) {
        return aplicarMultiples(img, CompasM.kirsch());
    }
    
    public static BufferedImage[] robinson3Orientaciones(BufferedImage img) {
        return aplicarMultiples(img, CompasM.robinson3());
    }
    
    public static BufferedImage[] robinson5Orientaciones(BufferedImage img) {
        return aplicarMultiples(img, CompasM.robinson5());
    }
}