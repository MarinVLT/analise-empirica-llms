package br.com.poquer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteSemCodigo {

    // Helper to create a hand
    private Mao criarMao(List<ValorCarta> valores, List<Naipe> naipes) {
        return new Mao(valores, naipes);
    }

    @Nested
    @DisplayName("Validações de construtor")
    class ConstrutorValidacoes {

        @Test
        void deveLancarNullPointerExceptionQuandoListaValoresNula() {
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(NullPointerException.class, () -> new Mao(null, naipes));
        }

        @Test
        void deveLancarNullPointerExceptionQuandoListaNaipesNula() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO);
            assertThrows(NullPointerException.class, () -> new Mao(valores, null));
        }

        @Test
        void deveLancarNullPointerExceptionQuandoElementoValorNulo() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(NullPointerException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void deveLancarNullPointerExceptionQuandoElementoNaipeNulo() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, null, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(NullPointerException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void deveLancarIllegalArgumentExceptionQuandoTamanhoInvalidoMenor() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void deveLancarIllegalArgumentExceptionQuandoTamanhoInvalidoMaior() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO, ValorCarta.SEIS);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS,
                    Naipe.COPAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void deveLancarIllegalArgumentExceptionQuandoCartaRepetida() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.AS, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        void deveCriarMaoQuandoEntradasValidas() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO,
                    ValorCarta.CINCO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertDoesNotThrow(() -> criarMao(valores, naipes));
        }
    }

    @Nested
    @DisplayName("Classificações de mão")
    class Classificacoes {

        @Test
        void sequenciaRealDoMesmoNaipe() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA,
                    ValorCarta.REI, ValorCarta.AS);
            List<Naipe> naipes = Arrays.asList(Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void sequenciaDoMesmoNaipe() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO,
                    ValorCarta.SEIS, ValorCarta.SETE);
            List<Naipe> naipes = Arrays.asList(Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.SEQUENCIA_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void quadra() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.REI, ValorCarta.REI, ValorCarta.REI, ValorCarta.REI,
                    ValorCarta.AS);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.QUADRA, mao.classificar());
        }

        @Test
        void trincaEPar() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.NOVE,
                    ValorCarta.DAMA, ValorCarta.DAMA);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.COPAS, Naipe.OUROS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.TRINCA_E_PAR, mao.classificar());
        }

        @Test
        void cincoDoMesmoNaipeSemSequencia() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.TRES, ValorCarta.CINCO,
                    ValorCarta.SEIS, ValorCarta.NOVE);
            List<Naipe> naipes = Arrays.asList(Naipe.ESPADAS, Naipe.ESPADAS, Naipe.ESPADAS, Naipe.ESPADAS,
                    Naipe.ESPADAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.CINCO_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void sequenciaComNaipesMisturados() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES,
                    ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }

        @Test
        void trinca() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.VALETE, ValorCarta.VALETE,
                    ValorCarta.VALETE, ValorCarta.CINCO, ValorCarta.NOVE);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.TRINCA, mao.classificar());
        }

        @Test
        void doisPares() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.DOIS, ValorCarta.DOIS, ValorCarta.SEIS,
                    ValorCarta.SEIS, ValorCarta.REI);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.DOIS_PARES, mao.classificar());
        }

        @Test
        void umPar() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.QUATRO, ValorCarta.QUATRO, ValorCarta.SEIS,
                    ValorCarta.NOVE, ValorCarta.REI);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.UM_PAR, mao.classificar());
        }

        @Test
        void cartaAlta() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.AS, ValorCarta.TRES, ValorCarta.CINCO,
                    ValorCarta.SETE, ValorCarta.NOVE);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao.classificar());
        }

        @Test
        void prioridadeSequenciaRealSobreSequenciaDoMesmoNaipe() {
            // Hand that satisfies both real sequence and normal sequence but real has higher priority
            List<ValorCarta> valores = Arrays.asList(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA,
                    ValorCarta.REI, ValorCarta.AS);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            Mao mao = criarMao(valores, naipes);
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        void ordemIndiferente() {
            List<ValorCarta> valores = Arrays.asList(ValorCarta.CINCO, ValorCarta.AS, ValorCarta.TRES,
                    ValorCarta.QUATRO, ValorCarta.DOIS);
            List<Naipe> naipes = Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Mao mao1 = criarMao(valores, naipes);

            List<ValorCarta> valoresReverso = new ArrayList<>(valores);
            Collections.reverse(valoresReverso);
            List<Naipe> naipesReverso = new ArrayList<>(naipes);
            Collections.reverse(naipesReverso);
            Mao mao2 = criarMao(valoresReverso, naipesReverso);

            assertEquals(mao1.classificar(), mao2.classificar());
        }
    }
}