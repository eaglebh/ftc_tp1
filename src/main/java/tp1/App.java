package tp1;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.io.*;

public class App {

    public static void main(String[] args) {
        Set<Set<Estado>> resultadoParcial;
        Set<Set<Estado>> conjuntoMinimizavel = null;
        Automato automatoEntrada = new Automato();

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
                        for (Simbolo simbolo : automatoEntrada.getAlfabeto()){
                            // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
                            Estado estadoAtingidoPorEscolhido = automatoEntrada.aplicaFuncaoTransicao(estadoEscolhido, simbolo);
                            Estado estadoAtingidoPorD = automatoEntrada.aplicaFuncaoTransicao(estadoD, simbolo);
                            if(!isEstadosMesmoConjunto(conjuntoMinimizavel, estadoAtingidoPorEscolhido, estadoAtingidoPorD)){
                                // senao, deve fazer parte de outro conjunto novo
                                agrupa = false;
                                break;
                            }
                        }
                        if(agrupa){
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
        for(Set<Estado> estados : conjuntoMinimizavel) {
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
        for(Estado estado : estados) {
            // vai concatenar com outro nome de estado anterior
            if(novoNome.length() != 0){
                novoNome.append('_');
            }
            novoNome.append(estado.getNome());
        }
        return new Estado(novoNome.toString());
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

    private void preencheEstadosFinais(Automato novoAutomato, String linha) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void preencheEstadoInicial(Automato novoAutomato, String linha) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private static Estado encontraConjuntoDoEstado(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoInicial) {
        for(Set<Estado> estados : conjuntoMinimizavel){
            if(estados.contains(estadoInicial)){
                return criaEstadoDeConjunto(estados);
            }
        }
        return null;
    }

    private static boolean isEstadosMesmoConjunto(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoAtingidoPorEscolhido, Estado estadoAtingidoPorD) {
        Set<Estado> grupoEstados = new HashSet<Estado>();
        grupoEstados.add(estadoAtingidoPorEscolhido);
        grupoEstados.add(estadoAtingidoPorD);
        for(Set<Estado> estados : conjuntoMinimizavel) {
            if(estados.containsAll(grupoEstados)){
                return true;
            }
        }
        return false;
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
