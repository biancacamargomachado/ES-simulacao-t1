package br.com.pucrs.src;

public class
Evento {
    public TipoEnum tipo;
    public double tempo; //Tempo do evento
    public Fila fila;
    public Fila destino;

    public Evento(TipoEnum tipo, double tempo, Fila fila, Fila destino){
        this.tipo = tipo;
        this.tempo = tempo;
        this.fila = fila;
        this.destino = destino;
    }

    public enum TipoEnum {
        CHEGADA, SAIDA;
    }

}
