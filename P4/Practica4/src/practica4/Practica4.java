package practica4;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Practica4 extends JFrame {

    //imagenes
    private BufferedImage imagenOriginal;
    private BufferedImage imagenOriginalGris;
    private BufferedImage imagenProcesada;
    //etiquetas (imagenes)
    private JLabel etiquetaOriginal;
    private JLabel etiquetaProcesada;
    //botenoes
    private JComboBox<String> comboUmbrales;
    private JTextField[] camposUmbrales;
    private JButton botonInvertir;
    private JButton botonReconstruir;
    private boolean invertida = false;
    //componentes YIQ
    private double[][] matrizY; 
    private double[][] matrizI; 
    private double[][] matrizQ; 
    private int[][] ultimaBinarizada; //Yb de la última binarización
    //paneles para historgramas
    private PanelHistograma histogramaOriginal;
    private PanelHistograma histogramaProcesada;

    public Practica4() {
        super("Binarización de una Imagen");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel panelImagenes = new JPanel(new GridLayout(1, 2, 10, 10));

        etiquetaOriginal = new JLabel("Imagen Original", SwingConstants.CENTER);
        etiquetaProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);
        etiquetaOriginal.setVerticalTextPosition(JLabel.BOTTOM);
        etiquetaOriginal.setHorizontalTextPosition(JLabel.CENTER);
        etiquetaProcesada.setVerticalTextPosition(JLabel.BOTTOM);
        etiquetaProcesada.setHorizontalTextPosition(JLabel.CENTER);

        histogramaOriginal = new PanelHistograma(new int[0], "Histograma Original", Color.GRAY);
        histogramaProcesada = new PanelHistograma(new int[0], "Histograma Procesada", Color.BLUE);

        JPanel seccionOriginal = new JPanel(new GridLayout(2, 1, 5, 5));
        seccionOriginal.add(new JScrollPane(etiquetaOriginal));
        seccionOriginal.add(histogramaOriginal);

        JPanel seccionProcesada = new JPanel(new GridLayout(2, 1, 5, 5));
        seccionProcesada.add(new JScrollPane(etiquetaProcesada));
        seccionProcesada.add(histogramaProcesada);

        panelImagenes.add(seccionOriginal);
        panelImagenes.add(seccionProcesada);
        add(panelImagenes, BorderLayout.CENTER);

        JPanel panelControles = new JPanel();
        JButton botonCargar = new JButton("Cargar Imagen");
        botonCargar.addActionListener(e -> cargarImagen());
        panelControles.add(botonCargar);

        panelControles.add(new JLabel("Número de umbrales:"));
        comboUmbrales = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        comboUmbrales.addActionListener(e -> actualizarCamposUmbrales());
        panelControles.add(comboUmbrales);

        camposUmbrales = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            camposUmbrales[i] = new JTextField(3);
            camposUmbrales[i].setText(String.valueOf(64 * (i + 1))); //valores por defento
            panelControles.add(camposUmbrales[i]);
        }

        JButton botonProcesar = new JButton("Procesar");
        botonProcesar.addActionListener(e -> procesarImagen());
        panelControles.add(botonProcesar);

        botonInvertir = new JButton("Invertir Binarización");
        botonInvertir.setEnabled(false);
        botonInvertir.addActionListener(e -> invertirImagen());
        panelControles.add(botonInvertir);

        botonReconstruir = new JButton("Reconstruir a RGB");
        botonReconstruir.setEnabled(false);
        botonReconstruir.addActionListener(e -> mostrarReconstruida());
        panelControles.add(botonReconstruir);

        add(panelControles, BorderLayout.SOUTH);

        actualizarCamposUmbrales();

        setSize(900, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarImagen() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes JPG, PNG", "jpg", "jpeg", "png"));
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                imagenOriginal = ImageIO.read(selector.getSelectedFile());

                matrizY = convertirImagenAY(imagenOriginal);
                matrizI = convertirImagenAI(imagenOriginal);
                matrizQ = convertirImagenAQ(imagenOriginal);

                imagenOriginalGris = convertirYAGris(matrizY);
                etiquetaOriginal.setIcon(new ImageIcon(escalarImagen(imagenOriginalGris, etiquetaOriginal.getWidth(), 300)));
                etiquetaOriginal.setText("Imagen Original");

                int[] histOriginal = calcularHistogramaDeY(matrizY);
                histogramaOriginal.setData(histOriginal);

                etiquetaProcesada.setIcon(null);
                etiquetaProcesada.setText("Imagen Procesada");
                imagenProcesada = null;
                ultimaBinarizada = null;
                invertida = false;
                botonInvertir.setEnabled(false);
                botonReconstruir.setEnabled(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + ex.getMessage());
            }
        }
    }

    private void actualizarCamposUmbrales() {
        int numUmbrales = comboUmbrales.getSelectedIndex() + 1;
        for (int i = 0; i < 4; i++) {
            camposUmbrales[i].setEnabled(i < numUmbrales);
        }
    }

    private void procesarImagen() {
        if (imagenOriginal == null) {
            JOptionPane.showMessageDialog(this, "Carga primero una imagen.");
            return;
        }

        int numUmbrales = comboUmbrales.getSelectedIndex() + 1;
        double[] umbrales = new double[numUmbrales];
        try {
            for (int i = 0; i < numUmbrales; i++) {
                umbrales[i] = Double.parseDouble(camposUmbrales[i].getText());
            }
            java.util.Arrays.sort(umbrales);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Introduce umbrales válidos (números).");
            return;
        }

        int altura = imagenOriginal.getHeight();
        int ancho = imagenOriginal.getWidth();
        int[][] binarizada = new int[altura][ancho];

        //recorrer y aplicar binarización según los umbrales
        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                double valorY = matrizY[fila][col];
                switch (numUmbrales) {
                    case 1:
                        binarizada[fila][col] = binarizarUnoUmbral(valorY, umbrales[0]);
                        break;
                    case 2:
                        binarizada[fila][col] = binarizarDosUmbrales(valorY, umbrales[0], umbrales[1]);
                        break;
                    case 3:
                        binarizada[fila][col] = binarizarTresUmbrales(valorY, umbrales[0], umbrales[1], umbrales[2]);
                        break;
                    case 4:
                        binarizada[fila][col] = binarizarCuatroUmbrales(valorY, umbrales[0], umbrales[1], umbrales[2], umbrales[3]);
                        break;
                }
            }
        }

        ultimaBinarizada = binarizada; //guardar para futuras operaciones
        imagenProcesada = convertirYBinariaAImagenGris(binarizada); //mostrar en grises en la ventana principal
        int[] histProcesada = calcularHistogramaDeYb(binarizada);
        histogramaProcesada.setData(histProcesada);
        etiquetaProcesada.setIcon(new ImageIcon(escalarImagen(imagenProcesada, etiquetaProcesada.getWidth(), 300)));
        etiquetaProcesada.setText("Imagen Procesada");
        invertida = false;
        botonInvertir.setEnabled(true);
        botonReconstruir.setEnabled(true); //reconstrucción RGB
    }

    //invertir la binarización
    private void invertirImagen() {
        if (ultimaBinarizada == null) return;

        int altura = ultimaBinarizada.length;
        int ancho = ultimaBinarizada[0].length;

        // Invertir cada valor en la matriz binarizada
        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                ultimaBinarizada[fila][col] = 255 - ultimaBinarizada[fila][col];
            }
        }
        //recrear la imagen procesada en grises con la inversión
        imagenProcesada = convertirYBinariaAImagenGris(ultimaBinarizada);
        int[] histProcesada = calcularHistogramaDeYb(ultimaBinarizada);
        histogramaProcesada.setData(histProcesada);
        etiquetaProcesada.setIcon(new ImageIcon(escalarImagen(imagenProcesada, etiquetaProcesada.getWidth(), 300)));
        invertida = !invertida;
        botonReconstruir.setEnabled(true); 
    }

    //ventana para imagen reconstruida
    private void mostrarReconstruida() {
        if (ultimaBinarizada == null) {
            JOptionPane.showMessageDialog(this, "Primero procesa y binariza una imagen.");
            return;
        }
        BufferedImage reconstruida = convertirYIQaRGB(ultimaBinarizada, matrizI, matrizQ);
        JFrame ventanaNueva = new JFrame("Imagen Reconstruida");
        ventanaNueva.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaNueva.setSize(800, 600);
        JLabel etiquetaImagen = new JLabel(new ImageIcon(escalarImagen(reconstruida, 700, 500)));
        etiquetaImagen.setHorizontalAlignment(SwingConstants.CENTER);
        ventanaNueva.add(new JScrollPane(etiquetaImagen), BorderLayout.CENTER);
        ventanaNueva.setLocationRelativeTo(this);
        ventanaNueva.setVisible(true);
    }

    //aux
    public static double rgbAY(int r, int g, int b) {  //componente Y
        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    public static double rgbAI(int r, int g, int b) {  //componente I
        return 0.596 * r - 0.275 * g - 0.321 * b;
    }

    public static double rgbAQ(int r, int g, int b) {  //componente Q
        return 0.212 * r - 0.523 * g + 0.311 * b;
    }

    //con un umbral
    public static int binarizarUnoUmbral(double y, double umbral) {
        return y >= umbral ? 255 : 0;
    }

    //con dos umbrales
    public static int binarizarDosUmbrales(double y, double t1, double t2) {
        if (y < t1) return 0;
        else if (y < t2) return 127;
        else return 255;
    }

    //con tres umbrales
    public static int binarizarTresUmbrales(double y, double t1, double t2, double t3) {
        if (y < t1) return 0;
        else if (y < t2) return 85;
        else if (y < t3) return 170;
        else return 255;
    }

    //con cuatro umbrales
    public static int binarizarCuatroUmbrales(double y, double t1, double t2, double t3, double t4) {
        if (y < t1) return 0;
        else if (y < t2) return 64;
        else if (y < t3) return 128;
        else if (y < t4) return 192;
        else return 255;
    }

    //imagen RGB a matriz Y
    public static double[][] convertirImagenAY(BufferedImage img) {
        int ancho = img.getWidth();
        int altura = img.getHeight();
        double[][] Y = new double[altura][ancho];

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int rgb = img.getRGB(col, fila);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                Y[fila][col] = rgbAY(r, g, b);
            }
        }
        return Y;
    }

    //imagen RGB a matriz I
    public static double[][] convertirImagenAI(BufferedImage img) {
        int ancho = img.getWidth();
        int altura = img.getHeight();
        double[][] I = new double[altura][ancho];

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int rgb = img.getRGB(col, fila);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                I[fila][col] = rgbAI(r, g, b);
            }
        }
        return I;
    }

    //imagen RGB a matriz Q
    public static double[][] convertirImagenAQ(BufferedImage img) {
        int ancho = img.getWidth();
        int altura = img.getHeight();
        double[][] Q = new double[altura][ancho];

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int rgb = img.getRGB(col, fila);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                Q[fila][col] = rgbAQ(r, g, b);
            }
        }
        return Q;
    }

    //matriz Y a imagen en escala de grises
    private BufferedImage convertirYAGris(double[][] Y) {
        int altura = Y.length;
        int ancho = Y[0].length;
        BufferedImage img = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int valor = (int) Math.round(Y[fila][col]);
                if (valor < 0) valor = 0;
                if (valor > 255) valor = 255;
                int rgb = (valor << 16) | (valor << 8) | valor;
                img.setRGB(col, fila, rgb);
            }
        }
        return img;
    }

    //Yb (binarizada) a imagen en escala de grises
    public static BufferedImage convertirYBinariaAImagenGris(int[][] Yb) {
        int altura = Yb.length;
        int ancho = Yb[0].length;
        BufferedImage img = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int valor = Yb[fila][col];
                int rgb = (valor << 16) | (valor << 8) | valor;
                img.setRGB(col, fila, rgb);
            }
        }
        return img;
    }

    // histograma de la matriz Y
    private int[] calcularHistogramaDeY(double[][] Y) {
        int altura = Y.length;
        int ancho = Y[0].length;
        int[] hist = new int[256];
        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int bin = (int) Math.round(Y[fila][col]);
                if (bin < 0) bin = 0;
                if (bin > 255) bin = 255;
                hist[bin]++;
            }
        }
        return hist;
    }

    // Calcular histograma directamente de la matriz Yb binarizada
    private int[] calcularHistogramaDeYb(int[][] Yb) {
        int altura = Yb.length;
        int ancho = Yb[0].length;
        int[] hist = new int[256];
        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int bin = Yb[fila][col];
                if (bin < 0) bin = 0;
                if (bin > 255) bin = 255;
                hist[bin]++;
            }
        }
        return hist;
    }

    //calcular histograma de una imagen
    private int[] calcularHistogramaDeImagen(BufferedImage img) {
        if (img == null) return null;
        int altura = img.getHeight();
        int ancho = img.getWidth();
        int[] hist = new int[256];
        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                int rgb = img.getRGB(col, fila);
                int gris = (rgb >> 16) & 0xff;
                hist[gris]++;
            }
        }
        return hist;
    }

    //convertir imagen binaria a RGB
    private BufferedImage convertirYIQaRGB(int[][] Yb, double[][] I, double[][] Q) {
        int altura = Yb.length;
        int ancho = Yb[0].length;
        BufferedImage img = new BufferedImage(ancho, altura, BufferedImage.TYPE_INT_RGB);

        for (int fila = 0; fila < altura; fila++) {
            for (int col = 0; col < ancho; col++) {
                double yVal = Yb[fila][col]; // Y binarizado
                double iVal = I[fila][col];  // I original 
                double qVal = Q[fila][col];  // Q original 

                //fórmulas de conversión YIQ a RGB
                double r = yVal + 0.956 * iVal + 0.621 * qVal;
                double g = yVal - 0.272 * iVal - 0.647 * qVal;
                double b = yVal - 1.105 * iVal + 1.702 * qVal;

                //limitar a rango 0-255
                int rInt = limitarValor((int) Math.round(r));
                int gInt = limitarValor((int) Math.round(g));
                int bInt = limitarValor((int) Math.round(b));

                int rgb = (rInt << 16) | (gInt << 8) | bInt;
                img.setRGB(col, fila, rgb);
            }
        }
        return img;
    }

    //limitar valores entre 0 y 255
    private static int limitarValor(int val) {
        return Math.min(255, Math.max(0, val));
    }

    //redimensionar imagen
    private Image escalarImagen(BufferedImage img, int maxAncho, int maxAlto) {
        if (img == null) return null;
        int ancho = img.getWidth();
        int altura = img.getHeight();
        double escala = Math.min((double) maxAncho / ancho, (double) maxAlto / altura);
        if (escala > 1) escala = 1;
        return img.getScaledInstance((int) (ancho * escala), (int) (altura * escala), Image.SCALE_SMOOTH);
    }

    //dibujar histogramas
    private static class PanelHistograma extends JPanel {
        private double[] datos;
        private String titulo;
        private Color color;

        public PanelHistograma(int[] datos, String titulo, Color color) {
            this.datos = new double[datos.length];
            for (int i = 0; i < datos.length; i++) this.datos[i] = datos[i];
            this.titulo = titulo;
            this.color = color;
        }

        public PanelHistograma(double[] datos, String titulo, Color color) {
            this.datos = datos;
            this.titulo = titulo;
            this.color = color;
        }

        public void setData(int[] nuevosDatos) {
            if (nuevosDatos == null) {
                this.datos = null;
            } else {
                this.datos = new double[nuevosDatos.length];
                for (int i = 0; i < nuevosDatos.length; i++) this.datos[i] = nuevosDatos[i];
            }
            repaint(); 
        }

        public void setData(double[] nuevosDatos) {
            if (nuevosDatos == null) {
                this.datos = null;
            } else {
                this.datos = nuevosDatos.clone();
            }
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos == null || datos.length == 0) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int ancho = getWidth(), alto = getHeight(), margen = 50;
            double maximo = 0;
            for (double v : datos) if (v > maximo) maximo = v;
            if (maximo == 0) return;
            int anchoGrafico = ancho - 2 * margen, altoGrafico = alto - 2 * margen;
            g2.setColor(Color.BLACK);
            g2.drawLine(margen, alto - margen, ancho - margen, alto - margen); // Eje X
            g2.drawLine(margen, margen, margen, alto - margen); // Eje Y

            //eje X (cada 51 niveles, de 0 a 255)
            for (int i = 0; i <= 255; i += 51) {
                int x = margen + (i * anchoGrafico / 256);
                g2.drawLine(x, alto - margen, x, alto - margen + 5);
                g2.drawString(Integer.toString(i), x - 10, alto - margen + 20);
            }

            //eje Y (0, mitad del máximo, máximo)
            g2.drawString("0", margen - 25, alto - margen);
            g2.drawString(String.format("%.0f", maximo / 2), margen - 40, alto - margen - (altoGrafico / 2));
            g2.drawString(String.format("%.0f", maximo), margen - 40, margen + 10);

            g2.drawString(titulo, ancho / 2 - 50, margen - 15); //titulo

            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100)); 
            Polygon poligono = new Polygon();
            poligono.addPoint(margen, alto - margen);
            for (int i = 0; i < 256; i++) {
                int x = margen + (i * anchoGrafico / 256);
                int y = alto - margen - (int) ((datos[i] / maximo) * altoGrafico);
                poligono.addPoint(x, y);
            }
            poligono.addPoint(margen + anchoGrafico, alto - margen);
            g2.fillPolygon(poligono);

            g2.setColor(color);
            for (int i = 0; i < 255; i++) {
                int x1 = margen + (i * anchoGrafico / 256);
                int y1 = alto - margen - (int) ((datos[i] / maximo) * altoGrafico);
                int x2 = margen + ((i + 1) * anchoGrafico / 256);
                int y2 = alto - margen - (int) ((datos[i + 1] / maximo) * altoGrafico);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Practica4());
    }
}