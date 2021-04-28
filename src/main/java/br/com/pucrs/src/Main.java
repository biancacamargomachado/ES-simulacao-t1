/**
 * Parte 1 do trabalho de Simulação e Métodos Analíticos.
 *
 * @author Bianca Camargo, Chiara Paskulin e Marcelo H. de Souza;
 *
 */
package br.com.pucrs.src;

public class Main {

    public static void main(String[] args){
        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        //Aleatorio aleatorios = new Aleatorio(simulador.qtdNumerosAleatorios);
        Aleatorio aleatorios = new Aleatorio();

        simulador.simulacao(aleatorios);

        //Aleatorio.arrayTest.forEach(c -> System.out.println());
    }
}
