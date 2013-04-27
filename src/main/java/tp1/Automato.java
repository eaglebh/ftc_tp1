package tp1;

import java.util.*;

public class Automato {
    private List<Estado> estados;
    private List<Estado> estadosFinais;
    private List<Simbolo> alfabeto;
    private Estado estadoInicial;
    private Map<Transicao, Estado> funcoesTransicao;

    public Automato() {
        this.funcoesTransicao = new HashMap<Transicao, Estado>();
    }

    public Automato(Automato automatoEntrada) {
        this.setEstados(new ArrayList<Estado>(automatoEntrada.getEstados()));
        this.setEstadosFinais(new ArrayList<Estado>(automatoEntrada.getEstadosFinais()));
        this.setAlfabeto(new ArrayList<Simbolo>(automatoEntrada.getAlfabeto()));
        this.setEstadoInicial(automatoEntrada.getEstadoInicial());
        this.setFuncoesTransicao(new HashMap<Transicao, Estado>(automatoEntrada.getFuncoesTransicao()));
    }

    private Estado extraiEstadoInicialDeEstados(List<Estado> conjuntoMinimizavel, Estado estadoInicial) {
        for (Estado estadoAgrupado : conjuntoMinimizavel) {
            if (estadoAgrupado.contains(estadoInicial)) {
                return estadoAgrupado;
            }
        }
        return null;
    }

