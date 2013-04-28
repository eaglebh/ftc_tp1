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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimboloImpl simbolo = (SimboloImpl) o;

        if (!nome.equals(simbolo.nome)) return false;

        return true;
    }
}