package br.com.pucrs.src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // System.out.println(aleatorios.numerosAleatorios);

        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        //Aleatorio aleatorios = new Aleatorio(simulador.qtdNumerosAleatorios);
        Aleatorio aleatorios = new Aleatorio(7);

        aleatorios.geraPseudoAleatorioTeste();

        simulador.executar();

    }
}
