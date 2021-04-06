package br.com.pucrs.src;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml";

    public int qtdNumerosAleatorios;
    public EscalanadorDeFilas escalanadorDeFilas =  new EscalanadorDeFilas();
    public double tempo;

    public void executar() {
        int i = 0;
        while (i < this.qtdNumerosAleatorios) {
            Controle controle = this.escalanadorDeFilas.agendamentos.remove(0); // pega o próximo evento a ocorrer
            tempo = controle.tempo;

            Fila fila = escalanadorDeFilas.filas.get(0);

            if (controle.controleEnum == Controle.ControleEnum.CHEGADA) {
                fila.chegada();
            }

            if (controle.controleEnum == Controle.ControleEnum.SAIDA) {
                fila.chegada();
            }

            if (i < this.qtdNumerosAleatorios)
                this.escalanadorDeFilas.agendamentoChegada(fila);
        }
    }

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
            novaFila.probabilidade = new double[novaFila.capacidade];
            novaFila.escalonador = escalanadorDeFilas;
            return novaFila;
        }).collect(Collectors.toList());

        this.escalanadorDeFilas.filas.addAll(filas); // adiciona todas as filas no escalonador.
        System.out.println( this.escalanadorDeFilas);
        // agenda primeiro controle de tempo (tabela laranja)
        Controle controle = new Controle(Controle.ControleEnum.CHEGADA, escalanadorDeFilas.filas.get(0).chegadaInicial);
        escalanadorDeFilas.agendamentos.add(controle);
    }

    public void agendaChegada(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoChegada = (filaAtual.chegadaMaxima - filaAtual.chegadaMinima) * aleatorio + filaAtual.chegadaMinima;
        // t + tempo atual
        double tempoRealChegada = tempoChegada + tempo;

        final Controle novoControle = new Controle(Controle.ControleEnum.CHEGADA, tempoRealChegada);
        escalanadorDeFilas.agendamentos.add(novoControle);
        escalanadorDeFilas.agendamentos.sort(Comparator.comparing(Controle::getTempo));
    }

    public void agendaSaida(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * aleatorio + filaAtual.saidaMinima;
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        final Controle novoControle = new Controle(Controle.ControleEnum.SAIDA, tempoRealSaida);
        escalanadorDeFilas.agendamentos.add(novoControle);
        escalanadorDeFilas.agendamentos.sort(Comparator.comparing(Controle::getTempo));
    }
}

