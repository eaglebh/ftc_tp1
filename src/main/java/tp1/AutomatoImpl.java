package tp1;

import java.util.*;
import java.util.logging.Logger;

public class AutomatoImpl implements Automato {
    private static final Logger LOGGER = Logger.getLogger("FTC-Tp1");
    private List<Estado> estados;
    private List<Estado> estadosFinais;
    private List<Simbolo> alfabeto;
    private Estado estadoInicial;
    private Map<Transicao, Estado> funcoesTransicao;

    public AutomatoImpl() {
        this.funcoesTransicao = new HashMap<Transicao, Estado>();
    }

    public void preencheTransicoesParaEstado(String linha, Estado estadoOrigem) {
        String[] estadosDestino = linha.replace(';', ' ').trim().split(" ");

        for (int count = 0; count < this.alfabeto.size(); count++) {
            Estado estadoFinal = recuperaEstadoPeloNome(estadosDestino[count]);
            Simbolo simbolo = this.alfabeto.get(count);
            Transicao transicao = new TransicaoImpl(estadoOrigem, simbolo);
            this.funcoesTransicao.put(transicao, estadoFinal);
        }
    }

    private Estado extraiEstadoInicialDeEstados(Iterable<Estado> conjuntoMinimizavel, Estado estadoInicial) {
        for (Estado estadoAgrupado : conjuntoMinimizavel) {
            if (estadoAgrupado.contains(estadoInicial)) {
                return estadoAgrupado;
            }
        }
        return null;
    }

