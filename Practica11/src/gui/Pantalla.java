package gui;

import logica.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Interfaz gráfica de usuario, clase que contiene el main
 * desde aquí se configura lo necesario para que el usuario interactúe con la aplicación
 * hace las llamadas e inserciones requeridas para llevar los métodos realizados desde la lógica
 * incluye un botón de reset para eliminar las operaciones realizadas en la imagen
 * permite que el usuario seleccione el elemento estructurante que desee a partir de los mostrados en la lista
 * 
 * @author andre
 */

public class Pantalla extends JFrame {

    private BufferedImage original;   // imagen lógica original (gris)
    private BufferedImage procesada;  // imagen lógica procesada

    private final JLabel lblOriginal = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblProcessed = new JLabel("", SwingConstants.CENTER);

    private JComboBox<String> comboEE; // selector de elemento estructurante

    public Pantalla() {
        super("Operaciones morfológicas con lattices");
        pant();
    }

    private void pant() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 750);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnCargar = new JButton("Cargar Imagen");
        JButton btnReset = new JButton("Restablecer");
        JButton btnErosion = new JButton("Erosión");
        JButton btnDilatar = new JButton("Dilatación");
        JButton btnApertura = new JButton("Apertura");
        JButton btnCierre = new JButton("Clausura");

        comboEE = new JComboBox<>(new String[]{
            "Diamante 5x5",
            "Diamante 7x7",
            "Disco 7x7"
        });

        top.add(btnCargar);
        top.add(btnReset);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(new JLabel("Elemento estructurante:"));
        top.add(comboEE);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(btnErosion);
        top.add(btnDilatar);
        top.add(btnApertura);
        top.add(btnCierre);

        add(top, BorderLayout.NORTH);

        //labels
        lblOriginal.setBorder(
                BorderFactory.createTitledBorder("Imagen Original (Niveles de gris)"));
        lblProcessed.setBorder(
                BorderFactory.createTitledBorder("Imagen Procesada"));

        lblOriginal.setPreferredSize(new Dimension(600, 600));
        lblProcessed.setPreferredSize(new Dimension(600, 600));

        JScrollPane spLeft = new JScrollPane(lblOriginal);
        JScrollPane spRight = new JScrollPane(lblProcessed);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, spLeft, spRight);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        //botones
        btnCargar.addActionListener(e -> cargarImagen());
        btnReset.addActionListener(e -> resetProcesada());

        btnErosion.addActionListener(e ->
                aplicaMorf(Morfologia::erosion));

        btnDilatar.addActionListener(e ->
                aplicaMorf(Morfologia::dilatacion));

        btnApertura.addActionListener(e ->
                aplicaMorf(Morfologia::apertura));

        btnCierre.addActionListener(e ->
                aplicaMorf(Morfologia::cierre));

        setVisible(true);
    }

    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter(
                    "Imágenes JPG y PNG", "jpg", "jpeg", "png"));

            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            BufferedImage img = ImageIO.read(f);

            if (img == null) {
                JOptionPane.showMessageDialog(
                        this, "Formato de imagen no soportado.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Conversión a escala de grises
            original = ImageUtils.toGray(img);

            // La procesada inicia igual a la original
            procesada = duplicado(original);

            actualizarImg();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Error al cargar imagen: " + ex.getMessage());
        }
    }

    private void resetProcesada() {
        if (original == null) return;
        procesada = duplicado(original);
        actualizarImg();
    }

    private void aplicaMorf(
            java.util.function.BiFunction<BufferedImage, ElementoEstructurante, BufferedImage> op) {

        if (procesada == null) return;

        ElementoEstructurante ee = seleccionarEE();

        BufferedImage resultado = op.apply(duplicado(procesada), ee);

        if (resultado != null) {
            procesada = resultado;
            actualizarImg();
        }
    }

    private void actualizarImg() {

        if (original != null) {
            lblOriginal.setIcon(new ImageIcon(
                    escalado(original, lblOriginal)));
        }

        if (procesada != null) {
            lblProcessed.setIcon(new ImageIcon(
                    escalado(procesada, lblProcessed)));
        }

        revalidate();
        repaint();
    }

    //escalado unicamente para visualización
    private Image escalado(BufferedImage img, JLabel label) {

        Container parent = label.getParent();

        int targetW = parent.getWidth();
        int targetH = parent.getHeight();

        if (targetW <= 0 || targetH <= 0) {
            targetW = 400;
            targetH = 400;
        }

        double scaleW = (double) targetW / img.getWidth();
        double scaleH = (double) targetH / img.getHeight();
        double scale = Math.min(scaleW, scaleH);

        int newW = (int) (img.getWidth() * scale);
        int newH = (int) (img.getHeight() * scale);

        return img.getScaledInstance(
                newW, newH, Image.SCALE_SMOOTH);
    }

    private BufferedImage duplicado(BufferedImage src) {
        BufferedImage copia = new BufferedImage(
                src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = copia.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        return copia;
    }

    private ElementoEstructurante seleccionarEE() {
        String sel = comboEE.getSelectedItem().toString();

        switch (sel) {
            case "Diamante 7x7":
                return ElementoEstructurante.diamante7x7();
            case "Disco 7x7":
                return ElementoEstructurante.disco7x7();
            default:
                return ElementoEstructurante.diamante5x5();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pantalla::new);
    }
}