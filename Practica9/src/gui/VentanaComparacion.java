package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
Esta clase ayuda a mostrar las imagenes generadas por la comparación en una ventana nueva
*/

public class VentanaComparacion extends JDialog {

    private JLabel lblImagen;

    public VentanaComparacion(JFrame padre, BufferedImage imagenComparada) {
        super(padre, "Comparación ==", false);
        setSize(500, 500);
        setLocationRelativeTo(padre);
        initComponentes(imagenComparada);
    }

    private void initComponentes(BufferedImage imagen) {

        lblImagen = new JLabel("", JLabel.CENTER);
        lblImagen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Image imgEscalada = imagen.getScaledInstance(
                450, 450, Image.SCALE_SMOOTH);

        lblImagen.setIcon(new ImageIcon(imgEscalada));
        add(lblImagen, BorderLayout.CENTER);
    }
}