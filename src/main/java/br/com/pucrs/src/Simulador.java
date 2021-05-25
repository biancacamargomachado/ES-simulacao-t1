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

        while (quantidadeAleatoriosGerados <= this.quantidadeIteracoes) {
            //Pega o evento a ser processado
            Evento eventoAtual = agendamentos.remove(0);

            //Atualiza o tempo para depois propagar todas as filas
            tempo = eventoAtual.tempo;

            //Buscar pelo id no escalonador
            Fila filaAtual = escalonador.filas.get(eventoAtual.idOrigem);
            Fila filaDestino = null;
            if(eventoAtual.idDestino != null){
                filaDestino = escalonador.filas.get(eventoAtual.idDestino);
            }

            if (eventoAtual.tipo == CHEGADA) {
                chegada(filaAtual);
            }else if (eventoAtual.tipo == SAIDA_1) {
                saida_1(filaAtual);
            }else if (eventoAtual.tipo == PASSAGEM) {
                passagem(filaAtual, filaDestino);
            }

//            if (eventoAtual.tipo == SAIDA_2) {
//                saida_2(filaAtual);
//            }

        }

        // exibe o resultado final
        this.exibirProbabilidade();
    }

    private void chegada(Fila origem) {
        //System.out.printf("(%02d) %s | %.2f \n", EVENT_NUMBER++, CHEGADA, tempo);
        // exibirProbabilidade();
        contabilizaTempo();
        // exibirProbabilidade();

        if (origem.capacidade == -1 || origem.populacaoAtual < origem.capacidade) {
            origem.populacaoAtual++;
            if (origem.populacaoAtual <= origem.servidores) {
                Fila destino = sorteio(origem);
                if (destino!= null) { // se for para outra fila
                    agendaPassagem(origem, destino);
                } else {
                    agendaSaida(origem, SAIDA_1);
                }
            }
        } else {
            origem.perdidos++; //?
        }

        agendaChegada(origem);
    }

    private void passagem(Fila origem, Fila destino) {
        //System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, PASSAGEM, tempo);
        // exibirProbabilidade();
        contabilizaTempo();
        // exibirProbabilidade();

        origem.populacaoAtual--;

        if (origem.populacaoAtual >= origem.servidores) {
            Fila destinoProxFilaOrigem = sorteio(origem);
            if (destinoProxFilaOrigem != null) {
                agendaPassagem(origem, destinoProxFilaOrigem);
            } else {
                agendaSaida(origem, SAIDA_1);
            }
        }

        //Fila destino = sorteioSemConsumirAleatorio(origem);

        if (destino != null) {
            if (destino.capacidade == -1 || destino.populacaoAtual < destino.capacidade) {
                destino.populacaoAtual++;
                if (destino.populacaoAtual <= destino.servidores) {
                    Fila destino2 = sorteio(destino);
                    if (destino2 != null) { // se for para outra fila
                        agendaPassagem(destino, destino2);
                    } else {
                        agendaSaida(destino, SAIDA_1);
                    }
                }
            } else {
                destino.perdidos++;
            }
        }
    }

    private void saida_1(Fila origem) { /** saida para rua durante passagem */
        //System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, SAIDA_1, tempo);
        // exibirProbabilidade();
        contabilizaTempo();
        // exibirProbabilidade();

        origem.populacaoAtual--;

        if (origem.populacaoAtual >= origem.servidores) {
            Fila destino = sorteio(origem);
            if (destino != null) {
                agendaPassagem(origem, destino);
            } else {
                agendaSaida(origem, SAIDA_1);
            }
        }
    }

    // Comentado pois no model odo professor não haverá uma saida direto para rua, sempre ha a possibilidade de ir para uma outra fila
