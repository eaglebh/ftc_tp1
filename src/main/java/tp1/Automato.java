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