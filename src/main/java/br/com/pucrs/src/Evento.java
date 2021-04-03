package br.com.pucrs.src;

public class Evento {
    public TipoEnum tipo; // 0 = chegada e 1 saída;
    public double tempo; // tempo do evento

    public Evento(TipoEnum tipo, double tempo){
        this.tipo = tipo;
        this.tempo = tempo;
    }

    public enum TipoEnum {
        ENTRADA, SAIDA;
    }

}
