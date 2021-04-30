package br.com.pucrs.src;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.pucrs.src.Aleatorio.*;
import static br.com.pucrs.src.Aleatorio.geraProximoAleatorio;
import static br.com.pucrs.src.Evento.*;
import static br.com.pucrs.src.Evento.TipoEnum.*;

public class Simulador {

    private static int INT = 0;
    public String ARQUIVO_YML = "application.yml"; //Arquivo com as configurações da fila

    public static int EVENT_NUMBER = 1;
    public int quantidadeIteracoes;

    public EscalonadorDeFilas escalonador;
    public List<Evento> agendamentos = new ArrayList<>();
    public double tempo;
    public double tempoAnterior = 0;
    public Map<Integer, double[]> probabilidades = new HashMap<>();

    public Simulador() {
        this.escalonador = new EscalonadorDeFilas();
    }

    public void simulacao() {

        while (quantidadeAleatoriosGerados < this.quantidadeIteracoes) {
            //Pega o evento a ser processado
            Evento eventoAtual = agendamentos.remove(0);

            //Atualiza o tempo para depois propagar todas as filas
            tempo = eventoAtual.tempo;

            //Buscar pelo id no escalonador
            Fila filaAtual = escalonador.filas.get(eventoAtual.idFila);

            if (eventoAtual.tipo == TipoEnum.CHEGADA) {
                chegada(filaAtual);
            }

            if (eventoAtual.tipo == SAIDA) {
                saida(filaAtual);
            }

            if (eventoAtual.tipo == TipoEnum.PASSAGEM) {
                passagem(filaAtual);
            }
        }
        //Exibe o resultado final
        this.exibirProbabilidade();
    }

    private void chegada(Fila filaAtual) {
        //System.out.printf("(%02d) %s | %.2f \n", EVENT_NUMBER++, CHEGADA, tempo);

        contabilizaTempo();

        //Verifica se ainda tem  espaço na fila
        if (filaAtual.populacaoAtual < filaAtual.capacidade) {
            filaAtual.populacaoAtual++;
            //Verifica se tem servidor livre para atendimento imediato
            if (filaAtual.populacaoAtual <= filaAtual.servidores) {
                agendaPassagem(filaAtual);
            }
        } else { //Não entrou na fila porque estava cheia
            filaAtual.perdidos++;
        }
        agendaChegada(filaAtual);
    }

    private void passagem(Fila fila) {
        //System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, PASSAGEM, tempo);

        contabilizaTempo();

        fila.populacaoAtual--;

        if (fila.populacaoAtual >= fila.servidores) {
            agendaPassagem(fila);
        }

        // Valor fixado, mudar para o metodo 'Sorteio'
        Fila destino = fila.filaDestino.get(1);

        if (destino.populacaoAtual < destino.capacidade) {
            destino.populacaoAtual++;
            if (destino.populacaoAtual <= destino.servidores) {
                agendaSaida(destino);
            }
        } else {
            destino.perdidos++;
        }
    }

    private void saida(Fila fila) {
        //System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, SAIDA, tempo);

        contabilizaTempo();

        fila.populacaoAtual--;

        if (fila.populacaoAtual >= fila.servidores) {
            agendaSaida(fila);
        }
    }

    public void agendaChegada(Fila filaAtual) {
        double tempoChegada = (filaAtual.chegadaMaxima - filaAtual.chegadaMinima) * (geraProximoAleatorio() + filaAtual.chegadaMinima);
        double tempoRealChegada = tempoChegada + tempo;

        Evento novaChegada = new Evento(TipoEnum.CHEGADA, tempoRealChegada, filaAtual.id);

        agendamentos.add(novaChegada);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }

