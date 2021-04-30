package br.com.pucrs.src;

public class
Evento {
    public TipoEnum tipo;
    public double tempo; //Tempo do evento
    public Integer idFila;

    public Evento(TipoEnum tipo, double tempo, Integer idFila) {
        this.tipo = tipo;
        this.tempo = tempo;
        this.idFila = idFila;
    }
    public enum TipoEnum {
        CHEGADA, SAIDA, PASSAGEM;
    }
}
