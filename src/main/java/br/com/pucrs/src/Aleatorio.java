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
    public static int quantidadeAleatoriosGerados = 0;

    public static int index = 0;

    public static ArrayList<Double> arrayTest = new ArrayList<Double>(Arrays.asList(0.2176, 0.0103, 0.1109, 0.3456, 0.9910, 0.2323, 0.9211, 0.0322,
            0.1211, 0.5131, 0.7208, 0.9172, 0.9922, 0.8324, 0.5011, 0.2931));

    public static double geraProximoAleatorio() {
//        quantidadeAleatoriosGerados++;
//        ultimoAleatorio = ((a * ultimoAleatorio + c) % mod) / mod;
//        return ultimoAleatorio;

        return arrayTest.get(quantidadeAleatoriosGerados++); //*/

    }
}