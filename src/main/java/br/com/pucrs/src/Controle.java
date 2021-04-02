package br.com.pucrs.src;

/**
 * Classe responsável pelo controle das entradas e saídas
 * para saber quem deve ser atendimento primeiro, ou seja, que tem o tempo menor.
 */
public class Controle {

    public double time;
    public ControleEnum controleEnum;

    public double getTime() {
        return time;
    }

    enum ControleEnum {
        ENTRADA, SAIDA;
    }
}
