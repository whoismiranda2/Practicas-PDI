package gui;

import logica.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * En esta clase, se programa la interfaz con la que el ususario interactua, aqui se implementan las clases de la lógica
 * Puntos importantes a mencionar son:
 * Debido a la complejidad del algoritmo, trabaja mejor con imágenes pequeñas, por lo tanto, se redimensiona la imagen
 *   al momento de ser cargada a una dimensión(tamaño) de 128x128 ya que, probando, fué la dimensión a la cual se alcanza a 
 *   apreciar la imagen y el algoritmo responde a un tiempo considerable.
 * Se le permite al ususario almacenar la imagen filtrada, pensado principalmente con el objetivo de abrir la imagen en
 *   un editor y visualizar con más detalle la imagen filtrada (haciendo zoom).
 * También, aquí se propone un Do=10, si se deseara cambiar, debe de realizarse manualmente en la parte dentro de process() 
 *   donde se llama al FiltroFrecuencia.
 * Solo permite cargar archivos con extensiones de imágen comunes.
 * @author andre
 */

public class Pantalla extends JFrame {

    private BufferedImage originalGray;
    private JLabel lblOriginal, lblResult;

    public Pantalla() {
        setTitle("Filtro en el dominio de la frecuencia");
        setSize(1200, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        lblOriginal = new JLabel("Original", JLabel.CENTER);
        lblResult = new JLabel("Resultado", JLabel.CENTER);

        JPanel images = new JPanel(new GridLayout(1, 2));
        images.add(new JScrollPane(lblOriginal));
        images.add(new JScrollPane(lblResult));

        JButton load = new JButton("Cargar Imagen");
        load.addActionListener(e -> loadImage());

        JButton apply = new JButton("Aplicar Fourier + Gauss");
        apply.addActionListener(e -> process());
        
        JPanel controls = new JPanel();
        controls.add(load);
        controls.add(apply);
        
        JButton save = new JButton("Guardar Resultado");
        save.addActionListener(e -> saveImage());
        controls.add(save);

        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
    }

    private BufferedImage resizeTo64(BufferedImage img) {
        int size = 128;

        Image tmp = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(
                size, size, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    
    private void loadImage() {
        try {
            JFileChooser fc = new JFileChooser();

            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter(
                            "Archivos de imagen (*.png, *.jpg, *.jpeg, *.bmp)",
                            "png", "jpg", "jpeg", "bmp"
                    );

            fc.setFileFilter(filter);
            fc.setAcceptAllFileFilterUsed(false); 

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                BufferedImage img = ImageIO.read(fc.getSelectedFile());

                BufferedImage small = resizeTo64(img);

                originalGray = ImageUtils.toGray(small);

                lblOriginal.setIcon(new ImageIcon(originalGray));
                lblResult.setIcon(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void process() {
        double[][] f = ImageUtils.imageToMatrix(originalGray);

        Complex[][] F = Fourier.dft2D(f);
        new VentanaResultado("DDF Imagen Original", ImageUtils.magnitudeImage(F));
        
        double[][] H = FiltroFrecuencia.gaussiano(f.length, f[0].length, 10);
        Complex[][] G = FiltroFrecuencia.aplicar(F, H);

        double[][] g = Fourier.idft2D(G);
        BufferedImage out = ImageUtils.matrixToImage(g);
        lblResult.setIcon(new ImageIcon(out));
        
        Complex[][] F_R = FiltroFrecuencia.aplicar(F, H);
        new VentanaResultado("DDF Imagen Filtrada", ImageUtils.magnitudeImage(G));

    }

    private void saveImage() {
        try {
            JFileChooser fc = new JFileChooser();

            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("Imagen PNG (*.png)", "png");
            fc.setFileFilter(filter);

            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();

                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }

                BufferedImage img =
                        (BufferedImage) ((ImageIcon) lblResult.getIcon()).getImage();

                ImageIO.write(img, "png", file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Pantalla().setVisible(true);
    }
}