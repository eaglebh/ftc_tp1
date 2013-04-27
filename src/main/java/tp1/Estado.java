package tp1;

import java.util.HashSet;
import java.util.Set;

public class Estado {

    private static int chaveAtualGerada = 0;

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    private int chave;
    private String nome;
    private Set<Estado> conjunto = null;

    private Estado(String nome) {
        this.chave = gerarChave();
        // TODO deve verificar se ja existe um estado com esse nome
        this.nome = nome;
    }

    private Estado(Set<Estado> conjunto) {
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
        this.conjunto = new HashSet<Estado>(conjunto);
    }

    public static Estado criaEstado(String nome) {
        return new Estado(nome);
    }

    public static Estado criaEstadoDeConjunto(Set<Estado> conjunto) {
        return new Estado(conjunto);
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
        return nome;
    }

    public Estado getEstadoRepresentativo() {
        return (this.conjunto == null || this.conjunto.isEmpty()) ? this : this.conjunto.iterator().next();
    }
}