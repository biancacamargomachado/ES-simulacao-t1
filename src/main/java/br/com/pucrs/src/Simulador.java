package br.com.pucrs.src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml";

    public int qtdNumerosAleatorios;
    public EscalanadorDeFilas escalanadorDeFilas;

    public Simulador() {
        this.escalanadorDeFilas = new EscalanadorDeFilas();
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
            return novaFila;
        }).collect(Collectors.toList());

        escalanadorDeFilas.filas.addAll(filas); // adiciona todas as filas no escalonador.

        System.out.println("break point");
    }
}

