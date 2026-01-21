package gui;

import logica.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * Interfaz gráfica de usuario, esta clase contiene el Main
 * permite que se cargue la imagen a la que se desea aplicar el algoritmo de canny
 * aplica las clases de la lógica
*/

public class Pantalla extends JFrame {

    private JLabel lblOriginal, lblResult;
    private BufferedImage image;

    public Pantalla() {
        setTitle("Canny 1D - Detector de Bordes");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton btnLoad = new JButton("Cargar Imagen");
        JButton btnCanny = new JButton("Aplicar Canny");

        JPanel top = new JPanel();
        top.add(btnLoad);
        top.add(btnCanny);

        lblOriginal = new JLabel("Imagen Original", JLabel.CENTER);
        lblResult = new JLabel("Resultado Canny", JLabel.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(new JScrollPane(lblOriginal));
        center.add(new JScrollPane(lblResult));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadImage());
        btnCanny.addActionListener(e -> applyCanny());
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

                image = ImageIO.read(fc.getSelectedFile());

                if (image == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Formato de imagen no soportado",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                Image scaled = ImageUtils.scaleImage(
                        image,
                        lblOriginal.getWidth(),
                        lblOriginal.getHeight()
                );

                lblOriginal.setIcon(new ImageIcon(scaled));
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

    private void applyCanny() {
        if (image != null) {
            BufferedImage result = Canny.process(image);

            Image scaled = ImageUtils.scaleImage(
                    result,
                    lblResult.getWidth(),
                    lblResult.getHeight()
            );

            lblResult.setIcon(new ImageIcon(scaled));
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Pantalla().setVisible(true);
        });
    }
}