    public void agendaPassagem(Fila filaAtual) {
        double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * geraProximoAleatorio() + filaAtual.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(TipoEnum.PASSAGEM, tempoRealSaida, filaAtual.id);

        agendamentos.add(novaSaida);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaSaida(Fila filaAtual) {
        double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * geraProximoAleatorio() + filaAtual.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(SAIDA, tempoRealSaida, filaAtual.id);
        agendamentos.add(novaSaida);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void contabilizaTempo() {
        for (Fila fila : escalonador.filas) {
            probabilidades.get(fila.id)[fila.populacaoAtual] += this.tempo - this.tempoAnterior;
        }
        tempoAnterior = tempo;
    }

    public void exibirProbabilidade() {
        System.out.println("******************************");

        double porcentagem = 0;

        for (int id : probabilidades.keySet()) {
            System.out.println("- FILA " + id);
            System.out.println("Perdidos: " + escalonador.filas.get(id).perdidos);
            for (double prob : probabilidades.get(id)) {
                double result =  ((prob * 1.0) / this.tempo) * 100;
                String print = String.format("Value %.2f", result);
                System.out.println(print + "%");
            }

            System.out.println(porcentagem * 100 + "%");
            System.out.println("Tempo total: " + tempo);
            porcentagem = 0;
        }
        System.out.println("******************************");

    }

    public void mapearYamlParaPOJO() {

        Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.quantidadeIteracoes = (int) dados.get("numeros-aleatorios");

        List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");

        //Mapeia do .yml para uma instancia de Fila a representacao dos dados contidos no arquivo
        List<Fila> filas = dadosFilas.stream().map(fila -> {
            Fila novaFila = new Fila();
            novaFila.id = (int) fila.get("id");
            novaFila.capacidade = (int) fila.get("capacidade");
            novaFila.chegadaInicial = (double) (fila.containsKey("chegada-inicial") ? fila.get("chegada-inicial") : -1.0);
            novaFila.chegadaMaxima = (double) (fila.containsKey("chegada-maxima") ? fila.get("chegada-maxima") : -1.0);
            novaFila.chegadaMinima = (double) (fila.containsKey("chegada-minima") ? fila.get("chegada-minima") : -1.0);
            novaFila.saidaMaxima = (double) (fila.containsKey("saida-maxima") ? fila.get("saida-maxima") : -1.0);
            novaFila.saidaMinima = (double) (fila.containsKey("saida-minima") ? fila.get("saida-minima") : -1.0);
            novaFila.servidores = (int) fila.get("servidores");
            return novaFila;
        }).collect(Collectors.toList());

        // montar topologia de rede
        // dados de rede contém todas a filas
        List<LinkedHashMap<String, Object>> dadosRedes = (List<LinkedHashMap<String, Object>>) dados.get("redes");

        // itera a estrutura para popular as filas
        for (HashMap<String, Object> rede : dadosRedes) {

            int origem = (int) rede.get("origem");
            int destino = (int) rede.get("destino");
            double probabilidade = (double) rede.get("probabilidade");

            Fila filaOrigem = filas.stream().filter(f -> f.id == origem).findFirst().get();
            Fila filaDestino = filas.stream().filter(f -> f.id == destino).findFirst().get();

            // relaciona o destino à origem
            filaOrigem.filaDestino.put(destino, filaDestino);

            // propabilidade que é passada no arquivo yml
            filaOrigem.probabilidades.put(destino, probabilidade);
        }

        System.out.println("EVENTO       |" + " TEMPO");
        escalonador.filas.addAll(filas); //Adiciona todas filas no escalonador
        escalonador.filas.remove(0); //Remove o primeiro item, que é vazio


        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k
        //probabilidade = new double[escalonadorDeFilas.filas.get(0).capacidade + 1];

        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k de multiplas filas
        escalonador.filas.forEach(f -> {
            probabilidades.put(f.id, new double[f.capacidade + 1]);
        });
        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(TipoEnum.CHEGADA, escalonador.filas.get(0).chegadaInicial, escalonador.filas.get(0).id); // pega a primeira fila
        agendamentos.add(primeiroEvento);
        //System.out.println("AGENDADO |" + primeiroEvento.tipo + " | " + primeiroEvento.tempo);
    }

}

