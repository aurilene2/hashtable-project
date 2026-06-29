package com.mycompany.hashtable;

import java.io.*;
import java.util.*;

/**
 * Programa Principal
 * Testes e análise de tabelas hash com dicionário em inglês
 */
public class Main {

    /**
     * Lê dados do arquivo CSV
     * Suporta formatos: word,definition ou apenas palavras
     */
    public static List<String[]> lerCSV(String caminhoArquivo) {
        List<String[]> dados = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeira = true;
            int linhasLidas = 0;
            
            while ((linha = br.readLine()) != null && linhasLidas < 50000) {
                if (linha.trim().isEmpty()) continue;
                
                // Pula cabeçalho
                if (primeira && (linha.toLowerCase().contains("word") || linha.toLowerCase().contains("definition"))) {
                    primeira = false;
                    continue;
                }
                primeira = false;
                
                String[] partes;
                
                if (linha.contains(",")) {
                    partes = linha.split(",", 2);
                } else if (linha.contains("\t")) {
                    partes = linha.split("\t", 2);
                } else {
                    partes = new String[]{linha.trim(), "word"};
                }
                
                if (partes.length >= 1 && !partes[0].trim().isEmpty()) {
                    String palavra = partes[0].trim();
                    String significado = partes.length > 1 ? partes[1].trim() : "word";
                    
                    if (palavra.length() > 1) {
                        dados.add(new String[]{palavra, significado});
                        linhasLidas++;
                    }
                }
            }
            
            System.out.println("✓ Carregados " + linhasLidas + " palavras do dicionário\n");
            
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Arquivo não encontrado: " + caminhoArquivo);
            System.out.println("Usando dados de exemplo...\n");
            dados = gerarDadosExemplo();
        } catch (IOException e) {
            System.out.println("⚠️ Erro ao ler arquivo: " + e.getMessage());
            System.out.println("Usando dados de exemplo...\n");
            dados = gerarDadosExemplo();
        }
        
