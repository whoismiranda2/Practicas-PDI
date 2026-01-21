package gui;

import logica.ProcesarImagen;
import logica.Morf;
import logica.AplicaRuido;
import logica.Esqueletizado;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Interfaz gráfica de usuario, clase que contiene el main
 * desde aquí se configura lo necesario para que el usuario interactúe con la aplicación
 * hace las llamadas e inserciones requeridas para llevar los métodos realizados desde
 * la lógica
 * incluye un botón de reset para eliminar las operaciones realizadas en la imagen
 */

public class Pantalla extends JFrame {

    private BufferedImage originalBIN;
    private BufferedImage procesadaBIN;

    private final JLabel lblOriginal = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblProcessed = new JLabel("", SwingConstants.CENTER);

    private final JSlider sliderRuido = new JSlider(0, 50, 5);

    private final int umbral = 128;     //umbral fijo para realizar la binarización

    public Pantalla() {
        super("Operaciones morfológicas binarias");
        pant();
    }

    private void pant() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 750);
        setLocationRelativeTo(null);

        // TOP 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnCargar = new JButton("Cargar Imagen");
        JButton btnReset = new JButton("Restablecer");
        JButton btnErosion = new JButton("Erosión");
        JButton btnDilatar = new JButton("Dilatación");
        JButton btnApertura = new JButton("Apertura");
        JButton btnCierre = new JButton("Clausura");
        JButton btnSal = new JButton("Ruido Sal");
        JButton btnPimienta = new JButton("Ruido Pimienta");
        JButton btnQuitarR = new JButton("Eliminar Ruido");
        JButton btnEsquel = new JButton("Esqueletizar");

        sliderRuido.setMajorTickSpacing(10);
        sliderRuido.setPaintTicks(true);
        sliderRuido.setPaintLabels(true);

        top.add(btnCargar);
        top.add(btnReset);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(btnErosion);
        top.add(btnDilatar);
        top.add(btnApertura);
        top.add(btnCierre);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(new JLabel("Ruido %:"));
        top.add(sliderRuido);
        top.add(btnSal);
        top.add(btnPimienta);
        top.add(btnQuitarR);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(btnEsquel);

        add(top, BorderLayout.NORTH);

        //  LABELS
        lblOriginal.setBorder(BorderFactory.createTitledBorder("Imagen Original (Binarizada)"));
        lblProcessed.setBorder(BorderFactory.createTitledBorder("Imagen Procesada"));

        lblOriginal.setPreferredSize(new Dimension(600, 600));
        lblProcessed.setPreferredSize(new Dimension(600, 600));

        JScrollPane spLeft = new JScrollPane(lblOriginal);
        JScrollPane spRight = new JScrollPane(lblProcessed);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLeft, spRight);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        // BOTONES 
        btnCargar.addActionListener(e -> cargarImagen());
        btnReset.addActionListener(e -> resetProcesada());
        btnErosion.addActionListener(e -> aplicaMorf(Morf::erosion));
        btnDilatar.addActionListener(e -> aplicaMorf(Morf::dilatacion));
        btnApertura.addActionListener(e -> aplicaMorf(Morf::apertura));
        btnCierre.addActionListener(e -> aplicaMorf(Morf::cierre));
        btnSal.addActionListener(e -> aplicaRuido(true));
        btnPimienta.addActionListener(e -> aplicaRuido(false));
        btnQuitarR.addActionListener(e -> quitarRuido());
        btnEsquel.addActionListener(e -> esqueletizado());

        setVisible(true);
    }

    //CARGA Y BINARIZA
    private void cargarImagen() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Imágenes JPG, PNG", "jpg", "jpeg", "png"));
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File f = fc.getSelectedFile();
            BufferedImage img = ImageIO.read(f);
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Formato de imagen no soportado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BufferedImage gris = ProcesarImagen.grises(img);
            originalBIN = ProcesarImagen.binariza(gris, umbral);
            procesadaBIN = duplicado(originalBIN);

            actualizarImg();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar imagen: " + ex.getMessage());
        }
    }

    // RESTABLECER
    private void resetProcesada() {
        if (originalBIN == null) return;
        procesadaBIN = duplicado(originalBIN);
        actualizarImg();
    }

    // MORF
    private void aplicaMorf(java.util.function.Function<BufferedImage, BufferedImage> op) {
        if (procesadaBIN == null) return;
        BufferedImage resultado = op.apply(duplicado(procesadaBIN));
        if (resultado != null) {
            procesadaBIN = resultado;
            actualizarImg();
        }
    }

    // RUIDO 
    private void aplicaRuido(boolean salt) {
        if (procesadaBIN == null) return;
        int porcentaje = sliderRuido.getValue();
        BufferedImage copy = duplicado(procesadaBIN);
        procesadaBIN = salt ? AplicaRuido.sal(copy, porcentaje) : AplicaRuido.pimienta(copy, porcentaje);
        actualizarImg();
    }

    // QUITAR RUIDO 
    private void quitarRuido() {
        if (procesadaBIN == null) return;

        long blanco = 0, negro = 0;
        for (int y = 0; y < procesadaBIN.getHeight(); y++) {
            for (int x = 0; x < procesadaBIN.getWidth(); x++) {
                int v = procesadaBIN.getRGB(x, y) & 0xff;
                if (v == 255) blanco++; else negro++;
            }
        }

        if (negro > blanco) {
            aplicaMorf(Morf::apertura);
            JOptionPane.showMessageDialog(this, "Fondo oscuro → Apertura");
        } else {
            aplicaMorf(Morf::cierre);
            JOptionPane.showMessageDialog(this, "Fondo claro → Clausura");
        }
    }

    // ESQUELETO 
    private void esqueletizado() {
        if (procesadaBIN == null) return;
        procesadaBIN = Esqueletizado.esqueleto(duplicado(procesadaBIN));
        actualizarImg();
    }

    //  MOSTRAR 
    private void actualizarImg() {

        if (originalBIN != null) {
            lblOriginal.setIcon(new ImageIcon(escalado(originalBIN, lblOriginal)));
        }

        if (procesadaBIN != null) {
            lblProcessed.setIcon(new ImageIcon(escalado(procesadaBIN, lblProcessed)));
        }

        revalidate();
        repaint();
    }

    //  ESCALADO
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

        return img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    //  DEEP COPY 
    private BufferedImage duplicado(BufferedImage src) {
        BufferedImage copia = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = copia.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copia;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pantalla::new);
    }
}