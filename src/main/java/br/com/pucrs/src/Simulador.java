package br.com.pucrs.src;

import java.util.*;
import java.util.stream.Collectors;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml"; //Arquivo com as configurações da fila

    public int qtdNumerosAleatorios;
    public EscalonadorDeFilas escalonadorDeFilas;
    public List<Evento> eventosAcontecendo = new ArrayList<>();
    public List<Evento> eventosAgendados = new ArrayList<>();
    public double tempo;
    public double tempoAnterior = 0;
    public double[] probabilidade;

    private int countAleatorios;

    public Simulador() {
        this.escalonadorDeFilas = new EscalonadorDeFilas();
    }

    public void simulacao (Aleatorio aleatorio) {
        countAleatorios = 0;

        while(countAleatorios < this.qtdNumerosAleatorios) {
            Evento eventoAtual = eventosAgendados.get(0); //Pega o próximo evento a ocorrer
            eventosAgendados.remove(0);             //Remove o evento dos agendados, pois já está sendo executado
            eventosAcontecendo.add(eventoAtual);          //Adiciona no evento que está acontecendo

            //Variável tempoAnterior é utilizada para o cálculo de probabilidade
            tempoAnterior = tempo;
            tempo = eventoAtual.tempo;

            Fila filaAtual = escalonadorDeFilas.filas.get(0);

            if (eventoAtual.tipo == Evento.TipoEnum.ENTRADA) {
                entrada(eventoAtual, filaAtual, aleatorio);
            } else if (eventoAtual.tipo == Evento.TipoEnum.SAIDA) {
                saida(eventoAtual, filaAtual, aleatorio);
            }

            if (countAleatorios < this.qtdNumerosAleatorios && eventoAtual.tipo != Evento.TipoEnum.SAIDA) {
                agendaChegada(aleatorio.numerosAleatorios[countAleatorios], filaAtual);
                countAleatorios++;
            }
        }

        //Exibir probabilidades
        this.exibirProbabilidade();
    }

    private void entrada(Evento eventoAtual, Fila filaAtual, Aleatorio aleatorio){
        if (filaAtual.populacaoAtual <= filaAtual.capacidade) {
            this.ajustarProbabilidade(filaAtual);
        }

        //Se ainda tempo espaço na fila
        if (filaAtual.populacaoAtual < filaAtual.capacidade) {
            filaAtual.populacaoAtual++;

            //Se só tem uma pessoa na fila ou nenhuma, essa pessoa já é atendida
            if (filaAtual.populacaoAtual <= filaAtual.servidores) {
                System.out.println("EXECUTADO |" + eventoAtual.tipo + " | " + eventoAtual.tempo);
                agendaSaida(aleatorio.numerosAleatorios[countAleatorios], filaAtual);
                countAleatorios++; //Quando usou um aleatório, passa para próximo
            }
        } else {
            //Não conseguiu entrar na fila pois estava cheia. E contabilizada como uma pessoa perdida
            filaAtual.perdidos++;
        }
    }

    private void saida(Evento eventoAtual, Fila filaAtual, Aleatorio aleatorio){
        System.out.println("EXECUTADO |" + eventoAtual.tipo + " | " + eventoAtual.tempo);
        this.ajustarProbabilidade(filaAtual);
        filaAtual.populacaoAtual--;

        //Se tem gente na espera pra ficar de frente para o servidor
        if (filaAtual.populacaoAtual >= filaAtual.servidores) {
            agendaSaida(aleatorio.numerosAleatorios[countAleatorios], filaAtual);
            countAleatorios++; //Quando usou um aleatório, passa para próximo
        }
    }

    public void mapearYamlParaPOJO() {

        final Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.qtdNumerosAleatorios = (int) dados.get("numeros-aleatorios");

        final List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");

        //Mapeia do .yml para uma instancia de Fila a representacao dos dados contidos no arquivo
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
        escalonadorDeFilas.filas.addAll(filas); //Adiciona todas filas no escalonador
        escalonadorDeFilas.filas.remove(0); //Remove o primeiro item, que é vazio

        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k
        probabilidade = new double[escalonadorDeFilas.filas.get(0).capacidade + 1];

        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(Evento.TipoEnum.ENTRADA, escalonadorDeFilas.filas.get(0).chegadaInicial);
        eventosAgendados.add(primeiroEvento);
        System.out.println("AGENDADO |" + primeiroEvento.tipo + " | " + primeiroEvento.tempo);
    }

    public void agendaSaida(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaAtual.saidaMaxima -  filaAtual.saidaMinima) * aleatorio + filaAtual.saidaMinima;
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(Evento.TipoEnum.SAIDA, tempoRealSaida);
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(event -> event.tempo));

        System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaChegada(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoChegada = (filaAtual.chegadaMaxima - filaAtual.chegadaMinima) * aleatorio + filaAtual.chegadaMinima;
        // t + tempo atual
        double tempoRealChegada = tempoChegada + tempo;

        Evento novaChegada = new Evento(Evento.TipoEnum.ENTRADA, tempoRealChegada);
        eventosAgendados.add(novaChegada);
        eventosAgendados.sort(Comparator.comparingDouble(event -> event.tempo));

        System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }

    public void ajustarProbabilidade(Fila filaAtual) {
        probabilidade[filaAtual.populacaoAtual] += this.tempo - this.tempoAnterior;
    }

    public void exibirProbabilidade() {
        System.out.println("Probabilidades:");

        double porcentagem = 0;

        for (double item : probabilidade){
            porcentagem += (item / this.tempo);
            String result = String.format("Value %.4f", (item / this.tempo));
            System.out.println(result + "%");
        }

        System.out.println(porcentagem + "%");
    }
}

