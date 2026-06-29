package com.mycompany.hashtable;

import java.util.*;

/**
 * Classe para análise comparativa de tabelas hash
 */
public class AnalisadorHash {

    public static class ResultadoAnalise {
        public String nomeEstrutura;
        public String nomeFuncao;
        public long tempoInsercao;
        public long tempoBusca;
        public long tempoRemocao;
        public int colisoes;
        public double fatorCarga;
        public int capacidadeFinal;

        public ResultadoAnalise(String nomeEstrutura, String nomeFuncao) {
            this.nomeEstrutura = nomeEstrutura;
            this.nomeFuncao = nomeFuncao;
        }

        public void exibir() {
            System.out.println("\n📊 === RESULTADO: " + nomeEstrutura + " ===");
            System.out.println("Função Hash: " + nomeFuncao);
            System.out.println("├─ Tempo Inserção: " + String.format("%,d", tempoInsercao / 1_000_000) + " ms");
            System.out.println("├─ Tempo Busca:    " + String.format("%,d", tempoBusca / 1_000_000) + " ms");
            System.out.println("├─ Tempo Remoção:  " + String.format("%,d", tempoRemocao / 1_000_000) + " ms");
            System.out.println("├─ Colisões:       " + colisoes);
            System.out.println("├─ Fator Carga:    " + String.format("%.2f", fatorCarga));
            System.out.println("└─ Capacidade:     " + capacidadeFinal);
        }
    }

    public static ResultadoAnalise testarTabelaHashLista(
            List<String[]> dados, 
            HashFunction funcaoHash,
            int capacidadeInicial) {

        ResultadoAnalise resultado = new ResultadoAnalise(
            "Tabela Hash - Encadeamento Separado",
            funcaoHash.getNome()
        );
        TabelaHashLista tabela = new TabelaHashLista(capacidadeInicial, funcaoHash);

        long inicio = System.nanoTime();
        for (String[] par : dados) {
            tabela.inserir(par[0], par[1]);
        }
        resultado.tempoInsercao = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (String[] par : dados) {
            tabela.buscar(par[0]);
        }
        resultado.tempoBusca = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (int i = 0; i < dados.size() / 2; i++) {
            tabela.remover(dados.get(i)[0]);
        }
        resultado.tempoRemocao = System.nanoTime() - inicio;

        resultado.colisoes = tabela.getColisoes();
        resultado.fatorCarga = tabela.getFatorCarga();
        resultado.capacidadeFinal = tabela.getCapacidade();

        return resultado;
    }

    public static ResultadoAnalise testarTabelaHashAberta(
            List<String[]> dados,
            HashFunction funcaoHash,
            TabelaHashAberta.TipoSondagem tipoSondagem,
            int capacidadeInicial) {

        ResultadoAnalise resultado = new ResultadoAnalise(
            "Tabela Hash - Endereçamento Aberto (" + tipoSondagem + ")",
            funcaoHash.getNome()
        );
        TabelaHashAberta tabela = new TabelaHashAberta(capacidadeInicial, funcaoHash, tipoSondagem);

        long inicio = System.nanoTime();
        for (String[] par : dados) {
            tabela.inserir(par[0], par[1]);
        }
        resultado.tempoInsercao = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (String[] par : dados) {
            tabela.buscar(par[0]);
        }
        resultado.tempoBusca = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (int i = 0; i < dados.size() / 2; i++) {
            tabela.remover(dados.get(i)[0]);
        }
        resultado.tempoRemocao = System.nanoTime() - inicio;

        resultado.colisoes = tabela.getColisoes();
        resultado.fatorCarga = tabela.getFatorCarga();
        resultado.capacidadeFinal = tabela.getCapacidade();

        return resultado;
    }

    public static ResultadoAnalise testarLinkedList(List<String[]> dados) {
        ResultadoAnalise resultado = new ResultadoAnalise(
            "LinkedList (Java Nativa)",
            "N/A"
        );
        LinkedList<String[]> lista = new LinkedList<>();

        long inicio = System.nanoTime();
        for (String[] par : dados) {
            lista.add(par);
        }
        resultado.tempoInsercao = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (String[] par : dados) {
            for (String[] item : lista) {
                if (item[0].equals(par[0])) {
                    break;
                }
            }
        }
        resultado.tempoBusca = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (int i = 0; i < dados.size() / 2; i++) {
            lista.remove(0);
        }
        resultado.tempoRemocao = System.nanoTime() - inicio;

        resultado.colisoes = 0;
        resultado.fatorCarga = 1.0;
        resultado.capacidadeFinal = lista.size();

        return resultado;
    }

    public static void exibirComparacao(List<ResultadoAnalise> resultados) {
        System.out.println("\n\n");
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║           📈 COMPARAÇÃO GERAL DE PERFORMANCE 📈                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        System.out.println("\n┌──────────────────────────────────────┬────────────┬────────────┬────────────┐");
        System.out.println("│ Estrutura                            │ Inserção   │ Busca      │ Remoção    │");
        System.out.println("│                                      │     (ms)   │     (ms)   │     (ms)   │");
        System.out.println("├──────────────────────────────────────┼────────────┼────────────┼────────────┤");

        for (ResultadoAnalise r : resultados) {
            String nomeAbrev = r.nomeEstrutura.length() > 35 ? 
                r.nomeEstrutura.substring(0, 35) : r.nomeEstrutura;
            System.out.printf("│ %-36s │ %10d │ %10d │ %10d │\n",
                nomeAbrev,
                r.tempoInsercao / 1_000_000,
                r.tempoBusca / 1_000_000,
                r.tempoRemocao / 1_000_000);
        }

        System.out.println("└──────────────────────────────────────┴────────────┴────────────┴────────────┘");

        System.out.println("\n📊 RANKING (Melhor Performance):");
        resultados.stream()
            .sorted(Comparator.comparingLong(r -> r.tempoInsercao))
            .forEach(r -> System.out.println("   ✓ " + r.nomeEstrutura));
    }
}
