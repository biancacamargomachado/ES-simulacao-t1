package br.com.pucrs.src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Aleatorio aleatorios = new Aleatorio();
        aleatorios.geraPseudoAleatorio();

        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

    }
}
