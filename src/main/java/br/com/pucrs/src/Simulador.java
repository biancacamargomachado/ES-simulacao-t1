package br.com.pucrs.src;

import br.com.pucrs.src.Evento.TipoEnum;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.pucrs.src.Aleatorio.geraProximoAleatorio;
import static br.com.pucrs.src.Aleatorio.quantidadeAleatoriosGerados;
import static br.com.pucrs.src.Evento.TipoEnum.*;

public class Simulador {

    public static int EVENT_NUMBER = 1;
    private static int INT = 0;
    public String ARQUIVO_YML = "application.yml"; //Arquivo com as configurações da fila
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

            if (eventoAtual.tipo == CHEGADA) {
                chegada(filaAtual);
            }

            if (eventoAtual.tipo == SAIDA_1) {
                saida_1(filaAtual);
            }

            if (eventoAtual.tipo == SAIDA_2) {
                saida_2(filaAtual);
            }

            if (eventoAtual.tipo == PASSAGEM) {
                passagem(filaAtual);
            }
        }

        //Exibe o resultado final
        this.exibirProbabilidade();
    }

    private void chegada(Fila fila) {
        System.out.printf("(%02d) %s | %.2f \n", EVENT_NUMBER++, CHEGADA, tempo);
        contabilizaTempo();

        if (fila.capacidade == -1 || fila.populacaoAtual < fila.capacidade) {
            fila.populacaoAtual++;
            if (fila.populacaoAtual <= fila.servidores) {
                if (sorteio(fila) != null) { // se for para outra fila
                    agendaPassagem(fila);
                } else {
                    agendaSaida(fila, SAIDA_1);
                }
            }
        } else {
            fila.perdidos++;
        }

        agendaChegada(fila);
    }

    private void passagem(Fila origem) {
        System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, PASSAGEM, tempo);
        contabilizaTempo();

        origem.populacaoAtual--;

        if (origem.populacaoAtual >= origem.servidores) {
            if (sorteio(origem) != null) {
                agendaPassagem(origem);
            } else {
                agendaSaida(origem, SAIDA_1);
            }
        }

        Fila destino = sorteioSemConsumirAleatorio(origem);

        if (destino != null) {
            destino.populacaoAtual++;
            if (destino.populacaoAtual <= destino.servidores) {
                agendaSaida(destino, SAIDA_2);
            }
        }
    }

    private void saida_1(Fila fila) { /** saida para rua durante passagem */
        System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, SAIDA_1, tempo);

        contabilizaTempo();

        fila.populacaoAtual--;

        if (fila.populacaoAtual >= fila.servidores) {
            if (sorteio(fila) != null) {
                agendaPassagem(fila);
            } else {
                agendaSaida(fila, SAIDA_1);
            }
        }
    }

    // Comentado pois no model odo professor não haverá uma saida direto para rua, sempre ha a possibilidade de ir para uma outra fila
    private void saida_2(Fila fila) { /** saida direto para rua */
        System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, SAIDA_2, tempo);
        contabilizaTempo();
        fila.populacaoAtual--;

        if (fila.populacaoAtual >= fila.servidores) {
            agendaSaida(fila, SAIDA_2);
        }
    }

    public void agendaChegada(Fila fila) {
        double tempoChegada = (fila.chegadaMaxima - fila.chegadaMinima) * (geraProximoAleatorio() + fila.chegadaMinima);
        double tempoRealChegada = tempoChegada + tempo;

        Evento evento = new Evento(CHEGADA, tempoRealChegada, fila.id);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }

    public void agendaPassagem(Fila fila) {
        double tempoSaida = (fila.saidaMaxima - fila.saidaMinima) * geraProximoAleatorio() + fila.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento evento = new Evento(PASSAGEM, tempoRealSaida, fila.id);
//      evento.setIdDestino(destino.id);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaSaida(Fila filaAtual, TipoEnum tipo) { // PASSA O TIPO DO ENUM CASO FOR NECESSARIO USAR O SAIDA_2
        double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * geraProximoAleatorio() + filaAtual.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento evento = new Evento(tipo, tempoRealSaida, filaAtual.id);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public Fila sorteio(final Fila origem) {

        double intervalo = 0.0;
        final double aleatorio = geraProximoAleatorio();

        //faz o sort do hahmap da menor probabilidade para a maior, afim de calcular o intervalo
        Map<Integer, Double> sortedMap = origem.probabilidades.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));

        Fila filaDestino = null;

        for (Integer fila : sortedMap.keySet()) {
            intervalo += sortedMap.get(fila);
            if (aleatorio <= intervalo) { // se o numero aleatorio for menor que o do intervalo
                filaDestino = escalonador.filas.get(fila); // adiciona a fila destino
                break; //para iteração, pois ja achou o resultado
            }
        }
//        System.out.println("Fila " + origem.id + " -> Fila " + filaDestino.id);
        return filaDestino;
    }

    public Fila sorteioSemConsumirAleatorio(final Fila origem) {

        double intervalo = 0.0;
        Random random = new Random();
        final double aleatorio = random.nextDouble() * 1.0;

        //faz o sort do hahmap da menor probabilidade para a maior, afim de calcular o intervalo
        Map<Integer, Double> sortedMap = origem.probabilidades.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));

        Fila filaDestino = null;

        for (Integer fila : sortedMap.keySet()) {
            intervalo += sortedMap.get(fila);
            if (aleatorio <= intervalo) { // se o numero aleatorio for menor que o do intervalo
                filaDestino = escalonador.filas.get(fila); // adiciona a fila destino
                break; //para iteração, pois ja achou o resultado
            }
        }
//        System.out.println("Fila " + origem.id + " -> Fila " + filaDestino.id);
        return filaDestino;
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
            for (double prob : probabilidades.get(id)) {
                double result = ((prob * 1.0) / this.tempo) * 100;

                String print = String.format("Value %.2f", result);
                System.out.println(print + "%");
                porcentagem += result;
            }

            System.out.println("Total de clientes perdidos: " + escalonador.filas.get(id).perdidos);
            System.out.println("Soma das porcentagens: " + Math.round(porcentagem) + "%");
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

        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k de multiplas filas
        escalonador.filas.forEach(f -> {
            /**
             *
             * ALTERAR O ULTIMO VALOR DA LINHA DO FINAL DESSE FOREACH CASO ESTOURE NULLPOINTEREXCEPTION
             *
             * */
            probabilidades.put(f.id, new double[f.capacidade != -1 ? f.capacidade + 1: 5]); /** ALTERAR O VALOR APOS O : */
        });

        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(CHEGADA, escalonador.filas.get(0).chegadaInicial, escalonador.filas.get(0).id); // pega a primeira fila
        agendamentos.add(primeiroEvento);
    }
}

