package br.com.pucrs.src;

import java.util.Comparator;

public class Evento {
    public TipoEnum tipo;
    public double tempo; // tempo do evento

    public Evento(TipoEnum tipo, double tempo){
        this.tipo = tipo;
        this.tempo = tempo;
    }

    public enum TipoEnum {
        ENTRADA, SAIDA;
    }

    public double getTime() {
        return this.tempo;
    };

}
