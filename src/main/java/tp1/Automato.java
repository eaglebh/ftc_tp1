package tp1;

import java.util.*;

public class Automato {
    private Set<Estado> estados;
    private Set<Estado> estadosFinais;
    private List<Simbolo> alfabeto;
    private Estado estadoInicial;
    private Map<Transicao, Estado> funcoesTransicao;

    public Automato() {
        this.funcoesTransicao = new HashMap<Transicao, Estado>();
    }

    public Automato(Automato automatoEntrada) {
        this.setEstados(new HashSet<Estado>(automatoEntrada.getEstados()));
        this.setEstadosFinais(new HashSet<Estado>(automatoEntrada.getEstadosFinais()));
        this.setAlfabeto(new ArrayList<Simbolo>(automatoEntrada.getAlfabeto()));
        this.setEstadoInicial(automatoEntrada.getEstadoInicial());
        this.setFuncoesTransicao( new HashMap<Transicao, Estado>(automatoEntrada.getFuncoesTransicao()));
    }

    private Estado encontraConjuntoDoEstado(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoInicial) {
        for (Set<Estado> estados : conjuntoMinimizavel) {
            if (estados.contains(estadoInicial)) {
                return Estado.criaEstadoDeConjunto(estados);
            }
        }
        return null;
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

    public Automato minimizaEstados(Set<Set<Estado>> conjuntoMinimizavel) {
        Set<Set<Estado>> resultadoParcial;// cria S0 com dois conjuntos : nao-finais e finais
        resultadoParcial = new HashSet<Set<Estado>>();
        Set<Estado> estadosNaoFinais = this.getEstados();
        estadosNaoFinais.removeAll(this.getEstadosFinais());
        resultadoParcial.add(estadosNaoFinais);
        resultadoParcial.add(this.getEstadosFinais());

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
                    conjuntoMinimizavel.removeAll(estadosAgrupados);

                    // R = R U {Y}
                    Set<Estado> conjuntoAgrupado = new HashSet<Estado>();
                    conjuntoAgrupado.addAll(estadosAgrupados);
                    resultadoParcial.add(conjuntoAgrupado);
                }
            }
        }

        Automato automatoSaida = new Automato(this);
        Set<Estado> estadosSaida = new HashSet<Estado>();
        for (Set<Estado> estados : conjuntoMinimizavel) {
            Estado novoEstado = Estado.criaEstadoDeConjunto(estados);
            estadosSaida.add(novoEstado);
        }
        // E' = S
        automatoSaida.setEstados(estadosSaida);

        // i' = conjunto em S que contém i
        Estado novoEstadoInicial = this.encontraConjuntoDoEstado(conjuntoMinimizavel, this.getEstadoInicial());
        automatoSaida.setEstadoInicial(novoEstadoInicial);

        // F' =

        return automatoSaida;
    }

    private boolean isEstadosMesmoConjunto(Set<Set<Estado>> conjuntoMinimizavel, Estado estadoAtingidoPorEscolhido, Estado estadoAtingidoPorD) {
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


    void retiraEstadosInatingiveis() {
        Set<Estado> estados = new HashSet<Estado>(this.getEstados());
        HashSet<Simbolo> alfabeto = new HashSet<Simbolo>(this.getAlfabeto());
        Set<Estado> estadosInatingiveis = new HashSet<Estado>(estados);
        while (!estadosInatingiveis.isEmpty()) {
            estadosInatingiveis.addAll(estados);
            for (Estado estado : estados) {
                boolean alcancado = false;
                for (Simbolo simbolo : alfabeto) {
                    Estado proximoEstado = this.aplicaFuncaoTransicao(estado, simbolo);
                    if (proximoEstado != null) {
                        alcancado = true;
                        break;
                    }
                }
                if(alcancado) {
                    estadosInatingiveis.remove(estado);
                }
            }
            estados.removeAll(estadosInatingiveis);
        }
        this.setEstados(estados);
    }

    public Set<Estado> getEstados() {
        return estados;
    }

    public void setEstados(Set<Estado> estados) {
        this.estados = estados;
    }

    public Set<Estado> getEstadosFinais() {
        return estadosFinais;
    }

    public void setEstadosFinais(Set<Estado> estadosFinais) {
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

    public Estado aplicaFuncaoTransicao(Estado estado, Simbolo simbolo){
        return this.getFuncoesTransicao().get(new Transicao(estado, simbolo));
    }

    public Map<Transicao, Estado> getFuncoesTransicao() {
        return funcoesTransicao;
    }
}