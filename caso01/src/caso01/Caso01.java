package caso01;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

//cargar imagen, convertirla y mostrarla en gris, poder seleccionar el ruido y verlo aplicado en una nueva ventana

public class Caso01 extends JFrame {

    private JLabel labelImagen;
    private JComboBox<String> comboTipoRuido;
    private BufferedImage imagenGris;
    private static final Random generadorAleatorio = new Random();

    public Caso01() {
        super("Adicionar ruido a la imagen");

        labelImagen = new JLabel("Cargue una imagen...", JLabel.CENTER);
        comboTipoRuido = new JComboBox<>(new String[]{
            "Seleccionar ruido",
            "Ruido Senoidal",
            "Ruido Gamma",
            "Ruido Gaussiano",
            "Ruido Exponencial",
            "Ruido Rayleigh",
            "Ruido Sal y Pimienta",
            "Ruido Uniforme"
        });

        JButton botonCargar = new JButton("Cargar Imagen");

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(botonCargar);
        panelSuperior.add(comboTipoRuido);

        add(panelSuperior, BorderLayout.NORTH);  //principal
        add(new JScrollPane(labelImagen), BorderLayout.CENTER);

        botonCargar.addActionListener(e -> {
            JFileChooser selectorArchivo = new JFileChooser();
            if (selectorArchivo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage imagenOriginal = ImageIO.read(new File(selectorArchivo.getSelectedFile().getAbsolutePath()));
                    imagenGris = convertirEscalaGrises(imagenOriginal);
                    mostrarImagenEscalaGrises(imagenGris);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al cargar imagen");
                }
            }
        });

        //seleccion del ruido
        comboTipoRuido.addActionListener(e -> {
            if (imagenGris == null) return;
            String tipoSeleccionado = (String) comboTipoRuido.getSelectedItem();
            if (tipoSeleccionado.equals("Seleccionar ruido")) return;

            BufferedImage imagenConRuido = null;
            switch (tipoSeleccionado) {
                case "Ruido Senoidal":
                    imagenConRuido = aplicarRuidoSenoidal(imagenGris);
                    break;
                case "Ruido Gamma":
                    imagenConRuido = aplicarRuidoGamma(imagenGris, 2.0, 2.0);
                    break;
                case "Ruido Gaussiano":
                    imagenConRuido = aplicarRuidoGaussiano(imagenGris, 0, 30);
                    break;
                case "Ruido Exponencial":
                    imagenConRuido = aplicarRuidoExponencial(imagenGris, 0.1);
                    break;
                case "Ruido Rayleigh":
                    imagenConRuido = aplicarRuidoRayleigh(imagenGris, 30);
                    break;
                case "Ruido Sal y Pimienta":
                    imagenConRuido = aplicarRuidoSalPimienta(imagenGris, 0.05);
                    break;
                case "Ruido Uniforme":
                    imagenConRuido = aplicarRuidoUniforme(imagenGris, -50, 50);
                    break;
            }
            if (imagenConRuido != null) {
                mostrarEnNuevaVentana(imagenConRuido, tipoSeleccionado);
            }
        });

