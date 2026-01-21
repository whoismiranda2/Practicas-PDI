package gui;

import logica.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 *Esta clase contiene la interfaz gráfica de usuario en donde se le permite
  -Cargar la imagen
  -Seleccionar si desea un filtro pasabajas o pasaaltas
  -Para el caso de pasabajas, puede aplicar la selección realizada (suavizado o definición)
  -Para el pasaaltas puede seleccionar los métodos y máscaras disponibles y aplicarlas
  -En los casos donde se desprenden más de dos imágenes, se muestra como imagen procesada
   el resultado de la OR de las imágenes generadas por las diferentes máscaras y las mismas se muestran
   en ventanas emergentes
*/

public class Pantalla extends JFrame {
    
    private BufferedImage originalGray, processed;
    private JLabel lblOriginal, lblProcessed;

    public Pantalla() {
        setTitle("Filtros Lineales");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //imágenes
        lblOriginal = new JLabel("Imagen Original", JLabel.CENTER);
        lblProcessed = new JLabel("Imagen Procesada", JLabel.CENTER);

        JPanel imagesPanel = new JPanel(new GridLayout(1, 2));
        imagesPanel.add(new JScrollPane(lblOriginal));
        imagesPanel.add(new JScrollPane(lblProcessed));

        //BOTÓN CARGA
        JButton load = new JButton("Cargar Imagen");
        load.addActionListener(e -> loadImage());

        //PESTAÑAS
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Pasa-bajas", panelPasaBajas());
        tabs.add("Pasa-altas", panelPasaAltas());

        add(load, BorderLayout.NORTH);
        add(tabs, BorderLayout.WEST);
        add(imagesPanel, BorderLayout.CENTER);
    }


    //PASA BAJAS
    private JPanel panelPasaBajas() {
        JPanel p = new JPanel(new GridLayout(0,1));

        
        JComboBox<String> comboSuavizado = new JComboBox<>(new String[]{
            "Suavizado 7x7",
            "Suavizado 9x9",
            "Suavizado 11x11"
        });

        JButton aplicarSuavizado = new JButton("Aplicar Suavizado");

        aplicarSuavizado.addActionListener(e -> {

            if (originalGray == null) return;

            double[][] kernel = null;

            switch (comboSuavizado.getSelectedItem().toString()) {
                case "Suavizado 7x7":
                    kernel = CargarKernel.suavizado7x7();
                    break;
                case "Suavizado 9x9":
                    kernel = CargarKernel.suavizado9x9();
                    break;
                case "Suavizado 11x11":
                    kernel = CargarKernel.suavizado11x11();
                    break;
            }

            processed = PasaBajas.suavizado(originalGray, kernel);
            showProcessed();
        });

        
        JComboBox<String> comboDef = new JComboBox<>(new String[]{
            "Definición Suave",
            "Definición Media",
            "Definición Fuerte"
        });

        JButton aplicarDef = new JButton("Aplicar Definición");

        aplicarDef.addActionListener(e -> {

            if (originalGray == null) return;

            double[][] kernel = null;

            switch (comboDef.getSelectedItem().toString()) {
                case "Definición Suave":
                    kernel = CargarKernel.definicionSuave();
                    break;
                case "Definición Media":
                    kernel = CargarKernel.definicionMedia();
                    break;
                case "Definición Fuerte":
                    kernel = CargarKernel.definicionFuerte();
                    break;
            }

            processed = PasaBajas.definicion(originalGray, kernel);
            showProcessed();
        });

        p.add(new JLabel("Suavizado"));
        p.add(comboSuavizado);
        p.add(aplicarSuavizado);

        p.add(new JSeparator());

        p.add(new JLabel("Definición"));
        p.add(comboDef);
        p.add(aplicarDef);

        return p;
    }


