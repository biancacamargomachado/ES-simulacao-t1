package br.com.pucrs.src;


public class Aleatorio {
    /**
     * c = é uma constante usada para maior variação
     * dos números gerados
     * a = um número
     * mod = um número grande
     */

    public static int index = 0;
    public static int a;
    public int c;
    public double mod;
    public int semente;
    public int size;
    public double[] numerosAleatorios;

    public Aleatorio(int size) {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2, 39) - 5;
        this.semente = 7;
        this.size = size;
    }

    public double[] geraPseudoAleatorio() {

        numerosAleatorios = new double[size];
        numerosAleatorios[0] = semente;
        System.out.println(numerosAleatorios[0] / mod);

        for (int i = 1; i < size; i++) {
            numerosAleatorios[i] = ((a * numerosAleatorios[i - 1] + c) % mod);
            System.out.println(numerosAleatorios[i] / mod);
        }

        return numerosAleatorios;
    }

    public double[] geraPseudoAleatorioTeste() {

        numerosAleatorios = new double[7];
        numerosAleatorios[0] = semente;

        double[] arrayTeste = new double[]{0.3276, 0.8851, 0.1643, 0.5542, 0.6813, 0.7221, 0.9881};
        for (int i = 0; i < 7; i++) {
            numerosAleatorios[i] = arrayTeste[i];
        }
        return numerosAleatorios;
    }
}
