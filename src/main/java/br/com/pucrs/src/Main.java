package br.com.pucrs.src;

public class Main {

    public static void main(String[] args){
        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        Aleatorio aleatorios = new Aleatorio(simulador.qtdNumerosAleatorios);
        aleatorios.geraPseudoAleatorio();

        simulador.simulacao(aleatorios);
    }
}
