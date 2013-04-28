package tp1;

import java.util.ArrayList;
import java.util.List;

public class EstadoImpl implements Estado {

    private static int chaveAtualGerada = 0;

    private final int chave;

    private final String nome;
    private List<Estado> conjunto = null;

    public static Estado criaEstado(String nome) {
        return new EstadoImpl(nome);
    }

    public static Estado criaEstadoDeConjunto(List<Estado> conjunto) {
        Estado estado;
        if (conjunto.size() > 1) {
            estado = new EstadoImpl(conjunto);
        } else {
            estado = new EstadoImpl(conjunto.get(0).getNome());
        }

        return estado;
    }

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

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    public String getNome() {
        return nome;
    }

    public Estado getEstadoRepresentativo() {
        return (this.conjunto == null || this.conjunto.isEmpty()) ? this : this.conjunto.iterator().next();
    }

    public static void reiniciarChave() {
        chaveAtualGerada = 0;
    }

    public boolean contains(Estado estado) {
        boolean contains;

        if (this.conjunto == null) {
            contains = this.equals(estado);
        } else {
            contains = this.conjunto.contains(estado);
        }

        return contains;
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Estado) {
            Estado other = (Estado) obj;
            return this.nome.equals(other.getNome());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(chave);
    }
}