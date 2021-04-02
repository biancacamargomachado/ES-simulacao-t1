package com.pucrs;

public class Fila {
    public int populacaoAtual = 0;
    public int populacaoMax = 3;
    public int qtdCaixas = 1;
    public int qtdClientePerdido;
    public int chegadaInicial = 2;

    public void entrada() {
        if (populacaoAtual < populacaoMax) {
            ++populacaoAtual;
            if (this.populacaoAtual <= this.qtdCaixas) {
            }
        } else {
            qtdClientePerdido++;
        }
    }

    public void saida() {
        --populacaoAtual;
        if (this.populacaoAtual >= this.qtdCaixas) {
        }
    }
}
