package br.com.pucrs.src;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.pucrs.src.Aleatorio.geraProximoAleatorio;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml"; //Arquivo com as configurações da fila

    public static int NUMBER_EVENT = 1;
    public int qtdNumerosAleatorios;
    public EscalonadorDeFilas escalonadorDeFilas;
    public List<Evento> eventosAcontecendo = new ArrayList<>();
    public List<Evento> eventosAgendados = new ArrayList<>();
    public double tempo;
    public double tempoAnterior = 0;
    public Map<Integer, double[]> probabilidades = new HashMap<>();

    public Simulador() {
        this.escalonadorDeFilas = new EscalonadorDeFilas();
    }

    public void simulacao(final Aleatorio aleatorios) {

        while (aleatorios.qtAleatorios < this.qtdNumerosAleatorios) {
            final Evento eventoAtual = eventosAgendados.get(0);    //Pega o próximo evento a ocorrer
            eventosAgendados.remove(0);                      //Remove o evento dos agendados, pois já está sendo executado
            eventosAcontecendo.add(eventoAtual);                   //Adiciona no evento que está acontecendo

            tempo = eventoAtual.tempo;

            final Fila filaAtual = escalonadorDeFilas.filas.get(eventoAtual.fila.id - 1);

            if (eventoAtual.tipo == Evento.TipoEnum.CHEGADA) {
                chegada(eventoAtual, filaAtual);
            }

            if (eventoAtual.tipo == Evento.TipoEnum.SAIDA) {
                saida(eventoAtual, filaAtual);
            }

            if (eventoAtual.tipo == Evento.TipoEnum.PASSAGEM) {
                passagem(eventoAtual, filaAtual);
            }
        }
        this.exibirProbabilidade();
    }

    private void chegada(Evento eventoAtual, Fila filaAtual) {
        System.out.printf("(%02d) %s | %.2f \n", NUMBER_EVENT++, eventoAtual.tipo.name(), eventoAtual.tempo);

        contabilizaTempo();

        //Se ainda tempo espaço na fila
        if (filaAtual.populacaoAtual < filaAtual.capacidade) {
            filaAtual.populacaoAtual++;
            //Se só tem uma pessoa na fila ou nenhuma, essa pessoa já é atendida
            if (filaAtual.populacaoAtual <= filaAtual.servidores) {
                agendaPassagem(Aleatorio.geraProximoAleatorio(), filaAtual);
            }
        }

        agendaChegada(geraProximoAleatorio(), filaAtual);

    }

    private void passagem(Evento eventoAtual, Fila fila) {
        System.out.printf("(%02d) %s   | %.2f \n", NUMBER_EVENT++, eventoAtual.tipo.name(), eventoAtual.tempo);

        contabilizaTempo();

        fila.populacaoAtual--;

        if (fila.populacaoAtual >= fila.servidores) {
            agendaPassagem(geraProximoAleatorio(), fila);
        }

        final Fila destino = sorteioFila(fila);

        if (destino.populacaoAtual < destino.capacidade) {
            destino.populacaoAtual++;
            if (destino.populacaoAtual <= destino.servidores) {
                agendaSaida(geraProximoAleatorio(), destino);
            }
        }
    }

    private void saida(Evento eventoAtual, Fila fila) {
        System.out.printf("(%02d) %s   | %.2f \n", NUMBER_EVENT++, eventoAtual.tipo.name(), eventoAtual.tempo);

        contabilizaTempo();

        fila.populacaoAtual--;

        //Se tem gente na espera pra ficar de frente para o servidor
        if (fila.populacaoAtual >= fila.servidores) {
            agendaSaida(geraProximoAleatorio(), fila);
        }
    }

    public void agendaChegada(double aleatorio, Fila filaAtual) {
        final double tempoChegada = (filaAtual.chegadaMaxima - filaAtual.chegadaMinima) * (aleatorio + filaAtual.chegadaMinima);
        final double tempoRealChegada = tempoChegada + tempo;

        final Evento novaChegada = new Evento(Evento.TipoEnum.CHEGADA, tempoRealChegada, filaAtual);

        eventosAgendados.add(novaChegada);
        eventosAgendados.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaChegada.tipo + " | " + tempoRealChegada);
    }

    public void agendaPassagem(double aleatorio, Fila filaAtual) {

        final double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * aleatorio + filaAtual.saidaMinima;

        final double tempoRealSaida = tempoSaida + tempo;

        final Evento novaSaida = new Evento(Evento.TipoEnum.PASSAGEM, tempoRealSaida, filaAtual);
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }

    public void agendaSaida(final double aleatorio, final Fila filaAtual) {

// t = ((B-A) * aleatorio + A)
        final double tempoSaida = (filaAtual.saidaMaxima - filaAtual.saidaMinima) * aleatorio + filaAtual.saidaMinima;

// t + tempo atual
        final double tempoRealSaida = tempoSaida + tempo;

// Continua na mesma fila (fila atual)
        final Evento novaSaida = new Evento(Evento.TipoEnum.SAIDA, tempoRealSaida, filaAtual);
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(event -> event.tempo));

        //System.out.println("AGENDADO |" + novaSaida.tipo + " | " + tempoRealSaida);
    }


    public Fila sorteioFila(final Fila origem) {
        final Random random = new Random();
        final double number = random.nextDouble() * 1.0;

        Fila destino = null;
        for (Integer fila : origem.probabilidades.keySet()) {
            double probabilidade = origem.probabilidades.get(fila);
            if (number <= probabilidade) {
                destino = origem.filaDestino.get(fila);
                break;
            }
        }
        return destino;
    }

    public void contabilizaTempo() {

        for (Fila fila : escalonadorDeFilas.filas) {
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
                porcentagem += (prob / this.tempo);
                String result = String.format("Value %.4f", ((prob / this.tempo) * 100));
                System.out.println(result + "%");
            }

            System.out.println(porcentagem * 100 + "%");
            System.out.println("Tempo total: " + tempo);
            porcentagem = 0;
        }
        System.out.println("******************************");

    }

    public void mapearYamlParaPOJO() {

        final Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.qtdNumerosAleatorios = (int) dados.get("numeros-aleatorios");

        final List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");

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
        final List<LinkedHashMap<String, Object>> dadosRedes = (List<LinkedHashMap<String, Object>>) dados.get("redes");

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
        escalonadorDeFilas.filas.addAll(filas); //Adiciona todas filas no escalonador
        escalonadorDeFilas.filas.remove(0); //Remove o primeiro item, que é vazio


        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k
        //probabilidade = new double[escalonadorDeFilas.filas.get(0).capacidade + 1];

        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k de multiplas filas
        escalonadorDeFilas.filas.forEach(f -> {
            probabilidades.put(f.id, new double[f.capacidade + 1]);
        });
        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(Evento.TipoEnum.CHEGADA, escalonadorDeFilas.filas.get(0).chegadaInicial, filas.get(0)); // pega a primeira fila
        eventosAgendados.add(primeiroEvento);
        //System.out.println("AGENDADO |" + primeiroEvento.tipo + " | " + primeiroEvento.tempo);
    }

}

