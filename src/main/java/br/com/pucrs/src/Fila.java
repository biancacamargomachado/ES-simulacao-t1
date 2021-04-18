package br.com.pucrs.src;

import java.util.HashMap;

public class Fila {
    //Dados carregados do arquivo de entrada .yml
    public int id;
    public double chegadaInicial;
    public int servidores;
    public int capacidade;
    public double chegadaMinima;
    public double chegadaMaxima;
    public double saidaMinima;
    public double saidaMaxima;

    //Dados de controle
    public int populacaoAtual;
    public int perdidos;

    //Topologia de filas
    public HashMap<Integer, Fila> filaDestino = new HashMap<>();
    public HashMap<Integer, Double> probabilidades = new HashMap<>();

    public Fila() {
    }
}