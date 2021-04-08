package br.com.pucrs.src;

public class Evento {
    public TipoEnum tipo;
    public double tempo; //Tempo do evento

    public Evento(TipoEnum tipo, double tempo){
        this.tipo = tipo;
        this.tempo = tempo;
    }

    public enum TipoEnum {
        ENTRADA, SAIDA;
    }

}
