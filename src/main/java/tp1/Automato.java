package tp1;

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
        return this.funcoesTransicao.get(new Transicao(estado, simbolo));
    }
}