    //PASA ALTAS
    private JPanel panelPasaAltas() {
        JPanel p = new JPanel(new GridLayout(0,1));

        JComboBox<String> combo = new JComboBox<>(new String[]{
            "Homogeneidad",
            "Diferencia",
            "Roberts",
            "Prewitt",
            "Sobel",
            "Frei-Chen",
            "Prewitt Compás",
            "Kirsch Compás",
            "Robinson niv.3 Compás",
            "Robinson niv.5 Compás",
            "Laplaciano"
        });

        JButton aplicar = new JButton("Aplicar");

        aplicar.addActionListener(e -> {
            String sel = combo.getSelectedItem().toString();
            
            if (originalGray == null) {
                JOptionPane.showMessageDialog(this,
                    "Primero cargue una imagen",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            switch (sel) {
                case "Homogeneidad": {
                    processed = PasaAltas.homogeneity(originalGray);
                    showProcessed();
                    break;
                }
                case "Diferencia": {
                    processed = PasaAltas.difference(originalGray);
                    showProcessed();
                    break;
                }
                case "Sobel": {
                    BufferedImage gx = Convolucion.apply(originalGray, CargarKernel.sobelX());
                    BufferedImage gy = Convolucion.apply(originalGray, CargarKernel.sobelY());

                    new VentanaResultado("Sobel Horizontal (Hx)", gx);
                    new VentanaResultado("Sobel Vertical (Hy)", gy);

                    processed = ImageUtils.or(gx, gy);
                    showProcessed();
                    break;
                }
                case "Prewitt": {
                    BufferedImage gx = Convolucion.apply(originalGray, CargarKernel.prewittX());
                    BufferedImage gy = Convolucion.apply(originalGray, CargarKernel.prewittY());

                    new VentanaResultado("Prewitt Horizontal", gx);
                    new VentanaResultado("Prewitt Vertical", gy);

                    processed = ImageUtils.or(gx, gy);
                    showProcessed();
                    break;
                }
                case "Roberts": {
                    BufferedImage gx = Convolucion.apply(originalGray, CargarKernel.robertsX());
                    BufferedImage gy = Convolucion.apply(originalGray, CargarKernel.robertsY());

                    new VentanaResultado("Roberts Horizontal", gx);
                    new VentanaResultado("Roberts Vertical", gy);

                    processed = ImageUtils.or(gx, gy);
                    showProcessed();
                    break;
                }
                case "Frei-Chen": {
                    BufferedImage gx = Convolucion.apply(originalGray, CargarKernel.freiChenX());
                    BufferedImage gy = Convolucion.apply(originalGray, CargarKernel.freiChenY());

                    new VentanaResultado("Frei-Chen Horizontal", gx);
                    new VentanaResultado("Frei-Chen Vertical", gy);

                    processed = ImageUtils.or(gx, gy);
                    showProcessed();
                    break;
                }
                case "Prewitt Compás": {
                    BufferedImage[] imgs = PasaAltas.prewittOrientaciones(originalGray);

                    String[] nombres = {
                        "Prewitt Este",
                        "Prewitt Noreste",
                        "Prewitt Norte",
                        "Prewitt Noroeste",
                        "Prewitt Oeste",
                        "Prewitt Suroeste",
                        "Prewitt Sur",
                        "Prewitt Sureste"
                    };

                    for (int i = 0; i < imgs.length; i++) {
                        new VentanaResultado(nombres[i], imgs[i]);
                    }

                    processed = PasaAltas.prewittCompas(originalGray);
                    showProcessed();
                    break;
                }
                case "Kirsch Compás": {
                    BufferedImage[] imgs = PasaAltas.kirschOrientaciones(originalGray);

                    String[] nombres = {
                        "Kirsch Este",
                        "Kirsch Noreste",
                        "Kirsch Norte",
                        "Kirsch Noroeste",
                        "Kirsch Oeste",
                        "Kirsch Suroeste",
                        "Kirsch Sur",
                        "Kirsch Sureste"
                    };

                    for (int i = 0; i < imgs.length; i++) {
                        new VentanaResultado(nombres[i], imgs[i]);
                    }

                    processed = PasaAltas.kirschCompas(originalGray);
                    showProcessed();
                    break;
                }
                case "Robinson niv.3 Compás": {
                    BufferedImage[] imgs = PasaAltas.robinson3Orientaciones(originalGray);

                    String[] nombres = {
                        "Robinson 3 Este",
                        "Robinson 3 Noreste",
                        "Robinson 3 Norte",
                        "Robinson 3 Noroeste",
                        "Robinson 3 Oeste",
                        "Robinson 3 Suroeste",
                        "Robinson 3 Sur",
                        "Robinson 3 Sureste"
                    };

                    for (int i = 0; i < imgs.length; i++) {
                        new VentanaResultado(nombres[i], imgs[i]);
                    }

                    processed = PasaAltas.robinson3Compas(originalGray);
                    showProcessed();
                    break;
                }
                case "Robinson niv.5 Compás": {
                    BufferedImage[] imgs = PasaAltas.robinson5Orientaciones(originalGray);

                    String[] nombres = {
                        "Robinson 5 Este",
                        "Robinson 5 Noreste",
                        "Robinson 5 Norte",
                        "Robinson 5 Noroeste",
                        "Robinson 5 Oeste",
                        "Robinson 5 Suroeste",
                        "Robinson 5 Sur",
                        "Robinson 5 Sureste"
                    };

                    for (int i = 0; i < imgs.length; i++) {
                        new VentanaResultado(nombres[i], imgs[i]);
                    }

                    processed = PasaAltas.robinson5Compas(originalGray);
                    showProcessed();
                    break;
                }
                case "Laplaciano": {
                    processed = PasaAltas.laplacian(originalGray);
                    showProcessed();
                    break;
                }

                default:
                    JOptionPane.showMessageDialog(this,
                        "Operador no implementado",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        p.add(combo);
        p.add(aplicar);
        return p;
    }

    private void loadImage() {
        try {
            JFileChooser fc = new JFileChooser();

            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter(
                            "Imágenes (*.jpg, *.png, *.bmp)",
                            "jpg", "jpeg", "png", "bmp"
                    );

            fc.setFileFilter(filter);
            fc.setAcceptAllFileFilterUsed(false); 

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());
                originalGray = ImageUtils.toGray(img);
                lblOriginal.setIcon(new ImageIcon(originalGray));
                lblProcessed.setIcon(null);

                if (img == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Formato de imagen no soportado",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar la imagen",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showProcessed() {
        lblProcessed.setIcon(new ImageIcon(processed));
    }

    
    public static void main(String[] args) {
        new Pantalla().setVisible(true);
    }
}