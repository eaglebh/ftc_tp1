package tp1;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Pablo
 */
public class AppTest {
    private Automato automatoTeste1;
    private Automato automatoTeste1Out;

    @Before
    public void setUp() throws Exception {
        this.automatoTeste1 = new AutomatoImpl();

        EstadoImpl.reiniciarChave();
        List<Estado> estados = new ArrayList<Estado>();
        estados.add(EstadoImpl.criaEstado("1"));
        estados.add(EstadoImpl.criaEstado("2"));
        estados.add(EstadoImpl.criaEstado("3"));
        automatoTeste1.setEstados(estados);

        List<Simbolo> alfabeto = new ArrayList<Simbolo>();
        alfabeto.add(new SimboloImpl("0"));
        alfabeto.add(new SimboloImpl("1"));
        automatoTeste1.setAlfabeto(alfabeto);

        automatoTeste1.setEstadoInicial(estados.get(0));

        List<Estado> finais = new ArrayList<Estado>();
        finais.add(estados.get(1));
        finais.add(estados.get(2));
        automatoTeste1.setEstadosFinais(finais);

        Map<Transicao, Estado> funcoesTransicao = new HashMap<Transicao, Estado>();
        funcoesTransicao.put(new TransicaoImpl(estados.get(0), alfabeto.get(0)), estados.get(1));
        funcoesTransicao.put(new TransicaoImpl(estados.get(0), alfabeto.get(1)), estados.get(2));
        funcoesTransicao.put(new TransicaoImpl(estados.get(1), alfabeto.get(0)), estados.get(1));
        funcoesTransicao.put(new TransicaoImpl(estados.get(1), alfabeto.get(1)), estados.get(2));
        funcoesTransicao.put(new TransicaoImpl(estados.get(2), alfabeto.get(0)), estados.get(1));
        funcoesTransicao.put(new TransicaoImpl(estados.get(2), alfabeto.get(1)), estados.get(2));

        automatoTeste1.setFuncoesTransicao(funcoesTransicao);

        automatoTeste1Out = App.leAutomatoDoArquivo("arquivosTeste\\teste1.out");
    }

    @Test
    public void testLeAutomatoDoArquivo() throws Exception {
        Automato automato = App.leAutomatoDoArquivo("arquivosTeste\\teste1.txt");

        assertEquals(automato, automatoTeste1);
    }

    @Test
    public void testCaso1() throws Exception {
        Automato automatoEntrada = App.leAutomatoDoArquivo("arquivosTeste\\teste1.txt");
        Automato automatoSaida = automatoEntrada.criaEquivalenteMinimizado();

        assertEquals(automatoSaida, automatoTeste1Out);
    }
}
