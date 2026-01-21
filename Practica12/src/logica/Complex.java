package logica;

/*
 Clase que permite manejar números complejos
*/

public class Complex {
    public double re, im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double magnitude() {
        return Math.sqrt(re*re + im*im);
    }
}