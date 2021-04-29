package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.Arrays;

public class Aleatorio {
    /**
     * c = constante usada para maior variação dos números gerados
     * a = número
     * mod = número grande
     */
    public static int a;
    public static int c;
    public static double mod;
    public int semente;
    public int size;
    public static double ultimoAleatorio;
    public static double qtAleatorios;

    public static int index = 0;

    public static ArrayList<Double> arrayTest = new ArrayList<Double>(Arrays.asList(0.9921, 0.0004, 0.5534, 0.2761, 0.3398, 0.8963, 0.9023, 0.0132,
            0.4569, 0.5121, 0.9208, 0.0171, 0.2299, 0.8545, 0.06001, 0.6001, 0.2921));

    public Aleatorio() {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2, 39) - 5;
        this.semente = 7;
        // this.size = size;
        this.ultimoAleatorio = semente;
        this.qtAleatorios = 0;
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

    /*
    public void geraPseudoAleatorio(){
        numerosAleatorios = new double[size];
        numerosAleatorios[0] = semente;
        for(int i = 1; i<size; i++){
            numerosAleatorios[i] = ((a*numerosAleatorios[i-1] + c) % mod);
        }
    }
     */

    public static double geraProximoAleatorio() {
        ultimoAleatorio = ((a * ultimoAleatorio + c) % mod) / mod;
        qtAleatorios++;
        return ultimoAleatorio;
//
//        qtAleatorios++;
//        return arrayTest.get(index++);
    }
}