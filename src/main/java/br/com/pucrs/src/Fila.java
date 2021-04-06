package br.com.pucrs.src;

public class Fila {

    // dados carregados do yml
    public double chegadaInicial;
    public int servidores;
    public int capacidade;
    public double chegadaMinima;
    public double chegadaMaxima;
    public double saidaMinima;
    public double saidaMaxima;

    // dados de controle
    public int populacaoAtual;
    public int perdidos;

    public EscalanadorDeFilas escalonador;
    public double[] probabilidade;

    /**
     * Construtor utilizado para criar o objeto fila com os parâmetros carregado do arquivo yaml.
     *
     * @param chegadaInicial
     * @param servidores
     * @param capacidade
     * @param chegadaMinima
     * @param chegadaMaxima
     * @param saidaMinima
     * @param saidaMaxima
     */
    public Fila(double chegadaInicial, int servidores, int capacidade, double chegadaMinima, double chegadaMaxima, double saidaMinima, double saidaMaxima, EscalanadorDeFilas escalanadorDeFilas) {
        this.chegadaInicial = chegadaInicial;
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.chegadaMinima = chegadaMinima;
        this.chegadaMaxima = chegadaMaxima;
        this.saidaMinima = saidaMinima;
        this.saidaMaxima = saidaMaxima;
        this.populacaoAtual = 0;
        this.perdidos = 0;

        // ideia do professor em ter um escalonador para gerenciar as filas
        this.escalonador = escalanadorDeFilas;

        // adicionar probabilidade % de chance da fila estar com x pessoas em seu k
        probabilidade = new double[capacidade];

    }

    public Fila() {
    }

    public void chegada() {
        this.ajustarProbabilidade();
        if (populacaoAtual <= capacidade) {
            populacaoAtual++;
            if (populacaoAtual <= 1) { // se só tem uma pessoa na fila ou nenhuma -> já é atendido
                escalonador.agendamentoSaida(this);
            }
        } else {
            perdidos++; // não conseguiu entrar na fila
        }
        escalonador.agendamentoChegada(this); // agenda a chegada de um novo cliente
    }

    public void saida() {
        this.ajustarProbabilidade();
        populacaoAtual--;
        if (this.populacaoAtual >= this.servidores) {
            this.escalonador.agendamentoSaida(this);
        }
    }

    /**
     * verificar se precisar guardar o tempo anterior para fazer o calculo do tempo atual com o ultimo tempo?
     * verificar com o professor.
     */
    public void ajustarProbabilidade() {
        double k = probabilidade[populacaoAtual];
        probabilidade[populacaoAtual] += k - escalonador.tempo; // errado
    }
}
