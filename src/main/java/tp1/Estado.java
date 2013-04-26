package tp1;

import java.util.Set;

public class Estado {

    private static int chaveAtualGerada = 0;

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    private int chave;
    private String nome;

    public Estado(String nome) {
        this.chave = gerarChave();
        // TODO deve verificar se ja existe um estado com esse nome
        this.nome = nome;
    }

    static Estado criaEstadoDeConjunto(Set<Estado> estados) {
        StringBuilder novoNome = new StringBuilder();
        for (Estado estado : estados) {
            // vai concatenar com outro nome de estado anterior
            if (novoNome.length() != 0) {
                novoNome.append('_');
            }
            novoNome.append(estado.getNome());
        }
        return new Estado(novoNome.toString());
    }

    public String getNome() {
        return nome;
    }

    @Override
    public int hashCode() {
        return chave;
    }

    @Override
    public String toString() {
        return nome;
    }
}