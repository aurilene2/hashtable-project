package com.mycompany.hashtable;

/**
 * Interface para diferentes funções de hash
 */
public interface HashFunction {
    int hash(String chave, int capacidade);
    String getNome();
}
