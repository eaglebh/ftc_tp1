package tp1;

public class SimboloImpl implements Simbolo {

    private final String nome;

    public SimboloImpl(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public String toString() {
        return nome;
    }
}