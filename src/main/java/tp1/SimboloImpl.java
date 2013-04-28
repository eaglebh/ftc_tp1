package tp1;

public class SimboloImpl implements Simbolo {

    private static int chaveAtualGerada = -1;

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    private int chave;
    private String nome;

    public SimboloImpl(String nome) {
        this.chave = gerarChave();
        this.nome = nome;
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

    public static void reiniciarChave() {
        chaveAtualGerada = 0;
    }
}