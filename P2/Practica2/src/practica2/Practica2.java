package practica2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Practica2 extends JFrame {
    //imagenes RGB - Grises
    private BufferedImage image;
    private BufferedImage imageGray;
    private BufferedImage scaledImage;  //imagen reescalada en color
    private BufferedImage scaledImageGray; //imagen reescalada en gris
    private BufferedImage scaledImageR; // canal R
    private BufferedImage scaledImageG; // canal G
    private BufferedImage scaledImageB; // canal B
    private JLabel imageLabel;  

    //histogramas base
    private int[] histR = new int[256];
    private int[] histG = new int[256];
    private int[] histB = new int[256];
    private int[] histGray = new int[256];

    //histogramas de probabilidad
    private double[] probR = new double[256];
    private double[] probG = new double[256];
    private double[] probB = new double[256];
    private double[] probGray = new double[256];

    //histogramas de densidad de la probabilidad
    private double[] dpR = new double[256];
    private double[] dpG = new double[256];
    private double[] dpB = new double[256];
    private double[] dpGray = new double[256];

    private int totalPixels; //total de pixeles de la imagen

    //propiedades
    private double mediaR, mediaG, mediaB, mediaGray;
    private double varR, varG, varB, varGray;
    private double asimGray;
    private double energiaR, energiaG, energiaB, energiaGray;
    private double entropiaR, entropiaG, entropiaB, entropiaGray;

    //componentes gráficos
    private JTabbedPane mainTabs;
    private JPanel histogramPanel;
    private JPanel probPanel;
    private JPanel dpPanel;
    private JPanel propPanel;
    private JRadioButton rgbButton;
    private JRadioButton grayButton;

    public Practica2() {
        setTitle("Generador del Histograma");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            image = ImageIO.read(new File("ejemplopates.jpg")); // la imagen a subir
        } catch (IOException e) {
            System.out.println("Error al cargar la imagen.");
            System.exit(1);
        }

        //reescalar la imagen para mostrarla en una parte de la pantalla
        int scaledWidth = Math.min(200, image.getWidth());
        int scaledHeight = (int) ((double) scaledWidth / image.getWidth() * image.getHeight());
        Image scaled = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(scaled, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        calcularHistogramas();
        calcularProbabilidades();
        calcularDensidadProbabilidad();
        calcularPropiedades();

        //inicializar los paneles
        histogramPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        probPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        dpPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        propPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        //mostrar todo lo de RGB
        mostrarHistogramasRGB();
        mostrarProbabilidadesRGB();
        mostrarDensidadRGB();
        mostrarPropiedadesRGB();

        // lo principal
        mainTabs = new JTabbedPane();
        crearVistaPrincipal();
        crearPestañaProbabilidad();
        crearPestañaDensidad();
        crearPestañaPropiedades();
        add(mainTabs);
    }

    private void crearVistaPrincipal() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Panel de imágenes
        JPanel multiImagePanel = new JPanel(new GridLayout(1, 5, 10, 10));
        multiImagePanel.add(new JLabel(new ImageIcon(scaledImage)));
        multiImagePanel.add(new JLabel(new ImageIcon(scaledImageGray)));
        multiImagePanel.add(new JLabel(new ImageIcon(scaledImageR)));
        multiImagePanel.add(new JLabel(new ImageIcon(scaledImageG)));
        multiImagePanel.add(new JLabel(new ImageIcon(scaledImageB)));

        imagePanel.add(multiImagePanel);
        mainPanel.add(imagePanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        rgbButton = new JRadioButton("Mostrar RGB", true);
        grayButton = new JRadioButton("Mostrar Gris");
        ButtonGroup group = new ButtonGroup();
        group.add(rgbButton);
        group.add(grayButton);

        rgbButton.addActionListener(e -> {
            mostrarHistogramasRGB();
            mostrarProbabilidadesRGB();
            mostrarDensidadRGB();
            mostrarPropiedadesRGB();
        });
        grayButton.addActionListener(e -> {
            mostrarHistogramasGris();
            mostrarProbabilidadesGris();
            mostrarDensidadGris();
            mostrarPropiedadesGris();
        });

        buttonPanel.add(rgbButton);
        buttonPanel.add(grayButton);
        controlPanel.add(buttonPanel, BorderLayout.NORTH);

        controlPanel.add(histogramPanel, BorderLayout.CENTER);

        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainTabs.add("Histograma", mainPanel);
    }

    private void crearPestañaProbabilidad() {
        mainTabs.add("Probabilidad", probPanel);
    }

    private void crearPestañaDensidad() {
        mainTabs.add("Densidad", dpPanel);
    }

    private void crearPestañaPropiedades() {
        mainTabs.add("Propiedades", propPanel);
    }

    //mostrar propiedades
    private void mostrarPropiedadesRGB() {
        propPanel.removeAll();
        propPanel.setLayout(new GridLayout(2, 2, 10, 10));
        propPanel.add(new BarChartPanel(new double[]{varR, varG, varB}, "Varianza (RGB)", new String[]{"R","G","B"}));
        propPanel.add(new BarChartPanel(new double[]{energiaR, energiaG, energiaB}, "Energía (RGB)", new String[]{"R","G","B"}));
        propPanel.add(new BarChartPanel(new double[]{entropiaR, entropiaG, entropiaB}, "Entropía (RGB)", new String[]{"R","G","B"}));
        propPanel.revalidate();
        propPanel.repaint();
    }
    private void mostrarPropiedadesGris() {
        propPanel.removeAll();
        propPanel.setLayout(new GridLayout(2, 2, 10, 10));
        propPanel.add(new BarChartPanel(new double[]{varGray}, "Varianza (Gris)", new String[]{"Gray"}));
        propPanel.add(new BarChartPanel(new double[]{asimGray}, "Asimetría (Gris)", new String[]{"Gray"}));
        propPanel.add(new BarChartPanel(new double[]{energiaGray}, "Energía (Gris)", new String[]{"Gray"}));
        propPanel.add(new BarChartPanel(new double[]{entropiaGray}, "Entropía (Gris)", new String[]{"Gray"}));
        propPanel.revalidate();
        propPanel.repaint();
    }

    //mostrar histogramas
    private void mostrarHistogramasRGB() {
        histogramPanel.removeAll();
        histogramPanel.setLayout(new GridLayout(1, 3, 10, 10));
        histogramPanel.add(new GraphPanel(histR, "Histograma Rojo", Color.RED, mediaR, true));
        histogramPanel.add(new GraphPanel(histG, "Histograma Verde", Color.GREEN, mediaG, true));
        histogramPanel.add(new GraphPanel(histB, "Histograma Azul", Color.BLUE, mediaB, true));
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }
    private void mostrarHistogramasGris() {
        histogramPanel.removeAll();
        histogramPanel.setLayout(new GridLayout(1, 1));
        histogramPanel.add(new GraphPanel(histGray, "Histograma Gris", Color.GRAY, mediaGray, true));
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    //mostrar probabilidades
    private void mostrarProbabilidadesRGB() {
        probPanel.removeAll();
        probPanel.setLayout(new GridLayout(1, 3, 10, 10));
        probPanel.add(new GraphPanel(probR, "Probabilidad Rojo", Color.RED, mediaR, true));
        probPanel.add(new GraphPanel(probG, "Probabilidad Verde", Color.GREEN, mediaG, true));
        probPanel.add(new GraphPanel(probB, "Probabilidad Azul", Color.BLUE, mediaB, true));
        probPanel.revalidate();
        probPanel.repaint();
    }
    private void mostrarProbabilidadesGris() {
        probPanel.removeAll();
        probPanel.setLayout(new GridLayout(1, 1));
        probPanel.add(new GraphPanel(probGray, "Probabilidad Gris", Color.GRAY, mediaGray, true));
        probPanel.revalidate();
        probPanel.repaint();
    }

    //mostrar densidades
    private void mostrarDensidadRGB() {
        dpPanel.removeAll();
        dpPanel.setLayout(new GridLayout(1, 3, 10, 10));
        dpPanel.add(new GraphPanel(dpR, "Densidad Probabilidad Rojo", Color.RED, mediaR, false));
        dpPanel.add(new GraphPanel(dpG, "Densidad Probabilidad Verde", Color.GREEN, mediaG, false));
        dpPanel.add(new GraphPanel(dpB, "Densidad Probabilidad Azul", Color.BLUE, mediaB, false));
        dpPanel.revalidate();
        dpPanel.repaint();
    }
    private void mostrarDensidadGris() {
        dpPanel.removeAll();
        dpPanel.setLayout(new GridLayout(1, 1));
        dpPanel.add(new GraphPanel(dpGray, "Densidad Probabilidad Gris", Color.GRAY, mediaGray, false));
        dpPanel.revalidate();
        dpPanel.repaint();
    }

    private void calcularHistogramas() {
        int width = image.getWidth();  //ancho
        int height = image.getHeight();  //alto
        totalPixels = width * height;
        imageGray = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageR = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageG = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageB = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(image.getRGB(x, y));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                histR[r]++;
                histG[g]++;
                histB[b]++;
                int Y = (int)(0.299*r + 0.587*g + 0.114*b);  //obtener Y de YIQ para gris
                if (Y < 0) Y = 0; if (Y > 255) Y = 255;   // limitar a 0 o 255 si se desborda los valores
                histGray[Y]++;
                int grayRGB = (Y<<16)|(Y<<8)|Y;
                imageGray.setRGB(x, y, grayRGB);
                imageR.setRGB(x, y, new Color(r,0,0).getRGB());
                imageG.setRGB(x, y, new Color(0,g,0).getRGB());
                imageB.setRGB(x, y, new Color(0,0,b).getRGB());
            }
        }

        // reescalar todas las imágenes
        int scaledWidth = Math.min(200, image.getWidth());
        int scaledHeight = (int) ((double) scaledWidth / image.getWidth() * image.getHeight());

        scaledImageGray = scaleImage(imageGray, scaledWidth, scaledHeight);
        scaledImageR = scaleImage(imageR, scaledWidth, scaledHeight);
        scaledImageG = scaleImage(imageG, scaledWidth, scaledHeight);
        scaledImageB = scaleImage(imageB, scaledWidth, scaledHeight);
    }

    private BufferedImage scaleImage(BufferedImage src, int w, int h) {
        Image tmp = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(tmp, 0, 0, w, h, null);
        g2.dispose();
        return scaled;
    }

    private void calcularProbabilidades() {
        for (int i = 0; i < 256; i++) {
            probR[i] = (double) histR[i] / totalPixels;
            probG[i] = (double) histG[i] / totalPixels;
            probB[i] = (double) histB[i] / totalPixels;
            probGray[i] = (double) histGray[i] / totalPixels;
        }
    }

    private void calcularDensidadProbabilidad() {
        dpR[0] = probR[0];
        dpG[0] = probG[0];
        dpB[0] = probB[0];
        dpGray[0] = probGray[0];
        for (int i = 1; i < 256; i++) {
            dpR[i] = dpR[i-1] + probR[i];
            dpG[i] = dpG[i-1] + probG[i];
            dpB[i] = dpB[i-1] + probB[i];
            dpGray[i] = dpGray[i-1] + probGray[i];
        }
    }

    private void calcularPropiedades() {
        mediaR = calcMedia(probR);
        mediaG = calcMedia(probG);
        mediaB = calcMedia(probB);
        mediaGray = calcMedia(probGray);

        varR = calcVarianza(probR, mediaR);
        varG = calcVarianza(probG, mediaG);
        varB = calcVarianza(probB, mediaB);
        varGray = calcVarianza(probGray, mediaGray);

        asimGray = calcAsimetria(probGray, mediaGray);

        energiaR = calcEnergia(probR);
        energiaG = calcEnergia(probG);
        energiaB = calcEnergia(probB);
        energiaGray = calcEnergia(probGray);

        entropiaR = calcEntropia(probR);
        entropiaG = calcEntropia(probG);
        entropiaB = calcEntropia(probB);
        entropiaGray = calcEntropia(probGray);
    }

    private double calcMedia(double[] prob) {
        double m=0;
        for (int i=0;i<256;i++) m += i*prob[i];
        return m;
    }
    private double calcVarianza(double[] prob,double m) {
        double v=0; 
        for (int i=0;i<256;i++) v+= Math.pow(i-m,2)*prob[i]; return v;
    }
    private double calcAsimetria(double[] prob,double m) {
        double a=0; for (int i=0;i<256;i++) a+= Math.pow(i-m,3)*prob[i]; return a;
    }
    private double calcEnergia(double[] prob) {
        double e=0; for (int i=0;i<256;i++) e+= Math.pow(prob[i],2); return e;
    }
    private double calcEntropia(double[] prob) {
        double h=0; for (int i=0;i<256;i++) if (prob[i]>0) h+= prob[i]*(Math.log(prob[i])/Math.log(2));
        return -h;
    }

    //los paneles
    class GraphPanel extends JPanel {
        private double[] data;
        private String title;
        private Color color;
        private double media;
        private boolean verMedia;

        public GraphPanel(int[] data,String title,Color color,double media,boolean verMedia){
            this.data=new double[data.length];
            for(int i=0;i<data.length;i++) this.data[i]=data[i];
            this.title=title; this.color=color; this.media=media; this.verMedia=verMedia;
        }
        public GraphPanel(double[] data,String title,Color color,double media,boolean verMedia){
            this.data=data; this.title=title; this.color=color; this.media=media; this.verMedia=verMedia;
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            int width=getWidth(), height=getHeight(), margin=50;
            double max=0; for(double v:data) if(v>max) max=v;
            int graphWidth=width-2*margin, graphHeight=height-2*margin;
            g2.setColor(Color.BLACK);
            g2.drawLine(margin,height-margin,width-margin,height-margin);
            g2.drawLine(margin,margin,margin,height-margin);

            // Eje X
            for(int i=0;i<=255;i+=51){
                int x=margin+(i*graphWidth/256);
                g2.drawLine(x,height-margin,x,height-margin+5);
                g2.drawString(Integer.toString(i),x-10,height-margin+20);
            }

            // Eje Y (0, mitad, máximo)
            g2.drawString("0", margin-25,height-margin);
            g2.drawString(String.format("%.2f",max/2), margin-40, height-margin-(graphHeight/2));
            g2.drawString(String.format("%.2f",max), margin-40, margin+10);

            g2.drawString(title,width/2-50,margin-15);

            // Curva
            g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),100));
            Polygon poly=new Polygon();
            poly.addPoint(margin,height-margin);
            for(int i=0;i<256;i++){
                int x=margin+(i*graphWidth/256);
                int y=height-margin-(int)((data[i]/max)*graphHeight);
                poly.addPoint(x,y);
            }
            poly.addPoint(margin+graphWidth,height-margin);
            g2.fillPolygon(poly);

            g2.setColor(color);
            for(int i=0;i<255;i++){
                int x1=margin+(i*graphWidth/256);
                int y1=height-margin-(int)((data[i]/max)*graphHeight);
                int x2=margin+((i+1)*graphWidth/256);
                int y2=height-margin-(int)((data[i+1]/max)*graphHeight);
                g2.drawLine(x1,y1,x2,y2);
            }
            //linea de la media
            if(verMedia){
                int Media=margin+(int)((media*graphWidth)/256);
                g2.setColor(Color.MAGENTA);
                g2.drawLine(Media,margin,Media,height-margin);
                g2.drawString("Media="+String.format("%.2f",media),Media+5,margin+15);
            }
        }
    }

    class BarChartPanel extends JPanel {
        private double[] values;
        private String title;
        private String[] labels;

        public BarChartPanel(double[] values,String title,String[] labels){
            this.values=values; this.title=title; this.labels=labels;
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            int width=getWidth(), height=getHeight(), margin=50;
            double maxAbs=0;
            for(double v:values) maxAbs = Math.max(maxAbs, Math.abs(v));
            int graphWidth=width-2*margin, graphHeight=height-2*margin;
            int barWidth=graphWidth/values.length-20;
            g2.setColor(Color.BLACK);
            g2.drawLine(margin,height-margin,width-margin,height-margin);
            g2.drawLine(margin,margin,margin,height-margin);
            g2.drawString(title,width/2-40,margin-15);

            // Eje Y (0, mitad, máximo)
            g2.drawString("0", margin-25,height-margin);
            g2.drawString(String.format("%.2f",maxAbs/2), margin-40, height-margin-(graphHeight/2));
            g2.drawString(String.format("%.2f",maxAbs), margin-40, margin+10);

            Color[] barColors={Color.RED,Color.GREEN,Color.BLUE,Color.GRAY};
            for(int i=0;i<values.length;i++){
                int barHeight=(int)((Math.abs(values[i])/maxAbs)*graphHeight);
                int x=margin+i*(barWidth+20);
                int y=height-margin-barHeight;
                g2.setColor(barColors[i%barColors.length]);
                g2.fillRect(x,y,barWidth,barHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(x,y,barWidth,barHeight);
                g2.drawString(labels[i],x+barWidth/2-10,height-margin+20);
                g2.drawString(String.format("%.2f",values[i]),x+barWidth/2-15,y-5);
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Practica2 app=new Practica2();
            app.setVisible(true);
        });
    }
}
