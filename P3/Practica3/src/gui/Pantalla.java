package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import logica.Practica3;
import java.io.File;

public class Pantalla extends JFrame {

    private JLabel lblOriginal, lblReferencia, lblProcesada;
    private JButton btnCargar, btnConvertir, btnCanalesColor, btnCanalesGris, btnApplyTransfer;
    private JComboBox<String> comboConversiones;
    private Practica3 conversor;
    private BufferedImage imagenReferencia;
    private JPanel referencePanel;

    public Pantalla() {
        super("Conversión entre Modelos de Color");

        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel();
        btnCargar = new JButton("Cargar Imagen");
        comboConversiones = new JComboBox<>(new String[]{
            "RGB a CMY",
            "CMY a RGB",
            "CMY a CMYK",
            "RGB a YIQ",
            "YIQ a RGB",
            "RGB a HSI",
            "HSI a RGB",
            "RGB a HSV",
            "HSV a RGB",
            "RGB a Cie Lab",
            "Cie Lab a RGB"
        });
        btnConvertir = new JButton("Aplicar Conversión");
        JButton btnCargarReferencia = new JButton("Cargar Referencia");
        btnCanalesColor = new JButton("Extraer Canales (Color)");
        btnCanalesGris = new JButton("Extraer Canales (Gris)");
        btnApplyTransfer = new JButton("Aplicar Transferencia"); 

        //oculto
        btnApplyTransfer.setVisible(false);

        panelSuperior.add(btnCargar);
        panelSuperior.add(btnCargarReferencia);
        panelSuperior.add(comboConversiones);
        panelSuperior.add(btnConvertir);
        panelSuperior.add(btnCanalesColor);
        panelSuperior.add(btnCanalesGris);
        panelSuperior.add(btnApplyTransfer);

        add(panelSuperior, BorderLayout.NORTH);

        //panel
        JPanel panelImagenes = new JPanel(new GridLayout(1, 3));
        lblOriginal = new JLabel("Imagen Original", SwingConstants.CENTER);
        lblReferencia = new JLabel("Referencia", JLabel.CENTER);
        lblProcesada = new JLabel("Imagen Procesada", SwingConstants.CENTER);

        panelImagenes.add(new JScrollPane(lblOriginal));
        panelImagenes.add(new JScrollPane(lblProcesada));
        
        referencePanel = new JPanel(new BorderLayout());
        referencePanel.add(lblReferencia, BorderLayout.CENTER);
        referencePanel.setVisible(false); //se muestra al cargar referencia
        
        panelImagenes.add(referencePanel);
        add(panelImagenes, BorderLayout.CENTER);

        //acciones
        btnCargar.addActionListener(e -> cargarImagen());

        btnCargarReferencia.addActionListener(e -> cargarImagenReferencia());

        btnConvertir.addActionListener(e -> aplicarConversion());

        btnCanalesColor.addActionListener(e -> extraerCanales(true));

        btnCanalesGris.addActionListener(e -> extraerCanales(false));

        btnApplyTransfer.addActionListener(e -> aplicarTransferencia());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarImagen() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes JPG, PNG", "jpg", "jpeg", "png"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = selector.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                conversor = new Practica3(img);
                lblOriginal.setIcon(new ImageIcon(img.getScaledInstance(400, -1, Image.SCALE_SMOOTH)));
                lblProcesada.setIcon(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cargando imagen: " + ex.getMessage());
            }
        }
    }

    private void cargarImagenReferencia() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes JPG, PNG", "jpg", "jpeg", "png"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = selector.getSelectedFile();
                imagenReferencia = ImageIO.read(f);
                lblReferencia.setIcon(new ImageIcon(imagenReferencia.getScaledInstance(400, -1, Image.SCALE_SMOOTH)));
                referencePanel.setVisible(true);   //mostrar panel de referencia
                btnApplyTransfer.setVisible(true); //mostrar botón de transferencia
                revalidate();
                repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cargando referencia: " + ex.getMessage());
            }
        }
    }

    private void aplicarConversion() {
        if (conversor == null) return;

        String seleccion = (String) comboConversiones.getSelectedItem();
        BufferedImage resultado = null;

        switch (seleccion) {
            case "RGB a CMY":
                resultado = conversor.convertirRGBaCMY();
                break;
            case "CMY a RGB":
                resultado = conversor.convertirCMYaRGB();
                break;
            case "CMY a CMYK":
                resultado = conversor.convertirCMYaCMYK();
                break;
            case "RGB a YIQ":
                resultado = conversor.convertirRGBaYIQ();
                break;
            case "YIQ a RGB":
                resultado = conversor.convertirYIQaRGB();
                break;
            case "RGB a HSI":
                resultado = conversor.convertirRGBaHSI();
                break;
            case "HSI a RGB":
                resultado = conversor.convertirHSIaRGB();
                break;
            case "RGB a HSV":
                resultado = conversor.convertirRGBaHSV();
                break;
            case "HSV a RGB":
                resultado = conversor.convertirHSVaRGB();
                break;
            case "RGB a Cie Lab":
                resultado = conversor.convertirRGBaLab();
                break;
            case "Cie Lab a RGB":
                resultado = conversor.convertirLabaRGB();
                break;
        }

        if (resultado != null) {
            lblProcesada.setIcon(new ImageIcon(resultado.getScaledInstance(400, -1, Image.SCALE_SMOOTH)));
        }
    }

    private void aplicarTransferencia() {
        if (conversor == null || imagenReferencia == null) {
            JOptionPane.showMessageDialog(this, "Carga imagen original y de referencia");
            return;
        }

        BufferedImage resultado = conversor.transferirColor(imagenReferencia);
        if (resultado != null) {
            lblProcesada.setIcon(new ImageIcon(resultado.getScaledInstance(400, -1, Image.SCALE_SMOOTH)));
        }
    }

    private void extraerCanales(boolean enColor) {
        if (conversor == null) return;
        BufferedImage img = conversor.getImagenProcesada();
        if (img == null) return;

        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage canal1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage canal2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage canal3 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color c = new Color(img.getRGB(x, y));
                int v1 = c.getRed();
                int v2 = c.getGreen();
                int v3 = c.getBlue();

                if (enColor) {
                    canal1.setRGB(x, y, new Color(v1, 0, 0).getRGB());
                    canal2.setRGB(x, y, new Color(0, v2, 0).getRGB());
                    canal3.setRGB(x, y, new Color(0, 0, v3).getRGB());
                } else {
                    canal1.setRGB(x, y, new Color(v1, v1, v1).getRGB());
                    canal2.setRGB(x, y, new Color(v2, v2, v2).getRGB());
                    canal3.setRGB(x, y, new Color(v3, v3, v3).getRGB());
                }
            }
        }

        mostrarImagen("Canal 1", canal1);
        mostrarImagen("Canal 2", canal2);
        mostrarImagen("Canal 3", canal3);
    }

    private void mostrarImagen(String titulo, BufferedImage img) {
        JFrame f = new JFrame(titulo);
        f.add(new JLabel(new ImageIcon(img.getScaledInstance(300, -1, Image.SCALE_SMOOTH))));
        f.pack();
        f.setLocationRelativeTo(this);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Pantalla());
    }
}