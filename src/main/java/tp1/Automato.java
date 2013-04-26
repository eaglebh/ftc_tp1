package tp1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Automato {
    private Set<Estado> estados;
    private Set<Estado> estadosFinais;
    private Set<Simbolo> alfabeto;
    private Estado estadoInicial;
    private Map<Transicao, Estado> funcoesTransicao;

    public Automato() {
    }

    public Automato(Automato automatoEntrada) {
        this.setEstados(new HashSet<Estado>(automatoEntrada.getEstados()));
        this.setEstadosFinais(new HashSet<Estado>(automatoEntrada.getEstadosFinais()));
        this.setAlfabeto(new HashSet<Simbolo>(automatoEntrada.getAlfabeto()));
        this.setEstadoInicial(automatoEntrada.getEstadoInicial());
        this.setFuncoesTransicao( new HashMap<Transicao, Estado>(automatoEntrada.getFuncoesTransicao()));
    }

    void retiraEstadosInatingiveis() {
        Set<Estado> estados = new HashSet<Estado>(this.getEstados());
        HashSet<Simbolo> alfabeto = new HashSet<Simbolo>(this.getAlfabeto());
        Set<Estado> estadosInatingiveis = new HashSet<Estado>(estados);
        while (!estadosInatingiveis.isEmpty()) {
            for (Estado estado : estados) {
                boolean alcancado = false;
                for (Simbolo simbolo : alfabeto) {
                    Estado proximoEstado = this.aplicaFuncaoTransicao(estado, simbolo);
                    if (proximoEstado != null) {
                        alcancado = true;
                        break;
                    }
                }
                if(!alcancado) {
                    estadosInatingiveis.remove(estado);
                }
            }
            estados.removeAll(estadosInatingiveis);
            estadosInatingiveis.addAll(estados);
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

    public Set<Simbolo> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(Set<Simbolo> alfabeto) {
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