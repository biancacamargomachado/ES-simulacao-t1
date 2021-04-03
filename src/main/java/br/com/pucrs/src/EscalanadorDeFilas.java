package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.List;

public class EscalanadorDeFilas {

    /**
     * Sempre que add um elemento nessa lista executar o comando abaixo
     * controles.sort(Comparator.comparing(Controle::getTime));
     */
    public List<Controle> controles;
    
    public List<Fila> filas;

    public EscalanadorDeFilas(){
        filas = new ArrayList<Fila>();
        filas.add(new Fila());
    }

}
