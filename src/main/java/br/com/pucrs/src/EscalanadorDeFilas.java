package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe responsavel por gerenciar as chegadas e saidas das filas (agendamentos)
 */
public class EscalanadorDeFilas {

    /**
     * Sempre que add um elemento nessa lista executar o comando abaixo
     * controles.sort(Comparator.comparing(Controle::getTime));
     */

    public List<Controle> agendamentos = new ArrayList<Controle>();
    public List<Fila> filas = new ArrayList<>();

    public Aleatorio aleatorio = new Aleatorio(7);
    public double tempo = 0;

    public EscalanadorDeFilas() {
    }

    public void agendamentoChegada(final Fila fila) {
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (fila.saidaMaxima - fila.saidaMinima) * aleatorio.numerosAleatorios[Aleatorio.index++] + fila.saidaMinima;

        // t + tempo atual
        double tempoRealChegada = tempoSaida + tempo;

        final Controle novaChegada = new Controle(Controle.ControleEnum.CHEGADA, tempoRealChegada);
        agendamentos.add(novaChegada);
        agendamentos.sort(Comparator.comparing(Controle::getTempo));
    }

    public void agendamentoSaida(final Fila fila) {

        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (fila.saidaMaxima - fila.saidaMinima) * aleatorio.numerosAleatorios[Aleatorio.index++] + fila.saidaMinima;

        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        final Controle novaSaida = new Controle(Controle.ControleEnum.SAIDA, tempoRealSaida);
        agendamentos.add(novaSaida);
        agendamentos.sort(Comparator.comparing(Controle::getTempo));
    }

    public void adicionarLista(List<Fila> filas) {
        this.filas = filas;
    }
}
