package tp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class App {

    private static final Logger LOGGER = Logger.getLogger("FTC-Tp1");

    public static void main(String[] args) {
        configuraLogger();

        String filePath = recuperaArquivoEntrada(args);

        Automato automatoEntrada = leAutomatoDoArquivo(filePath);

        Automato automatoSaida = automatoEntrada.criaEquivalenteMinimizado();

        imprimirTexto(automatoSaida, recuperaImprimirTexto(args));
        imprimirYUML(automatoSaida, recuperaImprimirYUML(args));
    }

    private static void configuraLogger() {
        LOGGER.setUseParentHandlers(false);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage()+ "\n";
            }
        });
        LOGGER.addHandler(consoleHandler);
    }

    private static void imprimirYUML(Automato automatoSaida, boolean habilitado) {
        if (habilitado) {
            LOGGER.info(automatoSaida.toYUML());
        }
    }

    private static void imprimirTexto(Automato automatoSaida, boolean habilitado) {
        if (habilitado) {
            LOGGER.info(automatoSaida.toText());
        }
    }

    private static String recuperaArquivoEntrada(String[] args) {
        String filePath = recuperaParametro(args, 0);
        return filePath == null ? "input" : filePath;
    }

    private static boolean recuperaImprimirTexto(String[] args) {
        String param = recuperaParametro(args, 1);
        return Boolean.parseBoolean(param);
    }

    private static boolean recuperaImprimirYUML(String[] args) {
        String param = recuperaParametro(args, 1);
        return Boolean.parseBoolean(param);
    }

    private static String recuperaParametro(String[] args, int indice) {
        String parametro = null;
        if (args.length > indice) {
            parametro = args[indice];
        }
        return parametro;
    }


    public static Automato leAutomatoDoArquivo(String filePath) {
        Automato novoAutomato = new AutomatoImpl();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String linha = br.readLine();
            int quantidadeEstados = preencheEstados(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheAlfabeto(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            for (int i = 0; i < quantidadeEstados; i++) {
                novoAutomato.preencheTransicoes(linha, i);
                linha = br.readLine();
            }

            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheEstadoInicial(novoAutomato, linha);

            linha = br.readLine();
            while (linha.trim().isEmpty()) {
                linha = br.readLine();
            }
            preencheEstadosFinais(novoAutomato, linha);
            br.close();
        } catch (IOException ioe) {
            LOGGER.severe("Erro na leitura do arquivo " + filePath);
        }

        return novoAutomato;
    }

    private static void preencheEstadosFinais(Automato novoAutomato, String linha) {
        String[] estadosFinaisStr = linha.split(" ");
        List<Estado> estadosFinais = new ArrayList<Estado>();
        for (String estadoFinalStr : estadosFinaisStr) {
            if (estadoFinalStr.contains(";")) {
                break;
            }
            Estado estadoFinal = novoAutomato.getEstadoPeloNome(estadoFinalStr);
            estadosFinais.add(estadoFinal);
        }
        novoAutomato.setEstadosFinais(estadosFinais);
    }

    private static void preencheEstadoInicial(Automato novoAutomato, String linha) {
        Estado estadoInicial = novoAutomato.getEstadoPeloNome(linha.split(" ")[0]);
        novoAutomato.setEstadoInicial(estadoInicial);
    }

    public static int preencheEstados(Automato aut, String estados) {
        String[] arrayEstados = estados.replace(';', ' ').trim().split(" ");
        List<Estado> conjuntoEstados = new ArrayList<Estado>();
        for (String nome : arrayEstados) {
            Estado novoEstado = EstadoImpl.criaEstado(nome);
            conjuntoEstados.add(novoEstado);
        }
        aut.setEstados(conjuntoEstados);
        return conjuntoEstados.size();
    }

    public static void preencheAlfabeto(Automato aut, String alfabeto) {
        String[] arraySimbolos = alfabeto.replace(';', ' ').trim().split(" ");
        List<Simbolo> conjuntoAlfabeto = new ArrayList<Simbolo>();
        for (String simbolo : arraySimbolos) {
            Simbolo novoSimbolo = new SimboloImpl(simbolo);
            conjuntoAlfabeto.add(novoSimbolo);
        }
        aut.setAlfabeto(conjuntoAlfabeto);
    }

}
