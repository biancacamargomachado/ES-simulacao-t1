package br.com.pucrs.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml";

    public int qtdNumerosAleatorios;
    public EscalanadorDeFilas escalanadorDeFilas;
    public List<Evento> eventosAcontecendo = new ArrayList<Evento>();
    public List<Evento> eventosAgendados = new ArrayList<Evento>(); // manter esta lista ordenada por menor tmepo de evento
    public double tempo;

    public Simulador() {
        this.escalanadorDeFilas = new EscalanadorDeFilas();
    }

    public void simulacao (Aleatorio aleatorio) {
        int i = 0;
        while(i < this.qtdNumerosAleatorios) {
            Evento eventoAtual = eventosAgendados.get(0); // pega o próximo evento a ocorrer
            eventosAgendados.remove(0); // remove o evento dos agendados, está sendo executado
            eventosAcontecendo.add(eventoAtual); // adiciona no evento que está acontecendo
            tempo = eventoAtual.tempo;

            Fila filaAtual = escalanadorDeFilas.filas.get(0);

            if (eventoAtual.tipo == Evento.TipoEnum.ENTRADA) {
                // se ainda tempo espaço na fila
                if (filaAtual.populacaoAtual <= filaAtual.capacidade) {
                    filaAtual.populacaoAtual++;
                    // se só tem uma pessoa na fila ou nenhuma -> já é atendido
                    if (filaAtual.populacaoAtual <= 1) {
                        System.out.println("EXECUTADO |" + eventoAtual.tipo + " | " + eventoAtual.tempo);
                        agendaSaida(aleatorio.numerosAleatorios[i], filaAtual);
                        i++; // usou um aleatório, passa pro próximo
                    }
                } else {
                    // não conseguiu entrar na fila, pois estava cheia
                    filaAtual.perdidos++;
                }
            } else if (eventoAtual.tipo == Evento.TipoEnum.SAIDA) {
                filaAtual.populacaoAtual--;
                System.out.println("EXECUTADO |" + eventoAtual.tipo + " | " + eventoAtual.tempo);
                if (filaAtual.populacaoAtual >= 1) { // tem gente na espera pra ficar de frente pro servidor?
                    agendaSaida(aleatorio.numerosAleatorios[i], filaAtual);
                    i++; // usou um aleatório, passa pro próximo
                }
            }

            if (i < this.qtdNumerosAleatorios)
                agendaChegada(aleatorio.numerosAleatorios[i], filaAtual);
        }
    };

    public void mapearYamlParaPOJO() {

        final Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.qtdNumerosAleatorios = (int) dados.get("numeros-aleatorios");

        final List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");

        /**
         * Mapeia do yaml para um object Fila a representação dos dados contidos no arquivo
         */
        List<Fila> filas = dadosFilas.stream().map(fila -> {
            Fila novaFila = new Fila();
            novaFila.capacidade = (int) fila.get("capacidade");
            novaFila.chegadaInicial = (double) fila.get("chegada-inicial");
            novaFila.chegadaMaxima = (double) fila.get("chegada-maxima");
            novaFila.chegadaMinima = (double) fila.get("chegada-minima");
            novaFila.saidaMaxima = (double) fila.get("saida-maxima");
            novaFila.saidaMinima = (double) fila.get("saida-minima");
            novaFila.servidores = (int) fila.get("servidores");
            return novaFila;
        }).collect(Collectors.toList());

        System.out.println("EVENTO   |" + "tipo    |" +  " tempo");
        // agendada a chegada
        escalanadorDeFilas.filas.addAll(filas); // adiciona todas as filas no escalonador.
        escalanadorDeFilas.filas.remove(0); // remove o primeiro item que é vazio

        // agenda primeiro evento
        Evento primeiroEvento = new Evento(Evento.TipoEnum.ENTRADA, escalanadorDeFilas.filas.get(0).chegadaInicial);
        eventosAgendados.add(primeiroEvento);
        System.out.println("AGENDADO |" + primeiroEvento.tipo + " | " + primeiroEvento.tempo);
    }

    public void agendaSaida(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaAtual.saidaMaxima -  filaAtual.saidaMinima) * aleatorio + filaAtual.saidaMinima;
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(Evento.TipoEnum.SAIDA, tempoRealSaida);
        eventosAgendados.add(novaSaida); // TODO: método para ordenar por tempo ao agendar -> menor tempo antes
        System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaChegada(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoChegada = (filaAtual.chegadaMaxima - filaAtual.chegadaMinima) * aleatorio + filaAtual.chegadaMinima;
        // t + tempo atual
        double tempoRealChegada = tempoChegada + tempo;

        Evento novaChegada = new Evento(Evento.TipoEnum.ENTRADA, tempoRealChegada);
        eventosAgendados.add(novaChegada); // TODO: método para ordenar por tempo ao agendar -> menor tempo antes
        System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }
}

