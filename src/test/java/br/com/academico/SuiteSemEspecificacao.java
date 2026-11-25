package br.com.academico;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SuiteSemEspecificacao {

    /* ---------- validações de parâmetros ---------- */

    @Test
    @DisplayName("Frequência nula lança NullPointerException")
    void frequenciaNula() {
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, null));
    }

    @ParameterizedTest(name = "Frequência fora do intervalo [{0}] lança IllegalArgumentException")
    @MethodSource("frequenciaInvalidaProvider")
    void frequenciaInvalida(Integer freq) {
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, freq));
    }

    private static Stream<Arguments> frequenciaInvalidaProvider() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(-100),
                Arguments.of(101),
                Arguments.of(150));
    }

    @ParameterizedTest(name = "Nota nula na posição {0} lança NullPointerException")
    @MethodSource("notaNulaProvider")
    void notaNula(Integer pos) {
        Float n1 = 5f, n2 = 5f, n3 = 5f;
        if (pos == 1) n1 = null;
        if (pos == 2) n2 = null;
        if (pos == 3) n3 = null;
        final Float f1 = n1, f2 = n2, f3 = n3;
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(f1, f2, f3, 80));
    }

    private static Stream<Arguments> notaNulaProvider() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3));
    }

    @ParameterizedTest(name = "Nota inválida [{0}] lança IllegalArgumentException")
    @MethodSource("notaInvalidaProvider")
    void notaInvalida(Float nota) {
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(nota, 5f, 5f, 80));
    }

    private static Stream<Arguments> notaInvalidaProvider() {
        return Stream.of(
                Arguments.of(-0.1f),
                Arguments.of(-5f),
                Arguments.of(10.1f),
                Arguments.of(20f),
                Arguments.of(Float.NaN),
                Arguments.of(Float.POSITIVE_INFINITY),
                Arguments.of(Float.NEGATIVE_INFINITY));
    }

    /* ---------- arredondamento de notas ---------- */

    @ParameterizedTest(name = "Arredondamento HALF_UP de {0} resulta {1}")
    @MethodSource("arredondamentoProvider")
    void arredondamentoNota(Float entrada, BigDecimal esperado) {
        // usar reflexão para chamar método privado
        try {
            var method = ConsolidacaoParcial.class.getDeclaredMethod("arredondarNota", Float.class);
            method.setAccessible(true);
            BigDecimal resultado = (BigDecimal) method.invoke(null, entrada);
            assertEquals(esperado, resultado);
        } catch (Exception e) {
            fail(e);
        }
    }

    private static Stream<Arguments> arredondamentoProvider() {
        return Stream.of(
                Arguments.of(2.34f, new BigDecimal("2.3")),
                Arguments.of(2.35f, new BigDecimal("2.4")), // .5 sobe
                Arguments.of(7.05f, new BigDecimal("7.1")),
                Arguments.of(7.04f, new BigDecimal("7.0")),
                Arguments.of(9.99f, new BigDecimal("10.0")),
                Arguments.of(0f, new BigDecimal("0.0")));
    }

    /* ---------- cálculo de média e decisões ---------- */

    @Nested
    @DisplayName("Regras de situação acadêmica")
    class SituacaoTests {

        @ParameterizedTest(name = "Média < 3 e frequência < 75 → REMF")
        @MethodSource("remfProvider")
        void remf(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REMF,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> remfProvider() {
            return Stream.of(
                    Arguments.of(2f, 2f, 2f, 70), // média 2.0
                    Arguments.of(2.9f, 2.9f, 2.9f, 0));
        }

        @ParameterizedTest(name = "Média < 3 e frequência >= 75 → REP")
        @MethodSource("repProvider")
        void rep(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REP,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> repProvider() {
            return Stream.of(
                    Arguments.of(2f, 2f, 2f, 75),
                    Arguments.of(2.9f, 2.9f, 2.9f, 100));
        }

        @ParameterizedTest(name = "Média >=3 <5 e frequência <75 → REPF")
        @MethodSource("repfProvider")
        void repf(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REPF,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> repfProvider() {
            return Stream.of(
                    Arguments.of(3f, 3f, 3f, 70), // média 3.0
                    Arguments.of(4.9f, 4.9f, 4.9f, 50));
        }

        @ParameterizedTest(name = "Média >=3 <5 e frequência >=75 → REC")
        @MethodSource("recProvider")
        void rec(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REC,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> recProvider() {
            return Stream.of(
                    Arguments.of(3f, 3f, 3f, 75),
                    Arguments.of(4.9f, 4.9f, 4.9f, 100));
        }

        @ParameterizedTest(name = "Média >=5 <7, todas notas >=3, frequência >=75 → APRN")
        @MethodSource("aprnProvider")
        void aprn(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.APRN,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> aprnProvider() {
            return Stream.of(
                    Arguments.of(5f, 5f, 5f, 80),
                    Arguments.of(6.4f, 5.5f, 5.1f, 75));
        }

        @ParameterizedTest(name = "Média >=5 <7, alguma nota <3, frequência >=75 → REC")
        @MethodSource("recPorNotaBaixaProvider")
        void recPorNotaBaixa(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REC,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> recPorNotaBaixaProvider() {
            return Stream.of(
                    Arguments.of(5f, 5f, 2.9f, 80),
                    Arguments.of(2.5f, 6f, 6f, 90));
        }

        @ParameterizedTest(name = "Média >=7 e frequência >=75 → APR")
        @MethodSource("aprProvider")
        void apr(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.APR,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> aprProvider() {
            return Stream.of(
                    Arguments.of(7f, 7f, 7f, 75),
                    Arguments.of(8.5f, 9f, 7.2f, 100));
        }

        @ParameterizedTest(name = "Média >=7 e frequência <75 → REPF")
        @MethodSource("repfAposMediaAltaProvider")
        void repfAposMediaAlta(Float n1, Float n2, Float n3, Integer freq) {
            assertEquals(SituacaoAcademica.REPF,
                    ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq));
        }

        private static Stream<Arguments> repfAposMediaAltaProvider() {
            return Stream.of(
                    Arguments.of(7f, 7f, 7f, 70),
                    Arguments.of(9f, 8f, 7f, 0));
        }

        /* ---------- testes de limites de média ---------- */

        @Test
        @DisplayName("Média exatamente 3 com frequência <75 → REPF")
        void limiteMedia3FrequenciaBaixa() {
            assertEquals(SituacaoAcademica.REPF,
                    ConsolidacaoParcial.consolidarParcialmente(3f, 3f, 3f, 70));
        }

        @Test
        @DisplayName("Média exatamente 3 com frequência >=75 → REC")
        void limiteMedia3FrequenciaAlta() {
            assertEquals(SituacaoAcademica.REC,
                    ConsolidacaoParcial.consolidarParcialmente(3f, 3f, 3f, 75));
        }

        @Test
        @DisplayName("Média exatamente 5 com todas notas >=3 e frequência >=75 → APRN")
        void limiteMedia5TodasNotasOk() {
            assertEquals(SituacaoAcademica.APRN,
                    ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, 80));
        }

        @Test
        @DisplayName("Média exatamente 5 com nota <3 → REC")
        void limiteMedia5NotaBaixa() {
            assertEquals(SituacaoAcademica.REC,
                    ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 2.9f, 80));
        }

        @Test
        @DisplayName("Média exatamente 7 com frequência >=75 → APR")
        void limiteMedia7FrequenciaAlta() {
            assertEquals(SituacaoAcademica.APR,
                    ConsolidacaoParcial.consolidarParcialmente(7f, 7f, 7f, 75));
        }

        @Test
        @DisplayName("Média exatamente 7 com frequência <75 → REPF")
        void limiteMedia7FrequenciaBaixa() {
            assertEquals(SituacaoAcademica.REPF,
                    ConsolidacaoParcial.consolidarParcialmente(7f, 7f, 7f, 70));
        }
    }
}