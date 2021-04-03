package br.com.pucrs.src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // System.out.println(aleatorios.numerosAleatorios);

        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        Aleatorio aleatorios = new Aleatorio(simulador.qtdNumerosAleatorios);
        aleatorios.geraPseudoAleatorio();

        simulador.simulacao(aleatorios);

    }
}
