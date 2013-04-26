package tp1;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.io.*;

/**
 * Entrada: um AFD (E; ; ; i; F ).
 * Saída: AFD (E 0   ; ;     0       ; i   0          ; F 0    ).
 * conjunto de conjuntos de estados : R; S;
 * conjunto de estados : C; Y;X ;
 * estado : e; d;
 * elimine todo estado alcançável a partir do estado inicial i;
 * R := fE   F; F g   ffgg;
 * repita
 * S := R; R := fg;
 * para cada C 2 S faça
 * repita
 * escolha e de C ;
 * Y := feg;
 * L: para cada d 2 C   feg faça
 * para cada a 2  faça
 * seja X 2 S tal que (e; a) 2 X ;
 * se (d; a) 62 X então continue L fimse
 * fimpara;
 * Y := Y [ fdg
 * fimpara;
 * C := C   Y ;
 * R := R [ fY g;
 * até C = fg
 * fimpara
 * até R = S ;
 * E
 * 0
 * := S ;
 * <p/>
 * 0
 * : para todos C 2 S e a 2 :
 * <p/>
 * 0
 * (C; a) = conjunto em S que contém (e; a) para algum a 2 C
 * i
 * 0
 * := conjunto em S que contém i;
 * F
 * 0
 * := fC 2 S j C  F g;
 */

public class App {

    public static void main(String[] args) {
        Set<Set<Estado>> resultadoParcial;
        Set<Set<Estado>> conjuntoMinimizavel;
        Automato automatoEntrada = new Automato();

        // elimina todos os estados inatingiveis
        automatoEntrada.retiraEstadosInatingiveis();

        // cria S0 com dois conjuntos : iniciais e finais

        // se Sn != S(n-1) então
        // para cada conjunto A de Sn faça
        // se o conjunto tem mais de um estado então, para cada estado e do conjunto A faça
        // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
        // se for, entao permanece no estado A
        // senao, deve fazer parte de outro conjunto novo
        // se o
    }

    public Automato leAutomatoDoArquivo() {
        Automato novoAutomato = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("c:/bla.txt"));
            String linha = br.readLine();
            int quantidadeEstados = preencheEstados(novoAutomato, linha);

            linha = br.readLine();
            preencheAlfabeto(novoAutomato, linha);

            for (int i = 1; i < quantidadeEstados; i++) {
                linha = br.readLine();
                preencheTransicao(novoAutomato, linha, i);
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

    public int preencheEstados(Automato aut, String estados) {
        String[] arrayEstados = estados.split(" ");
        Set conjuntoEstados = new HashSet();
        for (String estado : arrayEstados) {
            Estado novoEstado = new Estado(estado);
            conjuntoEstados.add(novoEstado);
        }
        aut.setEstados(conjuntoEstados);
        return arrayEstados.length;
    }

    public void preencheAlfabeto(Automato aut, String alfabeto) {
        String[] arraySimbolos = alfabeto.split(" ");
        Set conjuntoAlfabeto = new HashSet();
        for (String simbolo : arraySimbolos) {
            Simbolo novoSimbolo = new Simbolo(simbolo);
            conjuntoAlfabeto.add(novoSimbolo);
        }
        aut.setAlfabeto(conjuntoAlfabeto);
    }

    public void preencheTransicao(Automato aut, String linha, int numEstado) {
        Estado estado = getEstadoPeloNome(aut, String.valueOf(numEstado));

        String[] estadosFinais = linha.split(" ");

        Estado estadoFinal1 = getEstadoPeloNome(aut, estadosFinais[0]);
        Simbolo simbolo1 = getSimboloPeloNome(aut, "0");
        Transicao transicao1 = new Transicao(estado, simbolo1);

        Estado estadoFinal2 = getEstadoPeloNome(aut, estadosFinais[1]);
        Simbolo simbolo2 = getSimboloPeloNome(aut, "1");
        Transicao transicao2 = new Transicao(estado, simbolo2);
    }

    public Estado getEstadoPeloNome(Automato aut, String nomeEstado) {
        Estado estado = null;
        for (Estado est : aut.getEstados()) {
            if (est.getNome().equals(nomeEstado)) {
                estado = est;
                break;
            }
        }
        return estado;
    }

    public Simbolo getSimboloPeloNome(Automato aut, String nomeSimbolo) {
        Simbolo simbolo = null;

        for (Simbolo s : aut.getAlfabeto()) {
            if (s.getNome().equals(nomeSimbolo)) {
                simbolo = s;
                break;
            }
        }
        return simbolo;
    }
}
