package tp1;

public class Simbolo {

    private static int chaveAtualGerada = 0;

    private static synchronized int gerarChave() {
        ++chaveAtualGerada;
        return chaveAtualGerada;
    }

    private int chave;
    private String nome;

    public Simbolo(String nome) {
        this.chave = gerarChave();
        // TODO deve verificar se ja existe um simbolo com esse nome
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public int hashCode() {
        return chave;
    }
}