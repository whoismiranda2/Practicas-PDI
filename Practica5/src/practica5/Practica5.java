package practica5;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Practica5 extends JFrame {

    private BufferedImage img1, img2, resultImg;
    private JLabel labelImg1, labelImg2, labelResult;

    public Practica5() {
        super("Operaciones con Imágenes");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel imagesPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        labelImg1 = new JLabel("Imagen 1", SwingConstants.CENTER);
        labelImg2 = new JLabel("Imagen 2", SwingConstants.CENTER);
        labelResult = new JLabel("Resultado", SwingConstants.CENTER);
        labelImg1.setVerticalTextPosition(JLabel.BOTTOM);
        labelImg1.setHorizontalTextPosition(JLabel.CENTER);
        labelImg2.setVerticalTextPosition(JLabel.BOTTOM);
        labelImg2.setHorizontalTextPosition(JLabel.CENTER);
        labelResult.setVerticalTextPosition(JLabel.BOTTOM);
        labelResult.setHorizontalTextPosition(JLabel.CENTER);
        imagesPanel.add(new JScrollPane(labelImg1));
        imagesPanel.add(new JScrollPane(labelImg2));
        imagesPanel.add(new JScrollPane(labelResult));
        add(imagesPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        //cargar imágenes
        JPanel loadPanel = new JPanel();
        JButton load1 = new JButton("Cargar Imagen 1");
        load1.addActionListener(e -> {
            img1 = loadImage();
            if (img1 != null) {
                labelImg1.setIcon(new ImageIcon(scaleImage(img1, labelImg1.getWidth(), labelImg1.getHeight())));
                labelImg1.setText("Imagen 1");
            }
        });
        JButton load2 = new JButton("Cargar Imagen 2");
        load2.addActionListener(e -> {
            img2 = loadImage();
            if (img2 != null) {
                labelImg2.setIcon(new ImageIcon(scaleImage(img2, labelImg2.getWidth(), labelImg2.getHeight())));
                labelImg2.setText("Imagen 2");
            }
        });
        loadPanel.add(load1);
        loadPanel.add(load2);
        controlPanel.add(loadPanel);

        //transformaciones
        JPanel transPanel = new JPanel();
        transPanel.setBorder(BorderFactory.createTitledBorder("Transformaciones (Imagen 1)"));
        JButton btnTranslate = new JButton("Traslación");
        btnTranslate.addActionListener(e -> translateImage());
        JButton btnRotate = new JButton("Rotación");
        btnRotate.addActionListener(e -> rotateImage());
        JButton btnScale = new JButton("Escalar");
        btnScale.addActionListener(e -> scaleImage());
        transPanel.add(btnTranslate);
        transPanel.add(btnRotate);
        transPanel.add(btnScale);
        controlPanel.add(transPanel);

        //aritméticas
        JPanel arithPanel = new JPanel();
        arithPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Aritméticas"));
        JButton btnSum = new JButton("Suma");
        btnSum.addActionListener(e -> arithmeticOperation("suma"));
        JButton btnSub = new JButton("Resta");
        btnSub.addActionListener(e -> arithmeticOperation("resta"));
        JButton btnMul = new JButton("Multiplicación");
        btnMul.addActionListener(e -> arithmeticOperation("multiplicacion"));
        JButton btnDiv = new JButton("División");
        btnDiv.addActionListener(e -> arithmeticOperation("division"));
        arithPanel.add(btnSum);
        arithPanel.add(btnSub);
        arithPanel.add(btnMul);
        arithPanel.add(btnDiv);
        controlPanel.add(arithPanel);

        //lógicas
        JPanel logicPanel = new JPanel();
        logicPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Lógicas"));
        JButton btnAnd = new JButton("AND");
        btnAnd.addActionListener(e -> logicalOperation("and"));
        JButton btnOr = new JButton("OR");
        btnOr.addActionListener(e -> logicalOperation("or"));
        JButton btnXor = new JButton("XOR");
        btnXor.addActionListener(e -> logicalOperation("xor"));
        JButton btnNot = new JButton("NOT (Imagen 1)");
        btnNot.addActionListener(e -> logicalOperation("not"));
        logicPanel.add(btnAnd);
        logicPanel.add(btnOr);
        logicPanel.add(btnXor);
        logicPanel.add(btnNot);
        controlPanel.add(logicPanel);

        //relacionales
        JPanel relPanel = new JPanel();
        relPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Relacionales"));
        String[] relOps = {"<", "<=", ">", ">=", "==", "!="};
        JComboBox<String> relCombo = new JComboBox<>(relOps);
        JButton btnRel = new JButton("Aplicar");
        btnRel.addActionListener(e -> relationalOperation((String) relCombo.getSelectedItem()));
        relPanel.add(new JLabel("Operación:"));
        relPanel.add(relCombo);
        relPanel.add(btnRel);
        controlPanel.add(relPanel);

        add(controlPanel, BorderLayout.SOUTH);

        setSize(1100, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //aux
    private BufferedImage loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes JPG, PNG", "jpg", "jpeg", "png"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                return ImageIO.read(chooser.getSelectedFile());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar imagen: " + ex.getMessage());
            }
        }
        return null;
    }

    private Image scaleImage(BufferedImage img, int maxW, int maxH) {
        if (img == null) return null;
        if (maxW <= 0 || maxH <= 0) {
            maxW = 300;
            maxH = 300;
        }
        int w = img.getWidth();
        int h = img.getHeight();
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale > 1) scale = 1;
        return img.getScaledInstance((int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
    }

    //conversión a grises
    private BufferedImage toGrayscale(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int grayVal = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                int newRGB = (grayVal << 16) | (grayVal << 8) | grayVal;
                gray.setRGB(x, y, newRGB);
            }
        }
        return gray;
    }

    //OPERACIONES

    //aritméticas
    private void arithmeticOperation(String operation) {
        if (img1 == null || img2 == null) {
            JOptionPane.showMessageDialog(this, "Carga ambas imágenes primero.");
            return;
        }
        BufferedImage im1 = toGrayscale(img1);
        BufferedImage im2 = toGrayscale(img2);
        int w = Math.min(im1.getWidth(), im2.getWidth());
        int h = Math.min(im1.getHeight(), im2.getHeight());
        resultImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int g1 = im1.getRGB(x, y) & 0xff;
                int g2 = im2.getRGB(x, y) & 0xff;
                int val = 0;
                switch (operation) {
                    case "suma": val = Math.min(255, g1 + g2); break;
                    case "resta": val = Math.max(0, g1 - g2); break;
                    case "multiplicacion": val = Math.min(255, g1 * g2 / 255); break;
                    case "division": val = (g2 == 0) ? 255 : Math.min(255, (g1 * 255) / g2); break;
                }
                int rgb = (val << 16) | (val << 8) | val;
                resultImg.setRGB(x, y, rgb);
            }
        }
        labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
        labelResult.setText("Resultado " + operation);
    }

    //lógicas
    private void logicalOperation(String operation) {
        if (operation.equals("not")) {
            if (img1 == null) {
                JOptionPane.showMessageDialog(this, "Carga la Imagen 1 primero.");
                return;
            }
            BufferedImage im1 = toGrayscale(img1);
            int w = im1.getWidth();
            int h = im1.getHeight();
            resultImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int g1 = im1.getRGB(x, y) & 0xff;
                    int val = ~g1 & 0xff;
                    int rgb = (val << 16) | (val << 8) | val;
                    resultImg.setRGB(x, y, rgb);
                }
            }
            labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
            labelResult.setText("NOT Imagen 1");
            return;
        }

        if (img1 == null || img2 == null) {
            JOptionPane.showMessageDialog(this, "Carga ambas imágenes primero.");
            return;
        }
        BufferedImage im1 = toGrayscale(img1);
        BufferedImage im2 = toGrayscale(img2);
        int w = Math.min(im1.getWidth(), im2.getWidth());
        int h = Math.min(im1.getHeight(), im2.getHeight());
        resultImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int g1 = im1.getRGB(x, y) & 0xff;
                int g2 = im2.getRGB(x, y) & 0xff;
                int val = 0;
                switch (operation) {
                    case "and": val = g1 & g2; break;
                    case "or": val = g1 | g2; break;
                    case "xor": val = g1 ^ g2; break;
                }
                int rgb = (val << 16) | (val << 8) | val;
                resultImg.setRGB(x, y, rgb);
            }
        }
        labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
        labelResult.setText("Resultado " + operation.toUpperCase());
    }

    //relacionales
    private void relationalOperation(String operation) {
        if (img1 == null || img2 == null) {
            JOptionPane.showMessageDialog(this, "Carga ambas imágenes primero.");
            return;
        }
        BufferedImage im1 = toGrayscale(img1);
        BufferedImage im2 = toGrayscale(img2);
        int w = Math.min(im1.getWidth(), im2.getWidth());
        int h = Math.min(im1.getHeight(), im2.getHeight());
        resultImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int g1 = im1.getRGB(x, y) & 0xff;
                int g2 = im2.getRGB(x, y) & 0xff;
                boolean cond = false;
                switch (operation) {
                    case "<": cond = g1 < g2; break;
                    case "<=": cond = g1 <= g2; break;
                    case ">": cond = g1 > g2; break;
                    case ">=": cond = g1 >= g2; break;
                    case "==": cond = g1 == g2; break;
                    case "!=": cond = g1 != g2; break;
                }
                int val = cond ? 255 : 0;
                int rgb = (val << 16) | (val << 8) | val;
                resultImg.setRGB(x, y, rgb);
            }
        }
        labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
        labelResult.setText("Resultado Relacional " + operation);
    }

    //TRANSFORMACIONES
    private void translateImage() {
        if (img1 == null) {
            JOptionPane.showMessageDialog(this, "Carga la Imagen 1 primero.");
            return;
        }
        String dxStr = JOptionPane.showInputDialog(this, "Desplazamiento en X (px):", "0");
        String dyStr = JOptionPane.showInputDialog(this, "Desplazamiento en Y (px):", "0");
        try {
            int dx = Integer.parseInt(dxStr);
            int dy = Integer.parseInt(dyStr);
            resultImg = translate(img1, dx, dy);
            labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
            labelResult.setText("Imagen Trasladada");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valores inválidos.");
        }
    }

    private void rotateImage() {
        if (img1 == null) {
            JOptionPane.showMessageDialog(this, "Carga la Imagen 1 primero.");
            return;
        }
        String angleStr = JOptionPane.showInputDialog(this, "Ángulo de rotación (grados):", "0");
        try {
            double angle = Math.toRadians(Double.parseDouble(angleStr));
            resultImg = rotate(img1, angle);
            labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
            labelResult.setText("Imagen Rotada");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
        }
    }

    private void scaleImage() {
        if (img1 == null) {
            JOptionPane.showMessageDialog(this, "Carga la Imagen 1 primero.");
            return;
        }
        String scaleStr = JOptionPane.showInputDialog(this, "Factor de escala (ej. 0.5 para reducir, 2 para agrandar):", "1");
        try {
            double scale = Double.parseDouble(scaleStr);
            if (scale <= 0) throw new Exception("El factor de escala debe ser positivo");
            int maxSize = 5000;
            resultImg = scale(img1, scale, maxSize);
            labelResult.setIcon(new ImageIcon(scaleImage(resultImg, labelResult.getWidth(), labelResult.getHeight())));
            labelResult.setText("Imagen Escalada (factor: " + scale + ")");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
        }
    }

    private BufferedImage translate(BufferedImage img, int dx, int dy) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = res.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        g.drawImage(img, dx, dy, null);
        g.dispose();
        return res;
    }

    private BufferedImage rotate(BufferedImage img, double angleRad) {
        int w = img.getWidth();
        int h = img.getHeight();

        double sin = Math.abs(Math.sin(angleRad));
        double cos = Math.abs(Math.cos(angleRad));
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(h * cos + w * sin);

        BufferedImage res = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        int cx = w / 2;
        int cy = h / 2;
        int ncx = newW / 2;
        int ncy = newH / 2;

        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                int dx = x - ncx;
                int dy = y - ncy;
                double ox = Math.cos(angleRad) * dx + Math.sin(angleRad) * dy + cx;
                double oy = -Math.sin(angleRad) * dx + Math.cos(angleRad) * dy + cy;
                int rgb = bilinearInterpolation(img, ox, oy);
                res.setRGB(x, y, rgb);
            }
        }
        return res;
    }

    private BufferedImage scale(BufferedImage img, double scale, int maxSize) {
        int w = img.getWidth();
        int h = img.getHeight();
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        if (newW <= 0) newW = 1;
        if (newH <= 0) newH = 1;

        if (newW > maxSize || newH > maxSize) {
            double adjustScale = Math.min((double) maxSize / newW, (double) maxSize / newH);
            newW = (int) (w * adjustScale);
            newH = (int) (h * adjustScale);
        }

        BufferedImage res = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                double ox = x / scale;
                double oy = y / scale;
                int rgb = bilinearInterpolation(img, ox, oy);
                res.setRGB(x, y, rgb);
            }
        }
        return res;
    }

    private int bilinearInterpolation(BufferedImage img, double x, double y) {
        int w = img.getWidth();
        int h = img.getHeight();
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = Math.min(x1 + 1, w - 1);
        int y2 = Math.min(y1 + 1, h - 1);

        if (x1 < 0 || y1 < 0 || x1 >= w || y1 >= h) return Color.BLACK.getRGB();

        double dx = x - x1;
        double dy = y - y1;

        int rgb11 = img.getRGB(x1, y1);
        int rgb21 = img.getRGB(x2, y1);
        int rgb12 = img.getRGB(x1, y2);
        int rgb22 = img.getRGB(x2, y2);

        int r = (int) (
                (1 - dx) * (1 - dy) * ((rgb11 >> 16) & 0xFF) +
                        dx * (1 - dy) * ((rgb21 >> 16) & 0xFF) +
                        (1 - dx) * dy * ((rgb12 >> 16) & 0xFF) +
                        dx * dy * ((rgb22 >> 16) & 0xFF)
        );

        int g = (int) (
                (1 - dx) * (1 - dy) * ((rgb11 >> 8) & 0xFF) +
                        dx * (1 - dy) * ((rgb21 >> 8) & 0xFF) +
                        (1 - dx) * dy * ((rgb12 >> 8) & 0xFF) +
                        dx * dy * ((rgb22 >> 8) & 0xFF)
        );

        int b = (int) (
                (1 - dx) * (1 - dy) * (rgb11 & 0xFF) +
                        dx * (1 - dy) * (rgb21 & 0xFF) +
                        (1 - dx) * dy * (rgb12 & 0xFF) +
                        dx * dy * (rgb22 & 0xFF)
        );

        return (r << 16) | (g << 8) | b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Practica5());
    }
}