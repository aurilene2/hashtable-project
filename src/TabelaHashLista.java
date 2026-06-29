package com.mycompany.hashtable;

/**
 * Tabela Hash com Encadeamento Separado (Separate Chaining)
 */
public class TabelaHashLista {

    private static class Node {
        String chave;
        String valor;
        Node proximo;

        public Node(String chave, String valor) {
            this.chave = chave;
            this.valor = valor;
            this.proximo = null;
        }
    }

    private Node[] tabela;
    private int capacidade;
    private int tamanho;
    private HashFunction funcaoHash;
    private int colisoes = 0;

    public TabelaHashLista(int capacidade, HashFunction funcaoHash) {
        this.capacidade = capacidade;
        this.tabela = new Node[capacidade];
        this.tamanho = 0;
        this.funcaoHash = funcaoHash;
    }

    public double getFatorCarga() {
        return (double) tamanho / capacidade;
    }

    private void verificarRehash() {
        if (getFatorCarga() >= 0.75) {
            rehash();
        }
    }

    private void rehash() {
        Node[] tabelaAntiga = tabela;
        int capacidadeAntiga = capacidade;
        
        this.capacidade = capacidadeAntiga * 2;
        this.tabela = new Node[capacidade];
        this.tamanho = 0;
        this.colisoes = 0;

        for (int i = 0; i < capacidadeAntiga; i++) {
            Node noAtual = tabelaAntiga[i];
            while (noAtual != null) {
                inserir(noAtual.chave, noAtual.valor);
                noAtual = noAtual.proximo;
            }
        }

        System.out.println("⚡ Rehash realizado! Nova capacidade: " + capacidade);
    }

    public void inserir(String chave, String valor) {
        int indice = funcaoHash.hash(chave, capacidade);
        Node noAtual = tabela[indice];

        if (noAtual == null) {
            tabela[indice] = new Node(chave, valor);
            tamanho++;
            verificarRehash();
            return;
        }

        while (noAtual != null) {
            if (noAtual.chave.equals(chave)) {
                noAtual.valor = valor;
                return;
            }
            if (noAtual.proximo == null) {
                break;
            }
            noAtual = noAtual.proximo;
        }

        colisoes++;
        noAtual.proximo = new Node(chave, valor);
        tamanho++;
        verificarRehash();
    }

    public String buscar(String chave) {
        int indice = funcaoHash.hash(chave, capacidade);
        Node noAtual = tabela[indice];

        while (noAtual != null) {
            if (noAtual.chave.equals(chave)) {
                return noAtual.valor;
            }
            noAtual = noAtual.proximo;
        }
        return null;
    }

    public boolean remover(String chave) {
        int indice = funcaoHash.hash(chave, capacidade);
        Node noAtual = tabela[indice];
        Node noAnterior = null;

        while (noAtual != null) {
            if (noAtual.chave.equals(chave)) {
                if (noAnterior == null) {
                    tabela[indice] = noAtual.proximo;
                } else {
                    noAnterior.proximo = noAtual.proximo;
                }
                tamanho--;
                return true;
            }
            noAnterior = noAtual;
            noAtual = noAtual.proximo;
        }
        return false;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getColisoes() {
        return colisoes;
    }
}
