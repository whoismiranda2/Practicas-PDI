package gui;

import javax.swing.*;
import java.awt.image.BufferedImage;

/*
 Clase para mostrar en ventanas emergentes un resultado, he decidido que lo que se muestre sean los espectros
 obtenidos (para la imagen original y la filtrada)
*/

public class VentanaResultado extends JFrame {

    public VentanaResultado(String titulo, BufferedImage img) {
        setTitle(titulo);
        setSize(400, 400);
        setLocationRelativeTo(null);
        add(new JScrollPane(new JLabel(new ImageIcon(img))));
        setVisible(true);
    }
}
