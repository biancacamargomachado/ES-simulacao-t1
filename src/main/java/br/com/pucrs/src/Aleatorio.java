package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Aleatorio {
    /**
     * c = constante usada para maior variação dos números gerados
     * a = número
     * mod = número grande
     */
    public static long a = 25214903917L;
    public static long c = 44848151548451L;
    public static double mod = Math.pow(2.0, 24) - 8;
    public static int semente = 7;
    public static double ultimoAleatorio = semente;
    public static double quantidadeAleatoriosGerados = 0;

    public static int index = 0;

    public static ArrayList<Double> arrayTest = new ArrayList<Double>(Arrays.asList(0.9921, 0.0004, 0.5534, 0.2761, 0.3398, 0.8963, 0.9023, 0.0132,
            0.4569, 0.5121, 0.9208, 0.0171, 0.2299, 0.8545, 0.06001, 0.6001, 0.2921));

    public static double geraProximoAleatorio() {
        quantidadeAleatoriosGerados++;
        ultimoAleatorio = ((a * ultimoAleatorio + c) % mod) / mod;
        return ultimoAleatorio;

        //return arrayTest.get(index++);*/

    }
}