        return dados;
    }

    /**
     * Gera dados de exemplo em inglês
     */
    public static List<String[]> gerarDadosExemplo() {
        List<String[]> dados = new ArrayList<>();
        
        String[][] exemplos = {
            {"hello", "a polite greeting"},
            {"world", "the planet Earth"},
            {"computer", "an electronic device"},
            {"programming", "writing computer code"},
            {"algorithm", "a step-by-step procedure"},
            {"hash", "function mapping data to fixed size"},
            {"table", "data structure with rows and columns"},
            {"java", "programming language"},
            {"data", "facts and statistics"},
            {"structure", "arrangement of elements"},
            {"search", "look for something"},
            {"insert", "put something in"},
            {"delete", "remove something"},
            {"array", "ordered collection of elements"},
            {"linked", "connected or joined"},
            {"list", "sequence of items"},
            {"collision", "two keys hash to same value"},
            {"bucket", "container in a hash table"},
            {"chain", "sequence linked together"},
            {"performance", "how well something works"},
            {"memory", "storage for data"},
            {"efficient", "working in a well-organized way"},
            {"speed", "how fast something moves"},
            {"space", "available room or area"},
            {"time", "indefinite progression of events"}
        };
        
        for (String[] par : exemplos) {
            dados.add(par);
        }
        
        System.out.println("✓ Carregados " + exemplos.length + " palavras de exemplo\n");
        return dados;
    }

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║    🔍 ANÁLISE DE TABELAS HASH - DICIONÁRIO EM INGLÊS 🔍            ║");
        System.out.println("║                    PROJETO FINAL - HASH TABLES                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");

        String caminhoArquivo = args.length > 0 ? args[0] : "english_dictionary.csv";
        List<String[]> dados = lerCSV(caminhoArquivo);

        if (dados.isEmpty()) {
            System.out.println("❌ Nenhum dado foi carregado!");
            return;
        }

        System.out.println("📊 Total de registros: " + dados.size() + "\n");

        List<AnalisadorHash.ResultadoAnalise> resultados = new ArrayList<>();
        int capacidadeInicial = Math.max(16, dados.size() / 4);
        System.out.println("Capacidade inicial da tabela: " + capacidadeInicial + "\n");

        // ===== TESTE 1: Encadeamento Separado =====
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("TESTE 1️⃣ : ENCADEAMENTO SEPARADO (Separate Chaining)");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");

        System.out.println("1.1️⃣ Método da Multiplicação");
        resultados.add(AnalisadorHash.testarTabelaHashLista(
            dados,
            new HashFunctionImpl.MetodoMultiplicacao(),
            capacidadeInicial
        ));
        resultados.get(resultados.size() - 1).exibir();

        System.out.println("\n1.2️⃣ Algoritmo DJB2");
        resultados.add(AnalisadorHash.testarTabelaHashLista(
            dados,
            new HashFunctionImpl.DJB2(),
            capacidadeInicial
        ));
        resultados.get(resultados.size() - 1).exibir();

        System.out.println("\n1.3️⃣ Algoritmo MurmurHash");
        resultados.add(AnalisadorHash.testarTabelaHashLista(
            dados,
            new HashFunctionImpl.MurmurHash(),
            capacidadeInicial
        ));
        resultados.get(resultados.size() - 1).exibir();

        // ===== TESTE 2: Endereçamento Aberto =====
        System.out.println("\n\n═══════════════════════════════════════════════════════════════════════");
        System.out.println("TESTE 2️⃣ : ENDEREÇAMENTO ABERTO (Open Addressing)");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");

        System.out.println("2.1️⃣ Sondagem Linear (DJB2)");
        resultados.add(AnalisadorHash.testarTabelaHashAberta(
            dados,
            new HashFunctionImpl.DJB2(),
            TabelaHashAberta.TipoSondagem.LINEAR,
            capacidadeInicial
        ));
        resultados.get(resultados.size() - 1).exibir();

        System.out.println("\n2.2️⃣ Sondagem Quadrática (DJB2)");
        resultados.add(AnalisadorHash.testarTabelaHashAberta(
            dados,
            new HashFunctionImpl.DJB2(),
            TabelaHashAberta.TipoSondagem.QUADRATICA,
            capacidadeInicial
        ));
        resultados.get(resultados.size() - 1).exibir();

        // ===== TESTE 3: LinkedList =====
        System.out.println("\n\n═══════════════════════════════════════════════════════════════════════");
        System.out.println("TESTE 3️⃣ : LINKEDLIST (Comparação com estrutura nativa)");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");

        System.out.println("3.1️⃣ LinkedList Nativa do Java");
        resultados.add(AnalisadorHash.testarLinkedList(dados));
        resultados.get(resultados.size() - 1).exibir();

        // Exibição final
        AnalisadorHash.exibirComparacao(resultados);
        exibirAnaliseTeórica(dados.size(), capacidadeInicial);

        System.out.println("\n\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  ✅ ANÁLISE CONCLUÍDA COM SUCESSO ✅              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
    }

    public static void exibirAnaliseTeórica(int totalDados, int capacidadeInicial) {
        System.out.println("\n\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              📚 ANÁLISE TEÓRICA - FATOR DE CARGA 📚                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");

        double fatorInicial = (double) totalDados / capacidadeInicial;
        
        System.out.println("🔹 FATOR DE CARGA (α = n/m)");
        System.out.println("   n = " + totalDados + " (elementos)");
        System.out.println("   m = " + capacidadeInicial + " (capacidade)");
        System.out.println("   α = " + String.format("%.2f", fatorInicial) + "\n");

        System.out.println("📊 ANÁLISE POR FATOR DE CARGA:\n");
        System.out.println("✅ α = 0.5: Poucas colisões, O(1.5), mais espaço");
        System.out.println("⚠️  α = 0.9: Muitas colisões, O(1.9), menos espaço");
        System.out.println("🔗 ENCADEAMENTO: α > 1 permitido, O(1 + α)");
        System.out.println("📍 ENDEREÇAMENTO: α < 1 obrigatório (sem rehashing)");
    }
}
