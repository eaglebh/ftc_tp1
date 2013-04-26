package tp1;

/**
 * @author Pablo
 */
public class Transicao {
    private Estado estado;
    private Simbolo simbolo;

    public Transicao(Estado estado, Simbolo simbolo) {
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
}