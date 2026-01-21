package logica;

import java.awt.*;
import javax.swing.*;

public class Histograma extends JPanel {
    private double[] data;
    private String titulo;
    private Color color;

    public Histograma(int[] data, String titulo, Color color) {
        this.data = new double[data.length];
        for (int i = 0; i < data.length; i++) this.data[i] = data[i];
        this.titulo = titulo;
        this.color = color;
        setPreferredSize(new Dimension(500, 250));
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        int width=getWidth(), height=getHeight(), margin=50;
        double max=0; for(double v:data) if(v>max) max=v;
        int graphWidth=width-2*margin, graphHeight=height-2*margin;
        g2.setColor(Color.BLACK);
        g2.drawLine(margin,height-margin,width-margin,height-margin);
        g2.drawLine(margin,margin,margin,height-margin);

        // Eje X
        for(int i=0;i<=255;i+=51){
            int x=margin+(i*graphWidth/256);
            g2.drawLine(x,height-margin,x,height-margin+5);
            g2.drawString(Integer.toString(i),x-10,height-margin+20);
        }

        // Curva
        g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),100));
        Polygon poly=new Polygon();
        poly.addPoint(margin,height-margin);
        for(int i=0;i<256;i++){
            int x=margin+(i*graphWidth/256);
            int y=height-margin-(int)((data[i]/max)*graphHeight);
            poly.addPoint(x,y);
        }
        poly.addPoint(margin+graphWidth,height-margin);
        g2.fillPolygon(poly);

        g2.setColor(color);
        for(int i=0;i<255;i++){
            int x1=margin+(i*graphWidth/256);
            int y1=height-margin-(int)((data[i]/max)*graphHeight);
            int x2=margin+((i+1)*graphWidth/256);
            int y2=height-margin-(int)((data[i+1]/max)*graphHeight);
            g2.drawLine(x1,y1,x2,y2);
        }
    }
}