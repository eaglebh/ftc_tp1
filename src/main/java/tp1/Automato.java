package tp1;

import java.util.List;
import java.util.Map;

/**
 * @author Pablo
 */
public interface Automato {
    public Automato criaEquivalenteMinimizado();

    public String toText();

    public String toYUML();

    public void preencheTransicoesParaEstado(String linha, Estado estadoOrigem);

    public Estado recuperaEstadoPeloNome(String nome);

    public void setEstadosFinais(List<Estado> estados);

    public void setEstadoInicial(Estado estado);

    public void setEstados(List<Estado> estados);

    public void setAlfabeto(List<Simbolo> alfabeto);

    public void setFuncoesTransicao(Map<Transicao, Estado> funcoesTransicao);
}
