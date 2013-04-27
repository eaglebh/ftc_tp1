package tp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App {

    public static void main(String[] args) {
        Set<Set<Estado>> resultadoParcial;
        Automato automatoEntrada = leAutomatoDoArquivo();

        // elimina todos os estados inatingiveis
        automatoEntrada.retiraEstadosInatingiveis();
        automatoEntrada.minimizaEstados();
    }


    public static Automato leAutomatoDoArquivo() {
        Automato novoAutomato = new Automato();
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Pablo\\Google Drive\\ufmg\\ftc\\tp1\\target\\classes\\caso1.txt"));
            String linha = br.readLine();
            int quantidadeEstados = preencheEstados(novoAutomato, linha);

            linha = br.readLine();
            preencheAlfabeto(novoAutomato, linha);

            for (int i = 0; i < quantidadeEstados; i++) {
                linha = br.readLine();
                preencheTransicoes(novoAutomato, linha, i + 1);
            }
            linha = br.readLine();
            preencheEstadoInicial(novoAutomato, linha);

            linha = br.readLine();
            preencheEstadosFinais(novoAutomato, linha);
            System.out.println(linha);
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return novoAutomato;
    }

    private static void preencheEstadosFinais(Automato novoAutomato, String linha) {
        String[] estadosFinaisStr = linha.split(" ");
        Set<Estado> estadosFinais = new HashSet<Estado>();
        for (String estadoFinalStr : estadosFinaisStr) {
            if (estadoFinalStr.contains(";")) {
                break;
            }
            Estado estadoFinal = novoAutomato.getEstadoPeloNome(estadoFinalStr);
            estadosFinais.add(estadoFinal);
        }
        novoAutomato.setEstadosFinais(estadosFinais);
    }

    private static void preencheEstadoInicial(Automato novoAutomato, String linha) {
        Estado estadoInicial = novoAutomato.getEstadoPeloNome(linha.split(" ")[0]);
        novoAutomato.setEstadoInicial(estadoInicial);
    }

    public static int preencheEstados(Automato aut, String estados) {
        String[] arrayEstados = estados.split(" ");
        Set<Estado> conjuntoEstados = new HashSet<Estado>();
        for (String nome : arrayEstados) {
            if (nome.contains(";")) {
                break;
            }
            Estado novoEstado = Estado.criaEstado(nome);
            conjuntoEstados.add(novoEstado);
        }
        aut.setEstados(conjuntoEstados);
        return conjuntoEstados.size();
    }

    public static void preencheAlfabeto(Automato aut, String alfabeto) {
        String[] arraySimbolos = alfabeto.split(" ");
        List<Simbolo> conjuntoAlfabeto = new ArrayList<Simbolo>();
        for (String simbolo : arraySimbolos) {
            if (simbolo.contains(";")) {
                break;
            }
            Simbolo novoSimbolo = new Simbolo(simbolo);
            conjuntoAlfabeto.add(novoSimbolo);
        }
        aut.setAlfabeto(conjuntoAlfabeto);
    }

    public static void preencheTransicoes(Automato aut, String linha, int numEstado) {
        Estado estado = aut.getEstadoPeloNome(String.valueOf(numEstado));

        String[] estadosFinais = linha.split(" ");

        for (int count = 0; count < aut.getAlfabeto().size(); count++) {
            Estado estadoFinal = aut.getEstadoPeloNome(estadosFinais[count]);
            Simbolo simbolo1 = aut.getAlfabeto().get(count);
            Transicao transicao = new Transicao(estado, simbolo1);
            aut.getFuncoesTransicao().put(transicao, estadoFinal);
        }
    }

}
