package gui;

import logica.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Clase en donde se realiza la interfaz con la que el usuario interactúa
 * Se le permite cargar archivos con estensiones de imagenes comunes, al selecionar la imagen, se muestra en pantalla
 *   redimensionada y en escala de grises
 * El usuario puede seleccionar de la lista los filtros disponibles indicando entre paréntesis el ruido que aplica
 * Al seleccionar un filtro se activan los parámetros que se pueden modificar para aplicar el ruido
 *   El usuario puede "jugar" con los parámetros, aplicar los cambios y filtrar la imagen
 *   Esto está pensado con el propósito de que se visualice cómo se comportan los filtros según la cantidad de 
 *   ruido presente en la imagen
 * Una vez que se filtra la imagen, el usuario puede seleccionar los botones para visualizar las comparaciones
 *   entre la imágenes, estando disponibles:
 *    -Original vs Ruido
 *    -Original vs Filtrada
 *    -Ruido vs Filtrada
 * También, se puede visualizar en forma de mensaje el resultado de píxeles modificados entre las imágenes, el porcentaje
 *   de píxeles modificados y el porcentaje de reducción de píxeles modificados después del filtrado.
 * 
 * @author andre
 */

public class Pantalla extends JFrame {

    private BufferedImage imgOriginal, imgRuido, imgFiltrada;

    private JLabel lblOriginal, lblRuido, lblFiltrada;
    private JComboBox<String> comboFiltros;

    private JSpinner spMedia, spSigma, spProb, spMin, spMax, spAlpha, spBeta, spLambda;
    private JPanel panelParametros; 