    public Estado getEstadoPelaOrdem(int indice) {
        return (indice < 0 || indice >= this.getEstados().size()) ? null : this.getEstados().get(indice);
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

    public Automato minimizaEstados() {
        Set<List<Estado>> conjuntoMinimizavel = null;
        Set<List<Estado>> resultadoParcial;// cria S0 com dois conjuntos : nao-finais e finais
        resultadoParcial = new HashSet<List<Estado>>();
        List<Estado> estadosNaoFinais = this.getEstados();
        estadosNaoFinais.removeAll(this.getEstadosFinais());
        resultadoParcial.add(estadosNaoFinais);
        resultadoParcial.add(this.getEstadosFinais());

        int contadorIteracoes = 0;
        while (!resultadoParcial.equals(conjuntoMinimizavel)) {
            conjuntoMinimizavel = new HashSet<List<Estado>>(resultadoParcial);
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
                    Set<Estado> estadosRestantesConj = new HashSet<Estado>(subConjunto);
                    estadosRestantesConj.remove(estadoEscolhido);
                    for (Estado estadoD : estadosRestantesConj) {
                        boolean agrupa = true;
                        for (Simbolo simbolo : this.getAlfabeto()) {
                            // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
                            Estado estadoAtingidoPorEscolhido = this.aplicaFuncaoTransicao(estadoEscolhido, simbolo);
                            Estado estadoAtingidoPorD = this.aplicaFuncaoTransicao(estadoD, simbolo);
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

        Automato automatoSaida = new Automato(this);
        // E' = S
        automatoSaida.setEstados(estadosSaida);
        // i' = conjunto em S que contém i
        Estado novoEstadoInicial = this.extraiEstadoInicialDeEstados(estadosSaida, this.getEstadoInicial());
        automatoSaida.setEstadoInicial(novoEstadoInicial);
        // F' = {C E S|C conj-prop F}
        automatoSaida.setEstadosFinais(estadosFinaisSaida);
        // ft' : para todos C E S e E Alfabeto :
        // ft'(C, a) = conjunto em S que contém ft(e, a) para algum e E C
        automatoSaida.setFuncoesTransicao(funcoesTransicaoSaida);

        return automatoSaida;
    }

    private void imprimeConjuntoMinimizavel(Set<List<Estado>> conjuntoMinimizavel, int iteracao) {
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

        System.out.println(conjuntosTextual.toString());
    }

    private List<Estado> criaEstadosDeConjuntos(Set<List<Estado>> conjuntos) {
        List<Estado> estados = new ArrayList<Estado>();
        for (List<Estado> estadoAgrupado : conjuntos) {
            Estado novoEstado = Estado.criaEstadoDeConjunto(estadoAgrupado);
            estados.add(novoEstado);
        }

        return estados;
    }

    private List<Estado> extraiEstadosFinaisDeEstados(List<Estado> estadosConjunto) {
        List<Estado> estadosFinais = new ArrayList<Estado>();

        for (Estado estadoAgrupado : estadosConjunto) {
            for (Estado estadoFinal : this.getEstadosFinais()) {
                if (estadoAgrupado.contains(estadoFinal)) {
                    estadosFinais.add(estadoAgrupado);
                    break;
                }
            }
        }

        return estadosFinais;
    }

    private Map<Transicao, Estado> extraiTransicoes(List<Estado> estadosConjunto) {
        Map<Transicao, Estado> funcoesTransicao = new HashMap<Transicao, Estado>();

        for (Estado estadoAgrupado : estadosConjunto) {
            for (Simbolo simbolo : this.alfabeto) {
                Transicao transicao = new Transicao(estadoAgrupado.getEstadoRepresentativo(), simbolo);
                Estado proxEstado = this.funcoesTransicao.get(transicao);
                for (Estado estadoConjunto : estadosConjunto) {
                    if (estadoConjunto.contains(proxEstado)) {
                        funcoesTransicao.put(new Transicao(estadoAgrupado, simbolo), estadoConjunto);
                        break;
                    }
                }
            }
        }

        return funcoesTransicao;
    }

    private boolean isEstadosMesmoConjunto(Set<List<Estado>> conjuntoMinimizavel, Estado estadoAtingidoPorEscolhido, Estado estadoAtingidoPorD) {
        Set<Estado> grupoEstados = new HashSet<Estado>();
        grupoEstados.add(estadoAtingidoPorEscolhido);
        grupoEstados.add(estadoAtingidoPorD);
        for (List<Estado> estados : conjuntoMinimizavel) {
            if (estados.containsAll(grupoEstados)) {
                return true;
            }
        }
        return false;
    }


    void retiraEstadosInatingiveis() {
        List<Estado> estados = new ArrayList<Estado>(this.getEstados());
        Iterable<Simbolo> alfabeto = new HashSet<Simbolo>(this.getAlfabeto());
        Set<Estado> estadosInatingiveis = new HashSet<Estado>(estados);
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

    public List<Estado> getEstados() {
        return estados;
    }

    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    public List<Estado> getEstadosFinais() {
        return estadosFinais;
    }

    public void setEstadosFinais(List<Estado> estadosFinais) {
        this.estadosFinais = estadosFinais;
    }

    public List<Simbolo> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(List<Simbolo> alfabeto) {
        this.alfabeto = alfabeto;
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setFuncoesTransicao(Map<Transicao, Estado> funcoesTransicao) {
        this.funcoesTransicao = funcoesTransicao;
    }

    public Estado aplicaFuncaoTransicao(Estado estado, Simbolo simbolo) {
        return this.getFuncoesTransicao().get(new Transicao(estado, simbolo));
    }

    public Map<Transicao, Estado> getFuncoesTransicao() {
        return funcoesTransicao;
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
                Transicao transicao = new Transicao(estado, simbolo);
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
            alfabetoStringBuilder + "; //alfabeto\n" +
            funcoesTransicaoStringBuilder + "; //transicoes\n" +
            estadoInicial + "; //estado inicial\n" +
            estadosFinaisStringBuilder + "; //estados finais";
    }

    public String toyUML() {
        StringBuilder transicoesStringBuilder = new StringBuilder("\n");
        for (Estado estado : this.estados) {
            for (Simbolo simbolo : this.alfabeto) {
                Transicao transicao = new Transicao(estado, simbolo);
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

    public Estado getEstadoPeloNome(String nomeEstado) {
        Estado estado = null;
        for (Estado est : this.getEstados()) {
            if (est.getNome().equals(nomeEstado)) {
                estado = est;
                break;
            }
        }
        return estado;
    }
}