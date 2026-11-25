package br.com.poquer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuiteCompleta {

    // Helper to create a hand
    private Mao mao(List<ValorCarta> valores, List<Naipe> naipes) {
        return new Mao(valores, naipes);
    }

    @Nested
    @DisplayName("Classificações válidas")
    class ClassificacoesValidas {

        @Test
        @DisplayName("Sequência real do mesmo naipe")
        void sequenciaRealMesmoNaipe() {
            List<ValorCarta> v = List.of(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Sequência do mesmo naipe (não real)")
        void sequenciaMesmoNaipe() {
            List<ValorCarta> v = List.of(ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS, ValorCarta.SETE);
            List<Naipe> n = List.of(Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS);
            assertEquals(ClassificacaoMao.SEQUENCIA_DO_MESMO_NAIPE, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Quadra")
        void quadra() {
            List<ValorCarta> v = List.of(ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.DOIS);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.QUADRA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Trinca e par")
        void trincaEPar() {
            List<ValorCarta> v = List.of(ValorCarta.CINCO, ValorCarta.CINCO, ValorCarta.CINCO, ValorCarta.REI, ValorCarta.REI);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.COPAS, Naipe.OUROS);
            assertEquals(ClassificacaoMao.TRINCA_E_PAR, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Cinco do mesmo naipe sem sequência")
        void cincoMesmoNaipeSemSequencia() {
            List<ValorCarta> v = List.of(ValorCarta.AS, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.NOVE, ValorCarta.REI);
            List<Naipe> n = List.of(Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS, Naipe.PAUS);
            assertEquals(ClassificacaoMao.CINCO_DO_MESMO_NAIPE, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Sequência misturada")
        void sequenciaMista() {
            List<ValorCarta> v = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.SEQUENCIA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Sequência alta com Ás (não real) misturada")
        void sequenciaAltaMista() {
            List<ValorCarta> v = List.of(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.SEQUENCIA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Trinca")
        void trinca() {
            List<ValorCarta> v = List.of(ValorCarta.SETE, ValorCarta.SETE, ValorCarta.SETE, ValorCarta.DOIS, ValorCarta.CINCO);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.TRINCA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Dois pares")
        void doisPares() {
            List<ValorCarta> v = List.of(ValorCarta.QUATRO, ValorCarta.QUATRO, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.AS);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.DOIS_PARES, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Um par")
        void umPar() {
            List<ValorCarta> v = List.of(ValorCarta.DAMA, ValorCarta.DAMA, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.NOVE);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.UM_PAR, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Carta alta")
        void cartaAlta() {
            List<ValorCarta> v = List.of(ValorCarta.AS, ValorCarta.TRES, ValorCarta.SEIS, ValorCarta.NOVE, ValorCarta.REI);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Ordem das cartas não altera classificação")
        void ordemIndiferente() {
            List<ValorCarta> v1 = List.of(ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS);
            List<Naipe> n1 = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            List<ValorCarta> v2 = List.of(ValorCarta.SEIS, ValorCarta.DOIS, ValorCarta.CINCO, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> n2 = List.of(Naipe.ESPADAS, Naipe.COPAS, Naipe.COPAS, Naipe.OUROS, Naipe.PAUS);
            assertEquals(mao(v1, n1).classificar(), mao(v2, n2).classificar());
        }
    }

    @Nested
    @DisplayName("Validações de entrada")
    class ValidacoesEntrada {

        @Test
        @DisplayName("Lista de valores nula")
        void listaValoresNula() {
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Executable exec = () -> new Mao(null, naipes);
            assertThrows(NullPointerException.class, exec);
        }

        @Test
        @DisplayName("Lista de naipes nula")
        void listaNaipesNula() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            Executable exec = () -> new Mao(valores, null);
            assertThrows(NullPointerException.class, exec);
        }

        @Test
        @DisplayName("Elemento nulo em valores")
        void elementoNuloValores() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Executable exec = () -> new Mao(valores, naipes);
            assertThrows(NullPointerException.class, exec);
        }

        @Test
        @DisplayName("Elemento nulo em naipes")
        void elementoNuloNaipes() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> naipes = List.of(Naipe.COPAS, null, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Executable exec = () -> new Mao(valores, naipes);
            assertThrows(NullPointerException.class, exec);
        }

        @Test
        @DisplayName("Tamanho da lista de valores diferente de 5")
        void tamanhoInvalidoValores() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            Executable exec = () -> new Mao(valores, naipes);
            assertThrows(IllegalArgumentException.class, exec);
        }

        @Test
        @DisplayName("Tamanho da lista de naipes diferente de 5")
        void tamanhoInvalidoNaipes() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS);
            Executable exec = () -> new Mao(valores, naipes);
            assertThrows(IllegalArgumentException.class, exec);
        }

        @Test
        @DisplayName("Cartas repetidas (mesmo valor e naipe)")
        void cartasRepetidas() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS);
            Executable exec = () -> new Mao(valores, naipes);
            assertThrows(IllegalArgumentException.class, exec);
        }
    }

    @Nested
    @DisplayName("Casos limites de sequência")
    class LimitesSequencia {

        @Test
        @DisplayName("Ás como menor valor (A‑2‑3‑4‑5)")
        void sequenciaAsBaixo() {
            List<ValorCarta> v = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.SEQUENCIA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Ás fora das pontas não forma sequência")
        void asForaDasPontas() {
            List<ValorCarta> v = List.of(ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertNotEquals(ClassificacaoMao.SEQUENCIA, mao(v, n).classificar());
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Passagem de rei para ás sem sequência real")
        void reiAsSemSequenciaReal() {
            List<ValorCarta> v = List.of(ValorCarta.REI, ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertNotEquals(ClassificacaoMao.SEQUENCIA, mao(v, n).classificar());
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao(v, n).classificar());
        }

        @Test
        @DisplayName("Sequência com valores repetidos não é reconhecida")
        void sequenciaComRepeticao() {
            List<ValorCarta> v = List.of(ValorCarta.SEIS, ValorCarta.SEIS, ValorCarta.SETE, ValorCarta.OITO, ValorCarta.NOVE);
            List<Naipe> n = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
            assertEquals(ClassificacaoMao.UM_PAR, mao(v, n).classificar());
        }
    }
}
