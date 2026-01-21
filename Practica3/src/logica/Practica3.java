package logica;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;

public class Practica3 {

    private BufferedImage imagenOriginal;
    private BufferedImage imagenProcesada;

    public Practica3(BufferedImage original) {
        this.imagenOriginal = original;
        this.imagenProcesada = original;
    }

    public BufferedImage getImagenProcesada() {
        return imagenProcesada;
    }

    public void setImagenProcesada(BufferedImage img) {
        this.imagenProcesada = img;
    }

    public BufferedImage getImagenOriginal() {
        return imagenOriginal;
    }

    //RGB --> CMY
    public BufferedImage convertirRGBaCMY() {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();
        BufferedImage cmy = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenOriginal.getRGB(x, y));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();

                int C = 255 - r;
                int M = 255 - g;
                int Y = 255 - b;

                cmy.setRGB(x, y, new Color(C, M, Y).getRGB());
            }
        }
        this.imagenProcesada = cmy;
        return cmy;
    }

    //CMY --> RGB
    public BufferedImage convertirCMYaRGB() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));
                int C = c.getRed();
                int M = c.getGreen();
                int Y = c.getBlue();

                int R = 255 - C;
                int G = 255 - M;
                int B = 255 - Y;

                rgb.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = rgb;
        return rgb;
    }

    //CMY --> CMYK
    public BufferedImage convertirCMYaCMYK() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage cmyk = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));
                double C = c.getRed() / 255.0;
                double M = c.getGreen() / 255.0;
                double Y = c.getBlue() / 255.0;
                //calcular K
                double K = Math.min(C, Math.min(M, Y));

                double C_ = (K < 1.0) ? (C - K) / (1 - K) : 0;
                double M_ = (K < 1.0) ? (M - K) / (1 - K) : 0;
                double Y_ = (K < 1.0) ? (Y - K) / (1 - K) : 0;

                int R = (int)(C_ * 255);
                int G = (int)(M_ * 255);
                int B = (int)(Y_ * 255);

                int kGray = (int)(K * 255);
                Color cmykColor = new Color(R, G, kGray);

                cmyk.setRGB(x, y, cmykColor.getRGB());
            }
        }
        this.imagenProcesada = cmyk;
        return cmyk;
    }
    
    //RGB --> YIQ
    public BufferedImage convertirRGBaYIQ() {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();
        BufferedImage yiq = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenOriginal.getRGB(x, y));
                double r = c.getRed() / 255.0;
                double g = c.getGreen() / 255.0;
                double b = c.getBlue() / 255.0;

                double Y = 0.299 * r + 0.587 * g + 0.114 * b;
                double I = 0.596 * r - 0.274 * g - 0.322 * b;
                double Q = 0.211 * r - 0.523 * g + 0.312 * b;

                int R = (int)(Math.min(1, Math.max(0, Y)) * 255);
                int G = (int)(Math.min(1, Math.max(0, I + 0.5)) * 255);
                int B = (int)(Math.min(1, Math.max(0, Q + 0.5)) * 255);

                yiq.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = yiq;
        return yiq;
    }

    //YIQ --> RGB
    public BufferedImage convertirYIQaRGB() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));
                double Y = c.getRed() / 255.0;
                double I = (c.getGreen() / 255.0) - 0.5;
                double Q = (c.getBlue() / 255.0) - 0.5;

                double r = Y + 0.956 * I + 0.621 * Q;
                double g = Y - 0.272 * I - 0.647 * Q;
                double b = Y - 1.106 * I + 1.703 * Q;

                int R = (int)Math.min(255, Math.max(0, r * 255));
                int G = (int)Math.min(255, Math.max(0, g * 255));
                int B = (int)Math.min(255, Math.max(0, b * 255));

                rgb.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = rgb;
        return rgb;
    }

    //RGB --> HSI
    public BufferedImage convertirRGBaHSI() {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();
        BufferedImage hsi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenOriginal.getRGB(x, y));
                double r = c.getRed() / 255.0;
                double g = c.getGreen() / 255.0;
                double b = c.getBlue() / 255.0;

                double num = 0.5 * ((r - g) + (r - b));
                double den = Math.sqrt((r - g) * (r - g) + (r - b) * (g - b));
                double theta = Math.acos(num / (den + 1e-9));

                double H = (b <= g) ? theta : (2 * Math.PI - theta);
                double S = 1 - (3 / (r + g + b + 1e-9)) * Math.min(r, Math.min(g, b));
                double I = (r + g + b) / 3.0;

                int R = (int)(H / (2 * Math.PI) * 255);
                int G = (int)(S * 255);
                int B = (int)(I * 255);

                hsi.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = hsi;
        return hsi;
    }

    //HSI --> RGB
    public BufferedImage convertirHSIaRGB() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));
                double H = (c.getRed() / 255.0) * 2 * Math.PI;
                double S = c.getGreen() / 255.0;
                double I = c.getBlue() / 255.0;

                double r=0,g=0,b=0;

                if (H < 2 * Math.PI / 3) {
                    b = I * (1 - S);
                    r = I * (1 + (S * Math.cos(H)) / Math.cos(Math.PI / 3 - H));
                    g = 3 * I - (r + b);
                } else if (H < 4 * Math.PI / 3) {
                    H = H - 2 * Math.PI / 3;
                    r = I * (1 - S);
                    g = I * (1 + (S * Math.cos(H)) / Math.cos(Math.PI / 3 - H));
                    b = 3 * I - (r + g);
                } else {
                    H = H - 4 * Math.PI / 3;
                    g = I * (1 - S);
                    b = I * (1 + (S * Math.cos(H)) / Math.cos(Math.PI / 3 - H));
                    r = 3 * I - (g + b);
                }

                int R = (int)Math.min(255, Math.max(0, r * 255));
                int G = (int)Math.min(255, Math.max(0, g * 255));
                int B = (int)Math.min(255, Math.max(0, b * 255));

                rgb.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = rgb;
        return rgb;
    }

    //RGB --> HSV
    public BufferedImage convertirRGBaHSV() {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();
        BufferedImage hsvImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(imagenOriginal.getRGB(x, y));
                float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

                int H = (int)(hsv[0] * 255);
                int S = (int)(hsv[1] * 255);
                int V = (int)(hsv[2] * 255);

                hsvImg.setRGB(x, y, new Color(H, S, V).getRGB());
            }
        }
        this.imagenProcesada = hsvImg;
        return hsvImg;
    }

    //HSV --> RGB
    public BufferedImage convertirHSVaRGB() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage rgbImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));
                float H = c.getRed() / 255f;
                float S = c.getGreen() / 255f;
                float V = c.getBlue() / 255f;

                int rgb = Color.HSBtoRGB(H, S, V);
                rgbImg.setRGB(x, y, rgb);
            }
        }
        this.imagenProcesada = rgbImg;
        return rgbImg;
    }

    //RGB --> lab
    public BufferedImage convertirRGBaLab() {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();
        BufferedImage labImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //RGB ---> LMS
        double[][] rgb2lms = {
            {0.3811, 0.5783, 0.0402},
            {0.1967, 0.7244, 0.0782},
            {0.0241, 0.1288, 0.8444}
        };

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenOriginal.getRGB(x, y));
                double r = c.getRed() / 255.0;
                double g = c.getGreen() / 255.0;
                double b = c.getBlue() / 255.0;

                //RGB ---> LMS
                double L = rgb2lms[0][0]*r + rgb2lms[0][1]*g + rgb2lms[0][2]*b;
                double M = rgb2lms[1][0]*r + rgb2lms[1][1]*g + rgb2lms[1][2]*b;
                double S = rgb2lms[2][0]*r + rgb2lms[2][1]*g + rgb2lms[2][2]*b;

                // Evitar log(0)
                L = Math.max(L, 1e-9);
                M = Math.max(M, 1e-9);
                S = Math.max(S, 1e-9);

                double lLog = Math.log10(L);
                double mLog = Math.log10(M);
                double sLog = Math.log10(S);

                //LMS ---> lab
                double Lp = (1.0/Math.sqrt(3)) * (lLog + mLog + sLog);
                double alpha = (1.0/Math.sqrt(6)) * (lLog + mLog - 2*sLog);
                double beta  = (1.0/Math.sqrt(2)) * (lLog - mLog);

                //limitar
                int R = (int)(Math.min(1, Math.max(0, (Lp + 1) / 2.0)) * 255);
                int G = (int)(Math.min(1, Math.max(0, (alpha + 1) / 2.0)) * 255);
                int B = (int)(Math.min(1, Math.max(0, (beta + 1) / 2.0)) * 255);

                labImg.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = labImg;
        return labImg;
    }

    //lab --> RGB
    public BufferedImage convertirLabaRGB() {
        int w = imagenProcesada.getWidth();
        int h = imagenProcesada.getHeight();
        BufferedImage rgbImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //LMS ---> RGB
        double[][] lms2rgb = {
            { 4.4679, -3.5873,  0.1193},
            {-1.2186,  2.3809, -0.1624},
            { 0.0497, -0.2439,  1.2045}
        };

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(imagenProcesada.getRGB(x, y));

                //lab
                double Lp = (c.getRed() / 255.0) * 2 - 1;
                double alpha = (c.getGreen() / 255.0) * 2 - 1;
                double beta  = (c.getBlue() / 255.0) * 2 - 1;

                //lab ---> LMS
                double lLog = (1/Math.sqrt(3.0))*Lp + (1/Math.sqrt(6.0))*alpha + (1/Math.sqrt(2.0))*beta;
                double mLog = (1/Math.sqrt(3.0))*Lp + (1/Math.sqrt(6.0))*alpha - (1/Math.sqrt(2.0))*beta;
                double sLog = (1/Math.sqrt(3.0))*Lp - (2/Math.sqrt(6.0))*alpha;

                //regresar a LMS
                double L = Math.pow(10, lLog);
                double M = Math.pow(10, mLog);
                double S = Math.pow(10, sLog);

                //LMS ---> RGB
                double r = lms2rgb[0][0]*L + lms2rgb[0][1]*M + lms2rgb[0][2]*S;
                double g = lms2rgb[1][0]*L + lms2rgb[1][1]*M + lms2rgb[1][2]*S;
                double b = lms2rgb[2][0]*L + lms2rgb[2][1]*M + lms2rgb[2][2]*S;

                int R = (int)Math.min(255, Math.max(0, r * 255));
                int G = (int)Math.min(255, Math.max(0, g * 255));
                int B = (int)Math.min(255, Math.max(0, b * 255));

                rgbImg.setRGB(x, y, new Color(R, G, B).getRGB());
            }
        }
        this.imagenProcesada = rgbImg;
        return rgbImg;
    }

    //transferencia de color entre imagenes
    public BufferedImage transferirColor(BufferedImage referencia) {
        int w = imagenOriginal.getWidth();
        int h = imagenOriginal.getHeight();

        //a lab
        double[][][] labOriginal = convertirRGBaLabArray(imagenOriginal);
        double[][][] labReferencia = convertirRGBaLabArray(referencia);

        //medias y desviaciones por canal
        double[] meanO = calcularMedia(labOriginal);
        double[] stdO  = calcularDesv(labOriginal, meanO);

        double[] meanR = calcularMedia(labReferencia);
        double[] stdR  = calcularDesv(labReferencia, meanR);

        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); //crear imagen resultado

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double L = labOriginal[y][x][0];
                double a = labOriginal[y][x][1];
                double b = labOriginal[y][x][2];

                L = (L - meanO[0]) / stdO[0];
                a = (a - meanO[1]) / stdO[1];
                b = (b - meanO[2]) / stdO[2];

                L = L * stdR[0] + meanR[0];
                a = a * stdR[1] + meanR[1];
                b = b * stdR[2] + meanR[2];
                //lab --> RGB
                Color rgb = LabToRGB(L, a, b);
                result.setRGB(x, y, rgb.getRGB());
            }
        }

        this.imagenProcesada = result;
        return result;
    }

    //utilidades
    private double[][][] convertirRGBaLabArray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        double[][][] lab = new double[h][w][3];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                double[] labPix = RGBToLab(c.getRed(), c.getGreen(), c.getBlue());
                lab[y][x][0] = labPix[0];
                lab[y][x][1] = labPix[1];
                lab[y][x][2] = labPix[2];
            }
        }
        return lab;
    }

    private double[] calcularMedia(double[][][] lab) {
        double L=0,a=0,b=0;
        int h = lab.length, w = lab[0].length;
        int n = w*h;

        for (int y=0;y<h;y++){
            for(int x=0;x<w;x++){
                L += lab[y][x][0];
                a += lab[y][x][1];
                b += lab[y][x][2];
            }
        }
        return new double[]{L/n, a/n, b/n};
    }

    private double[] calcularDesv(double[][][] lab, double[] mean) {
        double L=0,a=0,b=0;
        int h = lab.length, w = lab[0].length;
        int n = w*h;

        for (int y=0;y<h;y++){
            for(int x=0;x<w;x++){
                L += Math.pow(lab[y][x][0]-mean[0],2);
                a += Math.pow(lab[y][x][1]-mean[1],2);
                b += Math.pow(lab[y][x][2]-mean[2],2);
            }
        }
        return new double[]{
            Math.sqrt(L/n),
            Math.sqrt(a/n),
            Math.sqrt(b/n)
        };
    }

    //conversion auxiliar
    private double[] RGBToLab(int R, int G, int B) {
        double r = R/255.0, g = G/255.0, b = B/255.0;

        r = (r>0.04045)?Math.pow((r+0.055)/1.055,2.4):r/12.92;
        g = (g>0.04045)?Math.pow((g+0.055)/1.055,2.4):g/12.92;
        b = (b>0.04045)?Math.pow((b+0.055)/1.055,2.4):b/12.92;

        double X = r*0.4124+g*0.3576+b*0.1805;
        double Y = r*0.2126+g*0.7152+b*0.0722;
        double Z = r*0.0193+g*0.1192+b*0.9505;

        double Xn=0.95047,Yn=1.0,Zn=1.08883;
        double fx=fLab(X/Xn), fy=fLab(Y/Yn), fz=fLab(Z/Zn);

        double L=116*fy-16;
        double a=500*(fx-fy);
        double bb=200*(fy-fz);
        return new double[]{L,a,bb};
    }

    private double fLab(double t){
        return (t>0.008856)?Math.cbrt(t):(7.787*t+16.0/116.0);
    }

    private Color LabToRGB(double L,double a,double b){
        double fy=(L+16)/116.0;
        double fx=fy+(a/500.0);
        double fz=fy-(b/200.0);

        double Xn=0.95047,Yn=1.0,Zn=1.08883;
        double X=Xn*fInvLab(fx);
        double Y=Yn*fInvLab(fy);
        double Z=Zn*fInvLab(fz);

        double r= X*3.2406 + Y*(-1.5372) + Z*(-0.4986);
        double g= X*(-0.9689)+ Y*(1.8758) + Z*(0.0415);
        double bb= X*(0.0557)+ Y*(-0.2040)+ Z*(1.0570);

        r=(r>0.0031308)?(1.055*Math.pow(r,1/2.4)-0.055):12.92*r;
        g=(g>0.0031308)?(1.055*Math.pow(g,1/2.4)-0.055):12.92*g;
        bb=(bb>0.0031308)?(1.055*Math.pow(bb,1/2.4)-0.055):12.92*bb;

        int R=(int)Math.min(255,Math.max(0,r*255));
        int G=(int)Math.min(255,Math.max(0,g*255));
        int B=(int)Math.min(255,Math.max(0,bb*255));

        return new Color(R,G,B);
    }

    private double fInvLab(double t){
        double t3=t*t*t;
        return (t3>0.008856)?t3:(t-16.0/116.0)/7.787;
    }

}