//    private void saida_2(Fila fila) { /** saida direto para rua */
//        System.out.printf("(%02d) %s   | %.2f \n", EVENT_NUMBER++, SAIDA_2, tempo);
//        contabilizaTempo();
//        fila.populacaoAtual--;
//
//        if (fila.populacaoAtual >= fila.servidores) {
//            agendaSaida(fila, SAIDA_2);
//        }
//    }

    public void agendaChegada(Fila fila) {
        double tempoChegada = (fila.chegadaMaxima - fila.chegadaMinima) * geraProximoAleatorio() + fila.chegadaMinima;
        double tempoRealChegada = tempoChegada + tempo;

        Evento evento = new Evento(CHEGADA, tempoRealChegada, fila.id, null);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }

    public void agendaPassagem(Fila origem, Fila destino) {
        double tempoSaida = (origem.saidaMaxima - origem.saidaMinima) * geraProximoAleatorio() + origem.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento evento = new Evento(PASSAGEM, tempoRealSaida, origem.id, destino.id);
        //evento.setIdDestino(destino.id);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaSaida(Fila filaOrigem, TipoEnum tipo) { // passa o tipo do enum caso seja necessario usar o saida_2
        double tempoSaida = (filaOrigem.saidaMaxima - filaOrigem.saidaMinima) * geraProximoAleatorio() + filaOrigem.saidaMinima;
        double tempoRealSaida = tempoSaida + tempo;

        Evento evento = new Evento(tipo, tempoRealSaida, filaOrigem.id, null);

        agendamentos.add(evento);
        agendamentos.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public Fila sorteio(final Fila origem) {
        double intervalo = 0.0;
        final double aleatorio = geraProximoAleatorio();

        // faz o sort do hahmap da menor probabilidade para a maior, afim de calcular o intervalo
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
                break; // para iteração, pois ja achou o resultado
            }
        }
        //System.out.println("Fila " + origem.id + " -> Fila " + filaDestino.id);
        return filaDestino;
    }

    public Fila sorteioSemConsumirAleatorio(final Fila origem) {
        double intervalo = 0.0;
        Random random = new Random();
        final double aleatorio = random.nextDouble() * 1.0;

        // faz o sort do hahmap da menor probabilidade para a maior, afim de calcular o intervalo
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
                break; // para iteração, pois ja achou o resultado
            }
        }

        //System.out.println("Fila " + origem.id + " -> Fila " + filaDestino.id);
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
        int posicao = 0;
        double porcentagem = 0;

        for (int id : probabilidades.keySet()) {
            System.out.println("- FILA " + id);
            for (double time : probabilidades.get(id)) {
                double prob = (time / this.tempo) * 100;

                // probabilidades.get(fila.id)[fila.populacaoAtual]
                String print = String.format("(%d) %.2f (%.2f)", posicao++, prob, time);
                System.out.println(print);
                porcentagem += prob;
            }

            System.out.println("Total de clientes perdidos: " + escalonador.filas.get(id).perdidos);
            System.out.println("Soma das porcentagens: " + Math.round(porcentagem) + "%");
            System.out.println("Tempo total: " + tempo);
            porcentagem = 0;
            posicao = 0;
        }
        System.out.println("******************************");
    }

    public void mapearYamlParaPOJO() {

        Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.quantidadeIteracoes = (int) dados.get("numeros-aleatorios");

        List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");

        // mapeia do .yml para uma instancia de Fila a representacao dos dados contidos no arquivo
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

        // adiciona probabilidade % de chance de a fila estar com x pessoas em seu k de multiplas filas
        escalonador.filas.forEach(f -> {
            /** ALTERAR O ULTIMO VALOR DA LINHA DO FINAL DESSE FOREACH CASO ESTOURE NULLPOINTEREXCEPTION */
            probabilidades.put(f.id, new double[f.capacidade != -1 ? f.capacidade + 1 : 10]); /** ALTERAR O VALOR APOS O : */
        });

        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(CHEGADA, escalonador.filas.get(0).chegadaInicial, escalonador.filas.get(0).id, null); // pega a primeira fila
        agendamentos.add(primeiroEvento);
    }
}