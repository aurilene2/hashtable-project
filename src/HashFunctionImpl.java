package com.mycompany.hashtable;

/**
 * Implementação de 3 diferentes funções de hash
 * 1. Método da Multiplicação
 * 2. Algoritmo DJB2
 * 3. Algoritmo MurmurHash
 */
public class HashFunctionImpl {

    /**
     * Método da Multiplicação
     * Usa a constante áurea (0.6180339887)
     * Fórmula: h(k) = ⌊m * (k*A - ⌊k*A⌋)⌋
     * Onde A = (√5 - 1) / 2 ≈ 0.6180339887
     */
    public static class MetodoMultiplicacao implements HashFunction {
        private static final double A = 0.6180339887;

        @Override
        public int hash(String chave, int capacidade) {
            int h = chave.hashCode();
            double valor = Math.abs(h) * A;
            return (int) ((valor - Math.floor(valor)) * capacidade);
        }

        @Override
        public String getNome() {
            return "Método da Multiplicação";
        }
    }

    /**
     * Algoritmo DJB2 (Daniel J. Bernstein)
     * Hash clássico, simples e eficaz para strings
     * Fórmula: h = 5381; para cada char: h = ((h << 5) + h) + c
     */
    public static class DJB2 implements HashFunction {
        @Override
        public int hash(String chave, int capacidade) {
            long hash = 5381;
            for (char c : chave.toCharArray()) {
                hash = ((hash << 5) + hash) + c;
            }
            return (int) Math.abs(hash % capacidade);
        }

        @Override
        public String getNome() {
            return "DJB2";
        }
    }

    /**
     * Algoritmo MurmurHash (versão simplificada)
     * Hash robusto com excelente distribuição
     */
    public static class MurmurHash implements HashFunction {
        @Override
        public int hash(String chave, int capacidade) {
            long h = 0;
            for (byte b : chave.getBytes()) {
                h = h * 31 + b;
            }
            h = h ^ (h >> 16);
            return (int) Math.abs(h % capacidade);
        }

        @Override
        public String getNome() {
            return "MurmurHash";
        }
    }
}
