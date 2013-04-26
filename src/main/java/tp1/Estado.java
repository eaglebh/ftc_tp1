package tp1;

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