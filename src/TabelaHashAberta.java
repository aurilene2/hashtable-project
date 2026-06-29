package com.mycompany.hashtable;

/**
 * Tabela Hash com Endereçamento Aberto (Open Addressing)
 * 
 * ESTRATÉGIA DE COLISÃO:
 * - Todos os elementos estão no próprio array
 * - Quando há colisão, procura a próxima posição vazia
 * - Suporta dois tipos de sondagem: Linear e Quadrática
 * 
 * VANTAGENS:
 * - Melhor localidade de cache
 * - Menos memória (sem ponteiros)
 * - Mais rápido em sistemas modernos
 * 
 * DESVANTAGENS:
 * - Requer α < 1 (sem rehashing)
 * - Remoção é complexa (marca como deletado)
 * - Clustering (aglomeração) de elementos
 */
public class TabelaHashAberta {

    private static class Entry {
        String chave;
        String valor;
        boolean deletado;

        public Entry(String chave, String valor) {
            this.chave = chave;
            this.valor = valor;
            this.deletado = false;
        }
    }

    public enum TipoSondagem {
        LINEAR,      // h(k, i) = (h(k) + i) mod m
        QUADRATICA   // h(k, i) = (h(k) + i² + i) mod m
    }

    private Entry[] tabela;
    private int capacidade;
    private int tamanho;
    private HashFunction funcaoHash;
    private TipoSondagem tipoSondagem;
    private int colisoes = 0;

    public TabelaHashAberta(int capacidade, HashFunction funcaoHash, TipoSondagem tipoSondagem) {
        this.capacidade = capacidade;
        this.tabela = new Entry[capacidade];
        this.tamanho = 0;
        this.funcaoHash = funcaoHash;
        this.tipoSondagem = tipoSondagem;
    }

    /**
     * Calcula o próximo índice baseado no tipo de sondagem
     * 
     * Sondagem Linear: h(k, i) = (h(k) + i) mod m
     *   - Simples
     *   - Rápido
     *   - Mas causa clustering primário
     * 
     * Sondagem Quadrática: h(k, i) = (h(k) + i² + i) mod m
     *   - Evita clustering primário
     *   - Reduz clustering secundário
     */
    private int sondagem(int indiceInicial, int tentativa) {
        if (tipoSondagem == TipoSondagem.LINEAR) {
            return (indiceInicial + tentativa) % capacidade;
        } else {
            return (indiceInicial + tentativa * tentativa + tentativa) % capacidade;
        }
    }

    /**
     * Calcula o fator de carga α = n/m
     */
    public double getFatorCarga() {
        return (double) tamanho / capacidade;
    }

    /**
     * Verifica e realiza rehashing se necessário
     */
    private void verificarRehash() {
        if (getFatorCarga() >= 0.75) {
            rehash();
        }
    }

    /**
     * Rehashing: dobra a capacidade e remapeia elementos
     */
    private void rehash() {
        Entry[] tabelaAntiga = tabela;
        int capacidadeAntiga = capacidade;

        this.capacidade = capacidadeAntiga * 2;
        this.tabela = new Entry[capacidade];
        this.tamanho = 0;
        this.colisoes = 0;

        for (int i = 0; i < capacidadeAntiga; i++) {
            if (tabelaAntiga[i] != null && !tabelaAntiga[i].deletado) {
                inserir(tabelaAntiga[i].chave, tabelaAntiga[i].valor);
            }
        }

        System.out.println("⚡ Rehash realizado! Nova capacidade: " + capacidade);
    }

    /**
     * Insere um par chave-valor
     * Tempo: O(1) em média (sem clustering severo)
     */
    public void inserir(String chave, String valor) {
        if (getFatorCarga() >= 0.75) {
            rehash();
        }

        int indiceInicial = funcaoHash.hash(chave, capacidade);
        int tentativa = 0;

        while (tentativa < capacidade) {
            int indice = sondagem(indiceInicial, tentativa);

            // Posição vazia ou deletada
            if (tabela[indice] == null || tabela[indice].deletado) {
                tabela[indice] = new Entry(chave, valor);
                tamanho++;
                return;
            }

            // Chave já existe
            if (tabela[indice].chave.equals(chave)) {
                tabela[indice].valor = valor;
                return;
            }

            // COLISÃO! Continua sondando
            colisoes++;
            tentativa++;
        }

        System.out.println("⚠️ Tabela cheia! Não foi possível inserir: " + chave);
    }

    /**
     * Busca um valor pela chave
     * Tempo: O(1) em média
     */
    public String buscar(String chave) {
        int indiceInicial = funcaoHash.hash(chave, capacidade);
        int tentativa = 0;

        while (tentativa < capacidade) {
            int indice = sondagem(indiceInicial, tentativa);

            if (tabela[indice] == null) {
                return null;
            }

            if (!tabela[indice].deletado && tabela[indice].chave.equals(chave)) {
                return tabela[indice].valor;
            }

            tentativa++;
        }
        return null;
    }

    /**
     * Remove um elemento pela chave
     * Nota: marca como deletado (não remove fisicamente)
     */
    public boolean remover(String chave) {
        int indiceInicial = funcaoHash.hash(chave, capacidade);
        int tentativa = 0;

        while (tentativa < capacidade) {
            int indice = sondagem(indiceInicial, tentativa);

            if (tabela[indice] == null) {
                return false;
            }

            if (!tabela[indice].deletado && tabela[indice].chave.equals(chave)) {
                tabela[indice].deletado = true;
                tamanho--;
                return true;
            }

            tentativa++;
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

    /**
     * Exibe a estrutura da tabela
     */
    public void imprimirTabela() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║      TABELA HASH - ENDEREÇAMENTO ABERTO (Open Addressing)         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("Função Hash: " + funcaoHash.getNome());
        System.out.println("Tipo Sondagem: " + tipoSondagem);
        System.out.println("Capacidade: " + capacidade + " | Tamanho: " + tamanho);
        System.out.println("Fator de Carga: " + String.format("%.2f", getFatorCarga()));
        System.out.println("Colisões: " + colisoes);
        System.out.println("────────────────────────────────────────────────────────────────────");

        for (int i = 0; i < capacidade && i < 20; i++) {
            System.out.print("[" + i + "]: ");
            if (tabela[i] == null) {
                System.out.println("NULL");
            } else if (tabela[i].deletado) {
                System.out.println("[DELETADO]");
            } else {
                System.out.println(tabela[i].chave + "=" + tabela[i].valor);
            }
        }
        System.out.println("════════════════════════════════════════════════════════════════════\n");
    }
}
