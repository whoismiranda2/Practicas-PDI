package gui;

import javax.swing.*;
import java.awt.image.BufferedImage;

/*
 Esta otra clase de la gui permite mostrar las imagenes resultantes de cada máscara
*/

public class VentanaResultado extends JFrame {

    public VentanaResultado(String titulo, BufferedImage img) {
        setTitle(titulo);
        setSize(400, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel label = new JLabel(new ImageIcon(img));
        add(new JScrollPane(label));

        setVisible(true);
    }
}