        // Configurar ventana principal
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private BufferedImage convertirEscalaGrises(BufferedImage imagenOriginal) {
        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorRGB = imagenOriginal.getRGB(columna, fila);
                int canalRojo = (valorRGB >> 16) & 0xff;
                int canalVerde = (valorRGB >> 8) & 0xff;
                int canalAzul = valorRGB & 0xff;
                int valorGris = (canalRojo + canalVerde + canalAzul) / 3;  //promedio simple para escala de grises
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorGris << 16) | (valorGris << 8) | valorGris);
            }
        }
        return imagenSalida;
    }

    private void mostrarImagenEscalaGrises(BufferedImage imagen) {  //imagen gris original
        ImageIcon icono = new ImageIcon(imagen.getScaledInstance(labelImagen.getWidth(), labelImagen.getHeight(), Image.SCALE_SMOOTH));
        labelImagen.setIcon(icono);
        labelImagen.setText("");
    }

    private void mostrarEnNuevaVentana(BufferedImage imagen, String titulo) { //mostrar la imagen con ruido
        JFrame ventanaNueva = new JFrame("Imagen con " + titulo);
        JLabel labelImagenNueva = new JLabel(new ImageIcon(imagen));
        ventanaNueva.add(new JScrollPane(labelImagenNueva));
        ventanaNueva.setSize(600, 500);
        ventanaNueva.setLocationRelativeTo(this);
        ventanaNueva.setVisible(true);
    }

    //Métodos para aplicar los distintos ruidos

    private static BufferedImage aplicarRuidoSenoidal(BufferedImage imagenEntrada) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        double frecuenciaSenoidal = 0.1;
        double amplitudSenoidal = 50;
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double valorRuido = amplitudSenoidal * Math.sin(2 * Math.PI * frecuenciaSenoidal * columna);
                int valorNuevo = limitarValor(valorGris + (int) valorRuido);
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static BufferedImage aplicarRuidoGamma(BufferedImage imagenEntrada, double alpha, double beta) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double valorRuido = generarValorGamma(alpha, beta);
                int valorNuevo = limitarValor((int) (valorGris + valorRuido));
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static double generarValorGamma(double alpha, double beta) {
        if (alpha < 1) {
            return generarValorGamma(alpha + 1, beta) * Math.pow(generadorAleatorio.nextDouble(), 1.0 / alpha);
        }
        double d = alpha - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9 * d);
        while (true) {
            double x, v;
            do {
                x = generadorAleatorio.nextGaussian();
                v = 1 + c * x;
            } while (v <= 0);
            v = v * v * v;
            double u = generadorAleatorio.nextDouble();
            if (u < 1 - 0.0331 * x * x * x * x) return d * v / beta;
            if (Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v)))
                return d * v / beta;
        }
    }

    private static BufferedImage aplicarRuidoGaussiano(BufferedImage imagenEntrada, double media, double desviacion) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double valorRuido = media + desviacion * generadorAleatorio.nextGaussian();
                int valorNuevo = limitarValor((int) (valorGris + valorRuido));
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static BufferedImage aplicarRuidoExponencial(BufferedImage imagenEntrada, double lambda) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double valorRuido = -Math.log(1 - generadorAleatorio.nextDouble()) / lambda;
                int valorNuevo = limitarValor((int) (valorGris + valorRuido));
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static BufferedImage aplicarRuidoRayleigh(BufferedImage imagenEntrada, double sigma) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                double u = generadorAleatorio.nextDouble();
                double valorRuido = sigma * Math.sqrt(-2 * Math.log(1 - u));
                int valorNuevo = limitarValor((int) (valorGris + valorRuido));
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static BufferedImage aplicarRuidoSalPimienta(BufferedImage imagenEntrada, double probabilidad) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                if (generadorAleatorio.nextDouble() < probabilidad) {
                    int valorImpulsivo = generadorAleatorio.nextBoolean() ? 0 : 255;  // Pimienta (0) o sal (255)
                    imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorImpulsivo << 16) | (valorImpulsivo << 8) | valorImpulsivo);
                } else {
                    imagenSalida.setRGB(columna, fila, imagenEntrada.getRGB(columna, fila));
                }
            }
        }
        return imagenSalida;
    }

    private static BufferedImage aplicarRuidoUniforme(BufferedImage imagenEntrada, int minimo, int maximo) {
        int ancho = imagenEntrada.getWidth();
        int alto = imagenEntrada.getHeight();
        BufferedImage imagenSalida = new BufferedImage(ancho, alto, imagenEntrada.getType());
        for (int fila = 0; fila < alto; fila++) {
            for (int columna = 0; columna < ancho; columna++) {
                int valorGris = (imagenEntrada.getRGB(columna, fila) >> 16) & 0xff;
                int valorRuido = minimo + generadorAleatorio.nextInt(maximo - minimo + 1);
                int valorNuevo = limitarValor(valorGris + valorRuido);
                imagenSalida.setRGB(columna, fila, (0xff << 24) | (valorNuevo << 16) | (valorNuevo << 8) | valorNuevo);
            }
        }
        return imagenSalida;
    }

    private static int limitarValor(int valor) {
        return Math.max(0, Math.min(255, valor));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Caso01());
    }
}
