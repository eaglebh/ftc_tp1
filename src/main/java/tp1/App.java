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
        Automato automatoSaida = automatoEntrada.minimizaEstados();

        System.out.print(automatoSaida.toText());
    }

    public static Automato leAutomatoDoArquivo() {
        Automato novoAutomato = new Automato();
        //Estado.reiniciarChave();
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Pablo\\Google Drive\\ufmg\\ftc\\tp1\\target\\classes\\teste3.txt"));
            String linha = br.readLine();
            int quantidadeEstados = preencheEstados(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheAlfabeto(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            for (int i = 0; i < quantidadeEstados; i++) {
                preencheTransicoes(novoAutomato, linha, i);
                linha = br.readLine();
            }

            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheEstadoInicial(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheEstadosFinais(novoAutomato, linha);
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return novoAutomato;
    }

    private static void preencheEstadosFinais(Automato novoAutomato, String linha) {
        String[] estadosFinaisStr = linha.split(" ");
        List<Estado> estadosFinais = new ArrayList<Estado>();
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
        List<Estado> conjuntoEstados = new ArrayList<Estado>();
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
        Estado estado = aut.getEstadoPelaOrdem(numEstado);

        String[] estadosFinais = linha.split(" ");

        for (int count = 0; count < aut.getAlfabeto().size(); count++) {
            Estado estadoFinal = aut.getEstadoPeloNome(estadosFinais[count]);
            Simbolo simbolo1 = aut.getAlfabeto().get(count);
            Transicao transicao = new Transicao(estado, simbolo1);
            aut.getFuncoesTransicao().put(transicao, estadoFinal);
        }
    }

}
