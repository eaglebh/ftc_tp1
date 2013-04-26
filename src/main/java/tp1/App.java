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
        Set<Set<Estado>> conjuntoMinimizavel = null;
        Automato automatoEntrada = leAutomatoDoArquivo();

        // elimina todos os estados inatingiveis
        automatoEntrada.retiraEstadosInatingiveis();
        minimizaEstados(conjuntoMinimizavel, automatoEntrada);
    }

    private static Automato minimizaEstados(Set<Set<Estado>> conjuntoMinimizavel, Automato automatoEntrada) {
        Set<Set<Estado>> resultadoParcial;// cria S0 com dois conjuntos : nao-finais e finais
        resultadoParcial = new HashSet<Set<Estado>>();
        Set<Estado> estadosNaoFinais = automatoEntrada.getEstados();
        estadosNaoFinais.removeAll(automatoEntrada.getEstadosFinais());
        resultadoParcial.add(estadosNaoFinais);
        resultadoParcial.add(automatoEntrada.getEstadosFinais());

        while (!resultadoParcial.equals(conjuntoMinimizavel)) {
            conjuntoMinimizavel = new HashSet<Set<Estado>>(resultadoParcial);
            resultadoParcial.clear();

            // para cada conjunto A de Sn faça
            for (Set<Estado> conjunto : conjuntoMinimizavel) {
                // repita ate C = {}
                // se o conjunto tem mais de um estado então, enquanto o conjunto tiver mais de um estado
                while (conjunto.size() > 1) {
                    // escolha e de C
                    Estado estadoEscolhido = conjunto.iterator().next();
                    // Y = {e}
                    // coloque e no agrupamento novo de estados
                    Set<Estado> estadosAgrupados = new HashSet<Estado>();
                    estadosAgrupados.add(estadoEscolhido);
                    // para cada d de C - {e}
                    Set<Estado> estadosRestantesConj = new HashSet<Estado>(conjunto);
                    estadosRestantesConj.remove(estadoEscolhido);
                    for (Estado estadoD : estadosRestantesConj) {
                        boolean agrupa = true;
                        for (Simbolo simbolo : automatoEntrada.getAlfabeto()) {
                            // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
                            Estado estadoAtingidoPorEscolhido = automatoEntrada.aplicaFuncaoTransicao(estadoEscolhido, simbolo);
                            Estado estadoAtingidoPorD = automatoEntrada.aplicaFuncaoTransicao(estadoD, simbolo);
                            if (!isEstadosMesmoConjunto(conjuntoMinimizavel, estadoAtingidoPorEscolhido, estadoAtingidoPorD)) {
                                // senao, deve fazer parte de outro conjunto novo
                                agrupa = false;
                                break;
                            }
                        }
                        if (agrupa) {
                            // Y = Y U {d}
                            estadosAgrupados.add(estadoD);
                        }
                    }
                    // C = C - Y
                    conjuntoMinimizavel.removeAll(estadosAgrupados);

                    // R = R U {Y}
                    Set<Estado> conjuntoAgrupado = new HashSet<Estado>();
                    conjuntoAgrupado.addAll(estadosAgrupados);
                    resultadoParcial.add(conjuntoAgrupado);
                }
            }
        }

        Automato automatoSaida = new Automato(automatoEntrada);
        Set<Estado> estadosSaida = new HashSet<Estado>();
        for (Set<Estado> estados : conjuntoMinimizavel) {
            Estado novoEstado = criaEstadoDeConjunto(estados);
            estadosSaida.add(novoEstado);
        }
        // E' = S
        automatoSaida.setEstados(estadosSaida);

        // i' = conjunto em S que contém i
        Estado novoEstadoInicial = encontraConjuntoDoEstado(conjuntoMinimizavel, automatoEntrada.getEstadoInicial());
        automatoSaida.setEstadoInicial(novoEstadoInicial);

        // F' =

        return automatoSaida;
    }

    private static Estado criaEstadoDeConjunto(Set<Estado> estados) {
        StringBuilder novoNome = new StringBuilder();
        for (Estado estado : estados) {
            // vai concatenar com outro nome de estado anterior
            if (novoNome.length() != 0) {
                novoNome.append('_');
            }
            novoNome.append(estado.getNome());
        }
        return new Estado(novoNome.toString());
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
                preencheTransicoes(novoAutomato, linha, i+1);
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
        for(String estadoFinalStr : estadosFinaisStr) {
            Estado estadoFinal = getEstadoPeloNome(novoAutomato, estadoFinalStr);
            estadosFinais.add(estadoFinal);
        }
        novoAutomato.setEstadosFinais(estadosFinais);
    }

    private static void preencheEstadoInicial(Automato novoAutomato, String linha) {
        Estado estadoInicial = getEstadoPeloNome(novoAutomato, linha.split(" ")[0]);
        novoAutomato.setEstadoInicial(estadoInicial);
    }

    private static Estado encontraConjuntoDoEstado(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoInicial) {
        for (Set<Estado> estados : conjuntoMinimizavel) {
            if (estados.contains(estadoInicial)) {
                return criaEstadoDeConjunto(estados);
            }
        }
        return null;
    }

    private static boolean isEstadosMesmoConjunto(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoAtingidoPorEscolhido, Estado estadoAtingidoPorD) {
        Set<Estado> grupoEstados = new HashSet<Estado>();
        grupoEstados.add(estadoAtingidoPorEscolhido);
        grupoEstados.add(estadoAtingidoPorD);
        for (Set<Estado> estados : conjuntoMinimizavel) {
            if (estados.containsAll(grupoEstados)) {
                return true;
            }
        }
        return false;
    }

    public static int preencheEstados(Automato aut, String estados) {
        String[] arrayEstados = estados.split(" ");
        Set conjuntoEstados = new HashSet();
        for (String estado : arrayEstados) {
            if (estado.contains(";")) {
                break;
            }
            Estado novoEstado = new Estado(estado);
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
        Estado estado = getEstadoPeloNome(aut, String.valueOf(numEstado));

        String[] estadosFinais = linha.split(" ");

        for(int count = 0; count < aut.getAlfabeto().size(); count++){
            Estado estadoFinal = getEstadoPeloNome(aut, estadosFinais[count]);
            Simbolo simbolo1 = aut.getAlfabeto().get(count);
            Transicao transicao = new Transicao(estado, simbolo1);
            aut.getFuncoesTransicao().put(transicao, estadoFinal);
        }
    }

    public static Estado getEstadoPeloNome(Automato aut, String nomeEstado) {
        Estado estado = null;
        for (Estado est : aut.getEstados()) {
            if (est.getNome().equals(nomeEstado)) {
                estado = est;
                break;
            }
        }
        return estado;
    }

    public static Simbolo getSimboloPeloNome(Automato aut, String nomeSimbolo) {
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
