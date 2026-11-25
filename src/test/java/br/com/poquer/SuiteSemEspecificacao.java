package br.com.poquer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteSemEspecificacao {

    private Mao criarMao(List<ValorCarta> valores, List<Naipe> naipes) {
        return new Mao(valores, naipes);
    }

    private List<ValorCarta> v(ValorCarta... v) {
        return List.of(v);
    }

    private List<Naipe> n(Naipe... n) {
        return List.of(n);
    }

    @Nested
    @DisplayName("Classificações válidas")
    class ClassificacoesValidas {

        @Test
        void sequenciaRealDoMesmoNaipe() {
            Mao mao = criarMao(
                    v(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS),
                    n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void sequenciaDoMesmoNaipe() {
            Mao mao = criarMao(
                    v(ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS, ValorCarta.SETE),
                    n(Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS));
            assertEquals(ClassificacaoMao.SEQUENCIA_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void quadra() {
            Mao mao = criarMao(
                    v(ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.DOIS),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.QUADRA, mao.classificar());
        }

        @Test
        void trincaEPar() {
            Mao mao = criarMao(
                    v(ValorCarta.CINCO, ValorCarta.CINCO, ValorCarta.CINCO, ValorCarta.REI, ValorCarta.REI),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.COPAS, Naipe.OUROS));
            assertEquals(ClassificacaoMao.TRINCA_E_PAR, mao.classificar());
        }

        @Test
        void cincoDoMesmoNaipe() {
            Mao mao = criarMao(
                    v(ValorCarta.AS, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.NOVE, ValorCarta.DAMA),
                    n(Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS));
            assertEquals(ClassificacaoMao.CINCO_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void sequenciaComAsBaixo() {
            Mao mao = criarMao(
                    v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }

        @Test
        void sequenciaComAsAlto() {
            Mao mao = criarMao(
                    v(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }

        @Test
        void trinca() {
            Mao mao = criarMao(
                    v(ValorCarta.QUATRO, ValorCarta.QUATRO, ValorCarta.QUATRO, ValorCarta.SETE, ValorCarta.NOVE),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.TRINCA, mao.classificar());
        }

        @Test
        void doisPares() {
            Mao mao = criarMao(
                    v(ValorCarta.DOIS, ValorCarta.DOIS, ValorCarta.SEIS, ValorCarta.SEIS, ValorCarta.REI),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.DOIS_PARES, mao.classificar());
        }

        @Test
        void umPar() {
            Mao mao = criarMao(
                    v(ValorCarta.OITO, ValorCarta.OITO, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.DAMA),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.UM_PAR, mao.classificar());
        }

        @Test
        void cartaAlta() {
            Mao mao = criarMao(
                    v(ValorCarta.AS, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.NOVE, ValorCarta.REI),
                    n(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao.classificar());
        }

        @Test
        void ordemIndiferente() {
            List<ValorCarta> valores = new ArrayList<>(v(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS));
            List<Naipe> naipes = new ArrayList<>(n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS));
            // embaralhar
            Collections.swap(valores, 0, 3);
            Collections.swap(naipes, 0, 3);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao.classificar());
        }
    }

    @Nested
    @DisplayName("Validações de construtor")
    class ValidacoesConstrutor {

        @Test
        void tamanhoInvalidoMenor() {
            List<ValorCarta> valores = v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void tamanhoInvalidoMaior() {
            List<ValorCarta> valores = v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS);
            List<Naipe> naipes = n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void listaNula() {
            assertThrows(NullPointerException.class, () -> new Mao(null, n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS)));
            assertThrows(NullPointerException.class, () -> new Mao(v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO), null));
        }

        @Test
        void elementoNuloNasListas() {
            List<ValorCarta> valoresComNulo = new ArrayList<>(v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO));
            valoresComNulo.set(2, null);
            List<Naipe> naipes = n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(NullPointerException.class, () -> new Mao(valoresComNulo, naipes));

            List<Naipe> naipesComNulo = new ArrayList<>(n(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS));
            naipesComNulo.set(4, null);
            List<ValorCarta> valores = v(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            assertThrows(NullPointerException.class, () -> new Mao(valores, naipesComNulo));
        }

        @Test
        void cartasRepetidas() {
            List<ValorCarta> valores = v(ValorCarta.AS, ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = n(Naipe.COPAS, Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }
    }
}