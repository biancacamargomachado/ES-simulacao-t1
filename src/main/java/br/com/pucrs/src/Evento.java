package br.com.pucrs.src;

public class
Evento {
    public TipoEnum tipo;
    public double tempo; // Tempo do evento
    public Integer idFila;
    public Integer idDestino;

    public Evento(TipoEnum tipo, double tempo, Integer idFila) {
        this.tipo = tipo;
        this.tempo = tempo;
        this.idFila = idFila;
        this.idDestino = idDestino;
    }
    public enum TipoEnum {
        CHEGADA, SAIDA_1, /** SAIDA_2, */ PASSAGEM;
    }

    public void setIdDestino(Integer idDestino) {
        this.idDestino = idDestino;
    }

}