    public Pantalla() {
        setTitle("Filtros no lineales");
        setSize(1370, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponentes();
    }

    private void initComponentes() {

        lblOriginal = crearLabel("Original (Grises)");
        lblRuido = crearLabel("Con ruido");
        lblFiltrada = crearLabel("Filtrada");

        JPanel panelImagenes = new JPanel(new GridLayout(1, 3, 10, 10));
        panelImagenes.add(lblOriginal);
        panelImagenes.add(lblRuido);
        panelImagenes.add(lblFiltrada);

        comboFiltros = new JComboBox<>(new String[]{
                "Mediana (Sal y pimienta)",
                "Media aritmética (Rayleigh)",
                "Máximo (Uniforme)",
                "Mínimo (Gamma)",
                "Punto medio (Gauss)",
                "Alfa trimmed (Gauss)",
                "Armónico (Gamma)",
                "Contra-armónico (Gauss)",
                "Geométrico (Exponencial negativo)",
                "Máximo–mínimo (Sin ruido)"
        });

        JButton btnCargar = new JButton("Cargar imagen");
        JButton btnProcesar = new JButton("Procesar");
        JButton btnAplicarRuido = new JButton("Aplicar cambios de ruido"); 
        JButton btnComparar = new JButton("Comparar (Ruido | Filtrada)");
        JButton btnComparar2 = new JButton("Comparar (Ruido | Original)");
        JButton btnComparar3 = new JButton("Comparar (Filtrada | Original)");
        JButton btnAnalisis = new JButton("Análisis de píxeles");

        JPanel panelControles = new JPanel();
        panelControles.add(btnCargar);
        panelControles.add(comboFiltros);
        panelControles.add(btnAplicarRuido); 
        panelControles.add(btnProcesar);
        panelControles.add(btnComparar);
        panelControles.add(btnComparar2);
        panelControles.add(btnComparar3);
        panelControles.add(btnAnalisis);

        add(panelImagenes, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);
        add(crearPanelParametros(), BorderLayout.NORTH);

        btnCargar.addActionListener(e -> cargarImagen());

        comboFiltros.addActionListener(e -> {
            if (imgOriginal == null) return;

            String filtro = comboFiltros.getSelectedItem().toString();
            panelParametros.setVisible(true);
            actualizarParametrosVisibles(filtro);
            aplicarRuido();
        });

        btnAplicarRuido.addActionListener(e -> aplicarRuido());
        btnProcesar.addActionListener(e -> aplicarFiltro());
        btnComparar.addActionListener(e -> mostrarComparacion1());
        btnComparar2.addActionListener(e -> mostrarComparacion2());
        btnComparar3.addActionListener(e -> mostrarComparacion3());
        btnAnalisis.addActionListener(e -> mostrarAnalisisPixeles());
    }

    private JPanel crearPanelParametros() {

        panelParametros = new JPanel(new GridLayout(4, 4, 5, 5));
        panelParametros.setBorder(
                BorderFactory.createTitledBorder("Parámetros del ruido")
        );

        spMedia = new JSpinner(new SpinnerNumberModel(0, -50, 50, 1));
        spSigma = new JSpinner(new SpinnerNumberModel(20, 1, 50, 1));
        spProb = new JSpinner(new SpinnerNumberModel(0.05, 0.01, 0.2, 0.01));
        spMin = new JSpinner(new SpinnerNumberModel(-20, -50, 0, 1));
        spMax = new JSpinner(new SpinnerNumberModel(20, 0, 50, 1));
        spAlpha = new JSpinner(new SpinnerNumberModel(2.0, 1.0, 5.0, 0.1));
        spBeta = new JSpinner(new SpinnerNumberModel(2.0, 1.0, 5.0, 0.1));
        spLambda = new JSpinner(new SpinnerNumberModel(0.05, 0.01, 0.2, 0.01));

        panelParametros.add(new JLabel("Media"));
        panelParametros.add(spMedia);
        panelParametros.add(new JLabel("Sigma"));
        panelParametros.add(spSigma);

        panelParametros.add(new JLabel("Prob."));
        panelParametros.add(spProb);
        panelParametros.add(new JLabel("Min"));
        panelParametros.add(spMin);

        panelParametros.add(new JLabel("Max"));
        panelParametros.add(spMax);
        panelParametros.add(new JLabel("Alpha"));
        panelParametros.add(spAlpha);

        panelParametros.add(new JLabel("Beta"));
        panelParametros.add(spBeta);
        panelParametros.add(new JLabel("Lambda"));
        panelParametros.add(spLambda);

        panelParametros.setVisible(true); 
        return panelParametros;
    }

    private JLabel crearLabel(String titulo) {
        JLabel lbl = new JLabel("", JLabel.CENTER);
        lbl.setBorder(BorderFactory.createTitledBorder(titulo));
        return lbl;
    }

    private void cargarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Imágenes", "jpg", "jpeg", "png", "bmp", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                imgOriginal = ImageUtils.convertirAGrises(img);
                ParaLabel.mostrarEnLabel(lblOriginal, imgOriginal);
                lblRuido.setIcon(null);
                lblFiltrada.setIcon(null);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar imagen");
            }
        }
    }

    private void aplicarRuido() {

        if (imgOriginal == null) return;

        String filtro = comboFiltros.getSelectedItem().toString();

        ((TitledBorder) panelParametros.getBorder())
                .setTitle("Parámetros del ruido – " + filtro);
        panelParametros.repaint();

        switch (filtro) {

            case "Mediana (Sal y pimienta)":
                imgRuido = GeneradorRuido.ruidoSalPimienta(
                        imgOriginal, ((Number) spProb.getValue()).doubleValue());
                break;

            case "Media aritmética (Rayleigh)":
                imgRuido = GeneradorRuido.ruidoRayleigh(
                        imgOriginal, 
                        ((Number) spSigma.getValue()).doubleValue()
                );        
                break;
                
            case "Máximo (Uniforme)":
                imgRuido = GeneradorRuido.ruidoUniforme(
                        imgOriginal,
                        (int) spMin.getValue(),
                        (int) spMax.getValue());
                break;

            case "Mínimo (Gamma)":
            case "Armónico (Gamma)":
                imgRuido = GeneradorRuido.ruidoGamma(
                        imgOriginal,
                        ((Number) spAlpha.getValue()).doubleValue(),
                        ((Number) spBeta.getValue()).doubleValue());

                break;

            case "Punto medio (Gauss)":
            case "Alfa trimmed (Gauss)":
            case "Contra-armónico (Gauss)":
                imgRuido = GeneradorRuido.ruidoGaussiano(
                        imgOriginal,
                        ((Number) spMedia.getValue()).doubleValue(),
                        ((Number) spSigma.getValue()).doubleValue()
                );
                break;

            case "Geométrico (Exponencial negativo)":
                imgRuido = GeneradorRuido.ruidoExponencial(
                        imgOriginal, ((Number) spLambda.getValue()).doubleValue());
                break;

            case "Máximo–mínimo (Sin ruido)":
                imgRuido = imgOriginal;
                break;
        }

        ParaLabel.mostrarEnLabel(lblRuido, imgRuido);
        lblFiltrada.setIcon(null);
    }

    private void actualizarParametrosVisibles(String filtro) {
        /*
        permite solo interactuar con los parámetros del ruido asignado a cada filtro
        los parámetros que no pertenecen al ruido seleccionado se inhabilitan
        */
        
        spMedia.setEnabled(false);
        spSigma.setEnabled(false);
        spProb.setEnabled(false);
        spMin.setEnabled(false);
        spMax.setEnabled(false);
        spAlpha.setEnabled(false);
        spBeta.setEnabled(false);
        spLambda.setEnabled(false);

        if (filtro.contains("Gauss")) {
            spMedia.setEnabled(true);
            spSigma.setEnabled(true);
        }

        if (filtro.contains("Sal")) {
            spProb.setEnabled(true);
        }

        if (filtro.contains("Uniforme")) {
            spMin.setEnabled(true);
            spMax.setEnabled(true);
        }

        if (filtro.contains("Gamma")) {
            spAlpha.setEnabled(true);
            spBeta.setEnabled(true);
        }

        if (filtro.contains("Exponencial")) {
            spLambda.setEnabled(true);
        }
    }


    private void aplicarFiltro() {

        if (imgRuido == null) return;

        String filtro = comboFiltros.getSelectedItem().toString();

        switch (filtro) {

            case "Mediana (Sal y pimienta)":
                imgFiltrada = FiltrosNoLineales.mediana(imgRuido, 3);
                break;

            case "Media aritmética (Rayleigh)":
                imgFiltrada = FiltrosNoLineales.mediaAritmetica(imgRuido, 3);
                break;

            case "Máximo (Uniforme)":
                imgFiltrada = FiltrosNoLineales.maximo(imgRuido, 3);
                break;

            case "Mínimo (Gamma)":
                imgFiltrada = FiltrosNoLineales.minimo(imgRuido, 3);
                break;

            case "Punto medio (Gauss)":
                imgFiltrada = FiltrosNoLineales.puntoMedio(imgRuido, 3);
                break;

            case "Alfa trimmed (Gauss)":
                imgFiltrada = FiltrosNoLineales.alfaTrimmed(imgRuido, 3, 2);
                break;

            case "Armónico (Gamma)":
                imgFiltrada = FiltrosNoLineales.armonico(imgRuido, 3);
                break;

            case "Contra-armónico (Gauss)":
                imgFiltrada = FiltrosNoLineales.contraArmonico(imgRuido, 3, 0.0001);
                break;

            case "Geométrico (Exponencial negativo)":
                imgFiltrada = FiltrosNoLineales.geometrico(imgRuido, 3);
                break;

            case "Máximo–mínimo (Sin ruido)":
                imgFiltrada = FiltrosNoLineales.maximoMinimo(imgOriginal, 3);
                break;
        }

        ParaLabel.mostrarEnLabel(lblFiltrada, imgFiltrada);
    }
    
    private void mostrarMensajeComparacion() {
        String mensaje =
                "INTERPRETACIÓN DE LA COMPARACIÓN\n\n" +
                "• Los píxeles marcados en ROJO representan posiciones donde\n" +
                "  el valor de intensidad es distinto entre las dos imágenes.\n\n" +
                "• Los píxeles que no aparecen en rojo indican que la\n" +
                "  intensidad se conservó.\n\n" +
                "• Una mayor cantidad de píxeles rojos implica mayor\n" +
                "  diferencia entre las imágenes comparadas.\n\n";

        JOptionPane.showMessageDialog(this, mensaje,
                "¿Cómo interpretar la comparación?",
                JOptionPane.INFORMATION_MESSAGE);
    }

    //comparación visual entre las imágenes
    private void mostrarComparacion1() { //imagen con ruido vs imagen filtrada

        if (imgRuido == null || imgFiltrada == null) return;

        mostrarMensajeComparacion();
        BufferedImage comparada = Comparador.comparar(imgRuido, imgFiltrada);

        VentanaComparacion ventana =
                new VentanaComparacion(this, comparada);

        ventana.setVisible(true);
    }
    
    private void mostrarComparacion2() { //imagen original vs imagen con ruido

        if (imgRuido == null || imgOriginal == null) return;

        mostrarMensajeComparacion();
        BufferedImage comparada2 = Comparador.comparar(imgRuido, imgOriginal);

        VentanaComparacion ventana =
                new VentanaComparacion(this, comparada2);

        ventana.setVisible(true);
    }
    
    private void mostrarComparacion3() { //imagen original vs imagen filtrada
        if (imgFiltrada == null || imgOriginal == null) return;

        mostrarMensajeComparacion();
        BufferedImage comparada3 = Comparador.comparar(imgFiltrada, imgOriginal);

        VentanaComparacion ventana =
                new VentanaComparacion(this, comparada3);

        ventana.setVisible(true);
    }
    
    private void mostrarAnalisisPixeles() {
        if (imgOriginal == null || imgRuido == null || imgFiltrada == null) {
            JOptionPane.showMessageDialog(this,"Carga la imagen, aplica ruido y filtra primero");
            return;
        }

        int pixRuido = AnalizarPixeles.contarPixelesModificados(imgOriginal, imgRuido);

        double porcRuido = AnalizarPixeles.porcentajePixelesModificados(imgOriginal, imgRuido);

        int pixFiltrada = AnalizarPixeles.contarPixelesModificados(imgOriginal, imgFiltrada);

        double porcFiltrada = AnalizarPixeles.porcentajePixelesModificados(imgOriginal, imgFiltrada);

        double reduccion = porcRuido - porcFiltrada;

        String resultado =
                "ANÁLISIS DE PÍXELES\n\n" +
                "Imagen con ruido vs original\n" +
                "• Píxeles modificados: " + pixRuido + "\n" +
                "• Porcentaje: " + String.format("%.2f", porcRuido) + " %\n\n" +
                "Imagen filtrada vs original\n" +
                "• Píxeles modificados: " + pixFiltrada + "\n" +
                "• Porcentaje: " + String.format("%.2f", porcFiltrada) + " %\n\n" +
                "Reducción del porcentaje de píxeles modificados:\n" +
                "• " + String.format("%.2f", reduccion) + " %";

        JOptionPane.showMessageDialog(this, resultado,
                "Resultados del análisis",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Pantalla().setVisible(true));
    }
}