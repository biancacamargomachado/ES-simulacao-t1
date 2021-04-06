package br.com.pucrs.src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // System.out.println(aleatorios.numerosAleatorios);

        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        double[] aleatoriosdoexemplo = {0.3276, 0.8851, 0.1643, 0.5542, 0.6813, 0.7221, 0.9881};
        Aleatorio aleatorios = new Aleatorio(7, aleatoriosdoexemplo);
        // aleatorios.geraPseudoAleatorio();
        simulador.simulacao(aleatorios);

    }
}
