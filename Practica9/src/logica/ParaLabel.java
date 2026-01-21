package logica;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/*
 En esta clase se redimensiona la imagen para ajustarse al label
*/

public class ParaLabel {

    public static void mostrarEnLabel(JLabel label, BufferedImage img) {
        Image esc = img.getScaledInstance(
                label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(esc));
    }
}
