package gui;

import logica.Practica6_1;
import logica.Histograma;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Pantalla extends JFrame {

    private BufferedImage imgOriginal, imgProcesada, imgSecundaria;
    private JLabel lblOriginal, lblProcesada, lblSecundaria;
    private JPanel panelHistOriginal, panelHistProcesada, panelHistSecundaria;

    public Pantalla() {
        super("Modificación del Histograma - 1");
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setSize(1450, 870);
        setLocationRelativeTo(null);

        //botones superiores
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton btnCargar1 = new JButton("Cargar Imagen 1");
        JButton btnReset = new JButton("Reset");
        top.add(btnCargar1);
        top.add(btnReset);
        add(top, BorderLayout.NORTH);

        //panel derecho
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1) Desplazamiento", panelDesplazamiento());
        tabs.addTab("2) Expansión / Contracción", panelContraste());
        tabs.addTab("3) Propiedades (hi, p(hi), D(p(hi)))", panelIgualacion());
        tabs.addTab("4) Correspondencia", panelCorrespondencia());
        tabs.addTab("5) Ecualización", panelEcualizacion());


        JPanel panelOperaciones = new JPanel(new BorderLayout());
        panelOperaciones.add(tabs, BorderLayout.CENTER);
        panelOperaciones.setPreferredSize(new Dimension(360, 400)); // Más compacto

        //imagenes a la izq
        JPanel panelImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        panelImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblOriginal = crearLabelImagen("Imagen Original");
        lblProcesada = crearLabelImagen("Imagen Procesada");
        lblSecundaria = crearLabelImagen("Imagen 2 (Referencia)");

        panelImagenes.add(construirPanelImagen(lblOriginal, "Imagen Original"));
        panelImagenes.add(construirPanelImagen(lblProcesada, "Imagen Procesada"));
        panelImagenes.add(construirPanelImagen(lblSecundaria, "Imagen 2 (Referencia)"));

        // imagenes | operaciones
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelImagenes, panelOperaciones);
        split.setDividerLocation(1020);
        split.setResizeWeight(0.80);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        //histogramas
        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 10));
        panelHistOriginal = crearPanelHistograma("Histograma - Original");
        panelHistProcesada = crearPanelHistograma("Histograma - Procesada");
        panelHistSecundaria = crearPanelHistograma("Histograma - Referencia");

        bottom.add(panelHistOriginal);
        bottom.add(panelHistProcesada);
        bottom.add(panelHistSecundaria);
        add(bottom, BorderLayout.SOUTH);

        btnCargar1.addActionListener(e -> cargarImagen(false));
        btnReset.addActionListener(e -> resetear());

        setVisible(true);
    }

    private JPanel construirPanelImagen(JLabel lbl, String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(300, 380));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JLabel crearLabelImagen(String title) {
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(350, 350));
        lbl.setBorder(BorderFactory.createTitledBorder(title));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JPanel crearPanelHistograma(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        return p;
    }

    //panel de operaciones

    private JPanel panelDesplazamiento() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 12));
        JLabel info = new JLabel("Desplazamiento (mover histograma izquierda/derecha)");
        JSlider slider = new JSlider(-128, 128, 0);
        slider.setMajorTickSpacing(64);
        slider.setMinorTickSpacing(16);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        JButton aplicar = new JButton("Aplicar desplazamiento");
        center.add(info);
        center.add(slider);
        center.add(aplicar);
        p.add(center, BorderLayout.CENTER);

        aplicar.addActionListener(e -> {
            if (imgOriginal == null) {
                mostrarMsg("Carga primero la imagen original.");
                return;
            }
            imgProcesada = Practica6_1.desplazar(imgOriginal, slider.getValue());
            actualizarVisualizacionDespuésTransformacion();
        });
        return p;
    }

    private JPanel panelContraste() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 12));
        JLabel info = new JLabel("Factor de contraste");
        JSlider slider = new JSlider(50, 200, 100);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        JButton aplicar = new JButton("Aplicar contraste");
        center.add(info);
        center.add(slider);
        center.add(aplicar);
        p.add(center, BorderLayout.CENTER);

        aplicar.addActionListener(e -> {
            if (imgOriginal == null) {
                mostrarMsg("Carga primero la imagen.");
                return;
            }
            double factor = slider.getValue() / 100.0;
            imgProcesada = Practica6_1.ajustarContraste(imgOriginal, factor);
            actualizarVisualizacionDespuésTransformacion();
        });
        return p;
    }

    private JPanel panelIgualacion() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JTabbedPane subtabs = new JTabbedPane();

        JPanel subHi = new JPanel(new FlowLayout(FlowLayout.CENTER,8,10));
        JButton btnHi = new JButton("Mostrar hi (frecuencia)");
        btnHi.addActionListener(e -> {
            if (imgOriginal == null) { mostrarMsg("Carga primero la imagen."); return; }
            int[] hi = Practica6_1.calcularHistograma(imgOriginal);
            mostrarHistogramaEnVentana(hi, "hi (original)");
        });
        subHi.add(btnHi);

        JPanel subP = new JPanel(new FlowLayout(FlowLayout.CENTER,8,10));
        JButton btnP = new JButton("Mostrar p(hi) (probabilidades)");
        btnP.addActionListener(e -> {
            if (imgOriginal == null) { mostrarMsg("Carga primero la imagen."); return; }
            int[] hi = Practica6_1.calcularHistograma(imgOriginal);
            double[] pb = Practica6_1.calcularProbabilidad(hi, imgOriginal.getWidth() * imgOriginal.getHeight());
            int[] ip = convertirDoubleAIntEscalado(pb, imgOriginal.getWidth() * imgOriginal.getHeight());
            mostrarHistogramaEnVentana(ip, "p(hi) (probabilidades)");
        });
        subP.add(btnP);

        JPanel subD = new JPanel(new FlowLayout(FlowLayout.CENTER,8,10));
        JButton btnD = new JButton("Mostrar D(p(hi)) (acumulada)");
        btnD.addActionListener(e -> {
            if (imgOriginal == null) { mostrarMsg("Carga primero la imagen."); return; }
            int[] hi = Practica6_1.calcularHistograma(imgOriginal);
            double[] pb = Practica6_1.calcularProbabilidad(hi, imgOriginal.getWidth() * imgOriginal.getHeight());
            double[] D = Practica6_1.calcularDistribucionAcumulada(pb);
            int[] iD = convertirDoubleAIntEscalado(D, imgOriginal.getWidth() * imgOriginal.getHeight());
            mostrarHistogramaEnVentana(iD, "D(p(hi)) (acumulada)");
        });
        subD.add(btnD);

        subtabs.addTab("hi", subHi);
        subtabs.addTab("p(hi)", subP);
        subtabs.addTab("D(p(hi))", subD);

        p.add(subtabs, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelCorrespondencia() {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel center = new JPanel(new GridLayout(3,1,4,4));
        JLabel info = new JLabel("<html>Carga la imagen 2 (de referencia) y luego aplica correspondencia.<br>"
                + "La correspondencia iguala la densidad acumulada entre imágenes.</html>", SwingConstants.CENTER);
        JButton btnCargar2 = new JButton("Cargar Imagen 2 (referencia)");
        JButton btnAplicar = new JButton("Aplicar correspondencia");

        btnCargar2.addActionListener(e -> cargarImagen(true));
        btnAplicar.addActionListener(e -> {
            if (imgOriginal == null || imgSecundaria == null) {
                mostrarMsg("Carga ambas imágenes antes de aplicar correspondencia.");
                return;
            }
            imgProcesada = Practica6_1.correspondencia(imgOriginal, imgSecundaria);
            actualizarVisualizacionDespuésTransformacion();
        });

        center.add(info);
        center.add(btnCargar2);
        center.add(btnAplicar);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    //aux
    private void cargarImagen(boolean esSecundaria) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                if (img == null) { mostrarMsg("El archivo seleccionado no es una imagen válida."); return; }

                if (esSecundaria) {
                    imgSecundaria = convertirAGrises(img);
                    mostrarImagenEscalada(lblSecundaria, imgSecundaria);
                    mostrarHistogramaPanel(panelHistSecundaria, imgSecundaria, "Referencia");
                } else {
                    imgOriginal = convertirAGrises(img);
                    imgProcesada = null;
                    mostrarImagenEscalada(lblOriginal, imgOriginal);
                    lblProcesada.setIcon(null);
                    panelHistOriginal.removeAll();
                    panelHistProcesada.removeAll();
                    mostrarHistogramaPanel(panelHistOriginal, imgOriginal, "Original");
                }
            } catch (Exception ex) {
                mostrarMsg("Error al leer la imagen:\n" + ex.getMessage());
            }
        }
    }

    private BufferedImage convertirAGrises(BufferedImage imgColor) {
        int width = imgColor.getWidth();
        int height = imgColor.getHeight();
        BufferedImage gris = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(imgColor.getRGB(x, y));
                int yVal = (int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                int gray = new Color(yVal, yVal, yVal).getRGB();
                gris.setRGB(x, y, gray);
            }
        }
        return gris;
    }

    private void mostrarImagenEscalada(JLabel label, BufferedImage imagen) {
        if (imagen == null || label == null) return;
        SwingUtilities.invokeLater(() -> {
            int anchoLabel = label.getWidth();
            int altoLabel = label.getHeight();

            if (anchoLabel <= 0 || altoLabel <= 0) {
                Dimension pref = label.getPreferredSize();
                anchoLabel = pref.width;
                altoLabel = pref.height;
            }

            double ratioImg = (double) imagen.getWidth() / imagen.getHeight();
            double ratioLbl = (double) anchoLabel / altoLabel;

            int newWidth, newHeight;

            if (ratioImg > ratioLbl) {
                newWidth = anchoLabel;
                newHeight = (int) (anchoLabel / ratioImg);
            } else {
                newHeight = altoLabel;
                newWidth = (int) (altoLabel * ratioImg);
            }

            Image imgEscalada = imagen.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imgEscalada));
        });
    }

    private void resetear() {
        imgOriginal = null;
        imgProcesada = null;
        imgSecundaria = null;
        lblOriginal.setIcon(null);
        lblProcesada.setIcon(null);
        lblSecundaria.setIcon(null);
        panelHistOriginal.removeAll();
        panelHistProcesada.removeAll();
        panelHistSecundaria.removeAll();
        repaint();
    }

    private void actualizarVisualizacionDespuésTransformacion() {
        if (imgProcesada != null) {
            mostrarImagenEscalada(lblProcesada, imgProcesada);
            mostrarHistogramaPanel(panelHistProcesada, imgProcesada, "Procesada");
        }
    }

    private void mostrarHistogramaPanel(JPanel panel, BufferedImage img, String titulo) {
        panel.removeAll();
        int[] hi = Practica6_1.calcularHistograma(img);
        Histograma histComp = new Histograma(hi, titulo, Color.DARK_GRAY);
        panel.add(histComp, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private void mostrarHistogramaEnVentana(int[] datos, String titulo) {
        Histograma hist = new Histograma(datos, titulo, Color.GRAY);
        JFrame f = new JFrame("Histograma - " + titulo);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane().add(hist);
        f.setSize(800, 380);
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }

    private int[] convertirDoubleAIntEscalado(double[] vals, int scale) {
        int[] r = new int[vals.length];
        for (int i = 0; i < vals.length; i++) {
            r[i] = (int) Math.round(vals[i] * scale);
        }
        return r;
    }

    private void mostrarMsg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }
    
    private JPanel panelEcualizacion() {
        JPanel p = new JPanel(new BorderLayout(6,6));

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        JLabel lblTipo = new JLabel("Tipo de ecualización:");
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{
                "Uniforme", "Exponencial", "Rayleigh", "Hiperbólica Raíces"
        });
        JLabel lblAlpha = new JLabel("α (alpha):");
        JTextField txtAlpha = new JTextField("1.5");
        JLabel lblPot = new JLabel("pot (potencia):");
        JTextField txtPot = new JTextField("2.0");
        JButton btnAplicar = new JButton("Aplicar Ecualización");

        form.add(lblTipo);
        form.add(comboTipo);
        form.add(lblAlpha);
        form.add(txtAlpha);
        form.add(lblPot);
        form.add(txtPot);
        form.add(new JLabel(""));
        form.add(btnAplicar);

        p.add(form, BorderLayout.CENTER);

        btnAplicar.addActionListener(e -> {
            if (imgOriginal == null) {
                mostrarMsg("Carga primero la imagen original.");
                return;
            }
            String tipo = (String) comboTipo.getSelectedItem();
            double alpha, pot;

            try {
                alpha = Double.parseDouble(txtAlpha.getText());
                pot = Double.parseDouble(txtPot.getText());
            } catch (NumberFormatException ex) {
                mostrarMsg("Introduce valores numéricos válidos para α y pot.");
                return;
            }

            try {
                imgProcesada = Practica6_1.ecualizar(imgOriginal, tipo, alpha, pot);
                actualizarVisualizacionDespuésTransformacion();
                mostrarMsg("Ecualización aplicada con tipo: " + tipo);
            } catch (Exception ex) {
                mostrarMsg("Error al aplicar ecualización:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pantalla::new);
    }
}