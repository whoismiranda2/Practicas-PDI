package gui;

import logica.Practica7;
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
        super("Modificación del Histograma - Ecualización");
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setSize(1200, 780);
        setLocationRelativeTo(null);

        // botones superiores
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton btnCargar1 = new JButton("Cargar Imagen");
        JButton btnReset = new JButton("Reset");
        top.add(btnCargar1);
        top.add(btnReset);
        add(top, BorderLayout.NORTH);

        // panel derecho: solo Ecualización
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ecualización", panelEcualizacion());

        JPanel panelOperaciones = new JPanel(new BorderLayout());
        panelOperaciones.add(tabs, BorderLayout.CENTER);
        panelOperaciones.setPreferredSize(new Dimension(360, 400));

        // imagenes a la izq
        JPanel panelImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        panelImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblOriginal = crearLabelImagen("Imagen Original");
        lblProcesada = crearLabelImagen("Imagen Procesada");

        panelImagenes.add(construirPanelImagen(lblOriginal, "Imagen Original"));
        panelImagenes.add(construirPanelImagen(lblProcesada, "Imagen Procesada"));

        // imagenes | operaciones
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelImagenes, panelOperaciones);
        split.setDividerLocation(800);
        split.setResizeWeight(0.70);
        split.setOneTouchExpandable(true);
        add(split, BorderLayout.CENTER);

        // histogramas en el bottom
        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 10));
        panelHistOriginal = crearPanelHistograma("Histograma - Original");
        panelHistProcesada = crearPanelHistograma("Histograma - Procesada");

        bottom.add(panelHistOriginal);
        bottom.add(panelHistProcesada);
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

    // ---------- Panel de Ecualización ----------
    private JPanel panelEcualizacion() {
        JPanel p = new JPanel(new BorderLayout(6,6));

        JPanel form = new JPanel(new GridLayout(6,2,8,8));
        JLabel lblTipo = new JLabel("Tipo de ecualización:");
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{
                "Uniforme", "Exponencial", "Rayleigh", "Hiperbólica Raíces", "Hiperbólica Logarítmica"
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
        form.add(new JLabel(""));
        form.add(btnAplicar);

        p.add(form, BorderLayout.CENTER);

        // acciones

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
                imgProcesada = Practica7.ecualizar(imgOriginal, tipo, alpha, pot);
                actualizarVisualizacionDespuésTransformacion();
                mostrarMsg("Ecualización aplicada con tipo: " + tipo);
            } catch (Exception ex) {
                mostrarMsg("Error al aplicar ecualización:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return p;
    }

    // ---------- Auxiliares ----------
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
        int[] hi = Practica7.calcularHistograma(img);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pantalla::new);
    }
}
