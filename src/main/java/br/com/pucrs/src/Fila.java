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
    public Fila(double chegadaInicial, int servidores, int capacidade, double chegadaMinima, double chegadaMaxima, double saidaMinima, double saidaMaxima) {
        this.chegadaInicial = chegadaInicial;
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.chegadaMinima = chegadaMinima;
        this.chegadaMaxima = chegadaMaxima;
        this.saidaMinima = saidaMinima;
        this.saidaMaxima = saidaMaxima;
        this.populacaoAtual = 0;
        this.perdidos = 0;
    }

    public Fila() {

    }
}
