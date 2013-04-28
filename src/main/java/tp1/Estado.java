package tp1;

/**
 * @author Pablo
 */
public interface Estado {
    public String getNome();

    public boolean contains(Estado estadoFinal);

    public Estado getEstadoRepresentativo();
}