    public Automato criaEquivalenteMinimizado() {
        EstadoImpl.reiniciarChave();
        SimboloImpl.reiniciarChave();
        // elimina todos os estados inatingiveis
        this.retiraEstadosInatingiveis();

        List<List<Estado>> conjuntoMinimizavel = null;
        List<List<Estado>> resultadoParcial;// cria S0 com dois conjuntos : nao-finais e finais
        resultadoParcial = new ArrayList<List<Estado>>();
        List<Estado> estadosNaoFinais = new ArrayList<Estado>(this.estados);
        estadosNaoFinais.removeAll(this.estadosFinais);
        resultadoParcial.add(estadosNaoFinais);
        resultadoParcial.add(this.estadosFinais);

        int contadorIteracoes = 0;
        while (!resultadoParcial.equals(conjuntoMinimizavel)) {
            conjuntoMinimizavel = new ArrayList<List<Estado>>(resultadoParcial);
            imprimeConjuntoMinimizavel(conjuntoMinimizavel, contadorIteracoes);
            resultadoParcial.clear();

            // para cada conjunto A de Sn faça
            for (List<Estado> conjunto : conjuntoMinimizavel) {
                List<Estado> subConjunto = new ArrayList<Estado>(conjunto);
                // repita ate C = {}
                // se o conjunto tem mais de um estado então, enquanto o conjunto tiver mais de um estado
                while (subConjunto.size() > 0) {
                    // escolha e de C
                    Estado estadoEscolhido = subConjunto.iterator().next();
                    // Y = {e}
                    // coloque e no agrupamento novo de estados
                    List<Estado> estadosAgrupados = new ArrayList<Estado>();
                    estadosAgrupados.add(estadoEscolhido);
                    // para cada d de C - {e}
                    List<Estado> estadosRestantesConj = new ArrayList<Estado>(subConjunto);
                    estadosRestantesConj.remove(estadoEscolhido);
                    for (Estado estadoD : estadosRestantesConj) {
                        boolean agrupa = true;
                        for (Simbolo simbolo : this.alfabeto) {
                            // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
                            Estado estadoAtingidoPorEscolhido = this.aplicaFuncaoTransicao(estadoEscolhido, simbolo);
                            Estado estadoAtingidoPorD = this.aplicaFuncaoTransicao(estadoD, simbolo);
                            if (isEstadosConjuntosDiferentes(conjuntoMinimizavel, estadoAtingidoPorEscolhido, estadoAtingidoPorD)) {
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
                    subConjunto.removeAll(estadosAgrupados);

                    // R = R U {Y}
                    List<Estado> conjuntoAgrupado = new ArrayList<Estado>();
                    conjuntoAgrupado.addAll(estadosAgrupados);
                    resultadoParcial.add(conjuntoAgrupado);
                }
            }
            ++contadorIteracoes;
        }
        imprimeConjuntoMinimizavel(conjuntoMinimizavel, contadorIteracoes);

        List<Estado> estadosSaida = criaEstadosDeConjuntos(conjuntoMinimizavel);
        List<Estado> estadosFinaisSaida = extraiEstadosFinaisDeEstados(estadosSaida);
        Map<Transicao, Estado> funcoesTransicaoSaida = extraiTransicoes(estadosSaida);

        Automato automatoSaida = new AutomatoImpl();
        automatoSaida.setAlfabeto(this.alfabeto);
        // E' = S
        automatoSaida.setEstados(estadosSaida);
        // i' = conjunto em S que contém i
        Estado novoEstadoInicial = this.extraiEstadoInicialDeEstados(estadosSaida, this.estadoInicial);
        automatoSaida.setEstadoInicial(novoEstadoInicial);
        // F' = {C E S|C conj-prop F}
        automatoSaida.setEstadosFinais(estadosFinaisSaida);
        // ft' : para todos C E S e E Alfabeto :
        // ft'(C, a) = conjunto em S que contém ft(e, a) para algum e E C
        automatoSaida.setFuncoesTransicao(funcoesTransicaoSaida);

        return automatoSaida;
    }

    private void imprimeConjuntoMinimizavel(Iterable<List<Estado>> conjuntoMinimizavel, int iteracao) {
        StringBuilder conjuntosTextual = new StringBuilder();
        conjuntosTextual.append('S');
        conjuntosTextual.append(iteracao);
        conjuntosTextual.append(": {");
        for (List<Estado> conjunto : conjuntoMinimizavel) {
            conjuntosTextual.append('{');
            Iterator<Estado> estadoIterator = conjunto.iterator();
            Estado estado = estadoIterator.next();
            conjuntosTextual.append(estado);
            while (estadoIterator.hasNext()) {
                conjuntosTextual.append(',');
                estado = estadoIterator.next();
                conjuntosTextual.append(estado);
            }
            conjuntosTextual.append('}');
        }
        conjuntosTextual.append('}');

        LOGGER.info(conjuntosTextual.toString());
    }

    private List<Estado> criaEstadosDeConjuntos(Iterable<List<Estado>> conjuntos) {
        List<Estado> estados = new ArrayList<Estado>();
        for (List<Estado> estadoAgrupado : conjuntos) {
            Estado novoEstado = EstadoImpl.criaEstadoDeConjunto(estadoAgrupado);
            estados.add(novoEstado);
        }

        return estados;
    }

    private List<Estado> extraiEstadosFinaisDeEstados(Iterable<Estado> estadosConjunto) {
        List<Estado> estadosFinais = new ArrayList<Estado>();

        for (Estado estadoAgrupado : estadosConjunto) {
            for (Estado estadoFinal : this.estadosFinais) {
                if (estadoAgrupado.contains(estadoFinal)) {
                    estadosFinais.add(estadoAgrupado);
                    break;
                }
            }
        }

        return estadosFinais;
    }

    private Map<Transicao, Estado> extraiTransicoes(Iterable<Estado> estadosConjunto) {
        Map<Transicao, Estado> funcoesTransicao = new HashMap<Transicao, Estado>();

        for (Estado estadoAgrupado : estadosConjunto) {
            for (Simbolo simbolo : this.alfabeto) {
                Transicao transicao = new TransicaoImpl(estadoAgrupado.getEstadoRepresentativo(), simbolo);
                Estado proxEstado = this.funcoesTransicao.get(transicao);
                for (Estado estadoConjunto : estadosConjunto) {
                    if (estadoConjunto.contains(proxEstado)) {
                        funcoesTransicao.put(new TransicaoImpl(estadoAgrupado, simbolo), estadoConjunto);
                        break;
                    }
                }
            }
        }

        return funcoesTransicao;
    }

    private boolean isEstadosConjuntosDiferentes(Iterable<List<Estado>> conjuntoMinimizavel,
                                                 Estado estadoAtingidoPorEscolhido,
                                                 Estado estadoAtingidoPorD) {
        Collection<Estado> grupoEstados = new ArrayList<Estado>();
        grupoEstados.add(estadoAtingidoPorEscolhido);
        grupoEstados.add(estadoAtingidoPorD);
        for (List<Estado> estados : conjuntoMinimizavel) {
            if (estados.containsAll(grupoEstados)) {
                return false;
            }
        }
        return true;
    }


    private void retiraEstadosInatingiveis() {
        List<Estado> estados = new ArrayList<Estado>(this.estados);
        Iterable<Simbolo> alfabeto = new ArrayList<Simbolo>(this.alfabeto);
        Collection<Estado> estadosInatingiveis = new ArrayList<Estado>(estados);
        estadosInatingiveis.remove(this.estadoInicial);
        while (!estadosInatingiveis.isEmpty()) {
            for (Estado estado : estados) {
                for (Simbolo simbolo : alfabeto) {
                    Estado proximoEstado = this.aplicaFuncaoTransicao(estado, simbolo);
                    if (proximoEstado != null) {
                        estadosInatingiveis.remove(proximoEstado);
                    }
                }
            }
            estados.removeAll(estadosInatingiveis);
            if (!estadosInatingiveis.isEmpty()) {
                estadosInatingiveis.clear();
                estadosInatingiveis.addAll(estados);
                estadosInatingiveis.remove(this.estadoInicial);
            }
        }
        this.setEstados(estados);
    }

    public void setEstados(List<Estado> estados) {
        this.estados = new ArrayList<Estado>(estados);
    }

    public void setEstadosFinais(List<Estado> estadosFinais) {
        this.estadosFinais = new ArrayList<Estado>(estadosFinais);
    }

    public void setAlfabeto(List<Simbolo> alfabeto) {
        this.alfabeto = new ArrayList<Simbolo>(alfabeto);
    }

    public void setEstadoInicial(Estado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setFuncoesTransicao(Map<Transicao, Estado> funcoesTransicao) {
        this.funcoesTransicao = new HashMap<Transicao, Estado>(funcoesTransicao);
    }

    private Estado aplicaFuncaoTransicao(Estado estado, Simbolo simbolo) {
        return this.funcoesTransicao.get(new TransicaoImpl(estado, simbolo));
    }

    public String toText() {
        StringBuilder estadosStringBuilder = new StringBuilder();
        for (Estado estado : this.estados) {
            estadosStringBuilder.append(estado);
            estadosStringBuilder.append(' ');
        }

        StringBuilder alfabetoStringBuilder = new StringBuilder();
        for (Simbolo simbolo : this.alfabeto) {
            alfabetoStringBuilder.append(simbolo);
            alfabetoStringBuilder.append(' ');
        }

        StringBuilder funcoesTransicaoStringBuilder = new StringBuilder();
        for (Estado estado : this.estados) {
            for (Simbolo simbolo : this.alfabeto) {
                Transicao transicao = new TransicaoImpl(estado, simbolo);
                Estado estadoDestino = this.funcoesTransicao.get(transicao);
                funcoesTransicaoStringBuilder.append(estadoDestino);
                funcoesTransicaoStringBuilder.append(' ');
            }
            funcoesTransicaoStringBuilder.append(",\n");
        }
        funcoesTransicaoStringBuilder.delete(funcoesTransicaoStringBuilder.length() - 2, funcoesTransicaoStringBuilder.length());

        StringBuilder estadosFinaisStringBuilder = new StringBuilder();
        for (Estado estado : this.estadosFinais) {
            estadosFinaisStringBuilder.append(estado);
            estadosFinaisStringBuilder.append(' ');
        }

        return estadosStringBuilder.toString() + "; //estados\n" +
            alfabetoStringBuilder + "; //alfabeto\n\n" +
            funcoesTransicaoStringBuilder + "; //transicoes\n\n" +
            this.estadoInicial.toString() + " ; //estado inicial\n" +
            estadosFinaisStringBuilder + "; //estados finais";
    }

    public String toYUML() {
        StringBuilder transicoesStringBuilder = new StringBuilder("\n");
        for (Estado estado : this.estados) {
            for (Simbolo simbolo : this.alfabeto) {
                Transicao transicao = new TransicaoImpl(estado, simbolo);
                Estado estadoDestino = this.funcoesTransicao.get(transicao);
                transicoesStringBuilder.append(getyUMLDeEstado(estado.toString()));
                transicoesStringBuilder.append('-');
                transicoesStringBuilder.append(simbolo.getNome());
                transicoesStringBuilder.append('>');
                transicoesStringBuilder.append(getyUMLDeEstado(estadoDestino.toString()));
                transicoesStringBuilder.append("\n");
            }
        }

        transicoesStringBuilder.append(getyUMLDeEstado("start"));
        transicoesStringBuilder.append("->");
        transicoesStringBuilder.append(getyUMLDeEstado(this.estadoInicial.toString()));
        transicoesStringBuilder.append("\n");

        for (Estado estado : this.estadosFinais) {
            transicoesStringBuilder.append(getyUMLDeEstado(estado.toString()));
            transicoesStringBuilder.append("->");
            transicoesStringBuilder.append("(end)");
            transicoesStringBuilder.append("\n");
        }

        return transicoesStringBuilder.toString();
    }

    private String getyUMLDeEstado(String nome) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        stringBuilder.append(nome);
        stringBuilder.append(')');

        return stringBuilder.toString();
    }

    public Estado recuperaEstadoPeloNome(String nomeEstado) {
        Estado estado = null;
        for (Estado est : this.estados) {
            if (est.getNome().equals(nomeEstado)) {
                estado = est;
                break;
            }
        }
        return estado;
    }
}