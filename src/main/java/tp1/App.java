package tp1;

import java.util.Set;

/**
 * Entrada: um AFD (E; ; ; i; F ).
 * Saída: AFD (E 0   ; ;     0       ; i   0          ; F 0    ).
 * conjunto de conjuntos de estados : R; S;
 * conjunto de estados : C; Y;X ;
 * estado : e; d;
 * elimine todo estado alcançável a partir do estado inicial i;
 * R := fE   F; F g   ffgg;
 * repita
 * S := R; R := fg;
 * para cada C 2 S faça
 * repita
 * escolha e de C ;
 * Y := feg;
 * L: para cada d 2 C   feg faça
 * para cada a 2  faça
 * seja X 2 S tal que (e; a) 2 X ;
 * se (d; a) 62 X então continue L fimse
 * fimpara;
 * Y := Y [ fdg
 * fimpara;
 * C := C   Y ;
 * R := R [ fY g;
 * até C = fg
 * fimpara
 * até R = S ;
 * E
 * 0
 * := S ;
 * <p/>
 * 0
 * : para todos C 2 S e a 2 :
 * <p/>
 * 0
 * (C; a) = conjunto em S que contém (e; a) para algum a 2 C
 * i
 * 0
 * := conjunto em S que contém i;
 * F
 * 0
 * := fC 2 S j C  F g;
 */

public class App {

    public static void main(String[] args) {
        Set<Set<Estado>> resultadoParcial;
        Set<Set<Estado>> conjuntoMinimizavel;
        Automato automatoEntrada = new Automato();

        // elimina todos os estados inatingiveis
        automatoEntrada.retiraEstadosInatingiveis();

        // cria S0 com dois conjuntos : iniciais e finais

        // se Sn != S(n-1) então
        // para cada conjunto A de Sn faça
        // se o conjunto tem mais de um estado então, para cada estado e do conjunto A faça
        // verifique se e vai para o mesmo conjunto dos outros do mesmo estado
        // se for, entao permanece no estado A
        // senao, deve fazer parte de outro conjunto novo
        // se o
    }


}
