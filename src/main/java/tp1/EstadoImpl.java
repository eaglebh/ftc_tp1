package tp1;

import java.util.ArrayList;
import java.util.List;

public class EstadoImpl implements Estado {

    private static int chaveAtualGerada = 0;

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    private final int chave;
    private final String nome;
    private List<Estado> conjunto = null;

    private EstadoImpl(String nome) {
        this.chave = gerarChave();
        this.nome = nome;
    }

    private EstadoImpl(List<Estado> conjunto) {
        StringBuilder novoNome = new StringBuilder();
        for (Estado estado : conjunto) {
            // vai concatenar com outro nome de estado anterior
            if (novoNome.length() != 0) {
                novoNome.append('_');
            }
            novoNome.append(estado.getNome());
        }
        this.chave = gerarChave();
        this.nome = novoNome.toString();
        this.conjunto = new ArrayList<Estado>(conjunto);
    }

    public static Estado criaEstado(String nome) {
        return new EstadoImpl(nome);
    }

    public static Estado criaEstadoDeConjunto(List<Estado> conjunto) {
        return new EstadoImpl(conjunto);
    }

    public String getNome() {
        return nome;
    }

    public boolean contains(Estado estado) {
        return (this.conjunto != null) && this.conjunto.contains(estado);
    }

    @Override
    public int hashCode() {
        return chave;
    }

    @Override
    public String toString() {
        return String.valueOf(chave);
    }

    public Estado getEstadoRepresentativo() {
        return (this.conjunto == null || this.conjunto.isEmpty()) ? this : this.conjunto.iterator().next();
    }

    public static void reiniciarChave() {
        chaveAtualGerada = 0;
    }
}