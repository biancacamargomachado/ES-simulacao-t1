package br.com.pucrs.src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        /**
         * c = é uma constante usada para maior variação
         * dos números gerados
         * a = um número
         * mod = um número grande
         */
        double[] aleatorios = new double[1001];
        // aleatorios = new Aleatorio();


        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

    }
}
