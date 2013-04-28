package tp1;

/**
 * @author Pablo
 */
public class TransicaoImpl implements Transicao {
    private final Estado estado;
    private final Simbolo simbolo;

    public TransicaoImpl(Estado estado, Simbolo simbolo) {
        this.estado = estado;
        this.simbolo = simbolo;
    }

    public Estado getEstado() {
        return estado;
    }

    public Simbolo getSimbolo() {
        return simbolo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transicao) {
            Transicao other = (Transicao) obj;
            return this.getEstado().equals(other.getEstado()) &&
                this.getSimbolo().equals(other.getSimbolo());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.estado.hashCode() ^ this.simbolo.hashCode();
    }

    @Override
    public String toString() {
        return "Transicao{" +
            "estado=" + estado +
            ", simbolo=" + simbolo +
            '}';
    }
}
