package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.List;

public class EscalonadorDeFilas {
    public List<Fila> filas;

    public EscalonadorDeFilas() {
        filas = new ArrayList<>();
        filas.add(new Fila());
    }
}
