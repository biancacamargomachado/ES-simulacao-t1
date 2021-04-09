package br.com.pucrs.src;

public class Aleatorio {
    /**
     * c = constante usada para maior variação dos números gerados
     * a = número
     * mod = número grande
     */
    public static int a;
    public int c;
    public double mod;
    public int semente;
    public int size;
    public double[] numerosAleatorios;

    public Aleatorio(int size) {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2,39)-5;
        this.semente = 7;
        this.size = size;
    }

    /* Método para testar com outros aleatórios
    public Aleatorio(int size, double[] numerosAleatorios) {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2,39)-5;
        this.semente = 7;
        this.size = size;
        this.numerosAleatorios = numerosAleatorios;
    } */

    public void geraPseudoAleatorio(){
        numerosAleatorios = new double[size];
        numerosAleatorios[0] = semente;
        for(int i = 1; i<size; i++){
            numerosAleatorios[i] = ((a*numerosAleatorios[i-1] + c) % mod);
        }
    }
}
