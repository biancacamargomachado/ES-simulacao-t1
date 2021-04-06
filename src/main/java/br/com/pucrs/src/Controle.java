package br.com.pucrs.src;

/**
 * Classe responsável pelo controle das entradas e saídas
 * para saber quem deve ser atendimento primeiro, ou seja, que tem o tempo menor.
 */
public class Controle {

    public double tempo;
    public ControleEnum controleEnum;

    public Controle(ControleEnum controleEnum, double tempo) {
        this.controleEnum = controleEnum;
        this.tempo = tempo;
    }

    public double getTempo() {
        return tempo;
    }

    enum ControleEnum {
        CHEGADA, SAIDA;
    }
}
