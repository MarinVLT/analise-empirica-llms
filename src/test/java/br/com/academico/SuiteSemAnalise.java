package br.com.academico;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SuiteSemAnalise {

    // Helper to call method
    private SituacaoAcademica call(Float n1, Float n2, Float n3, Integer freq) {
        return ConsolidacaoParcial.consolidarParcialmente(n1, n2, n3, freq);
    }

    @Nested
    @DisplayName("Validação de argumentos nulos")
    class NullArguments {

        @Test
        void notasNulasLancamNPE() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> call(null, 5f, 5f, 80)),
                () -> assertThrows(NullPointerException.class, () -> call(5f, null, 5f, 80)),
                () -> assertThrows(NullPointerException.class, () -> call(5f, 5f, null, 80))
            );
        }

        @Test
        void frequenciaNulaLancaNPE() {
            assertThrows(NullPointerException.class, () -> call(5f, 5f, 5f, null));
        }
    }

    @Nested
    @DisplayName("Validação de domínio")
    class DomainValidation {

        @ParameterizedTest(name = "nota={0} fora do intervalo")
        @CsvSource({
            "-0.1", "-5", "10.1", "20"
        })
        void notaForaDoIntervaloLancaIAE(float nota) {
            assertThrows(IllegalArgumentException.class,
                () -> call(nota, 5f, 5f, 80));
        }

        @Test
        void notaInfinitaLancaIAE() {
            assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                    () -> call(Float.POSITIVE_INFINITY, 5f, 5f, 80)),
                () -> assertThrows(IllegalArgumentException.class,
                    () -> call(Float.NaN, 5f, 5f, 80))
            );
        }

        @ParameterizedTest(name = "frequencia={0} fora do intervalo")
        @CsvSource({
            "-1", "-10", "101", "150"
        })
        void frequenciaForaDoIntervaloLancaIAE(int freq) {
            assertThrows(IllegalArgumentException.class,
                () -> call(5f, 5f, 5f, freq));
        }
    }

    @Nested
    @DisplayName("Cálculo de média e arredondamento")
    class MediaArredondamento {

        @Test
        void arredondaNotasCorretamente() {
            // notas 4.44, 5.55, 6.66 -> arredondadas 4.4, 5.6, 6.7
            SituacaoAcademica situacao = call(4.44f, 5.55f, 6.66f, 80);
            // média (4.4+5.6+6.7)/3 = 5.566..., arredonda 5.6
            assertEquals(SituacaoAcademica.APRN, situacao);
        }

        @Test
        void arredondaMediaCorretamente() {
            // notas 2.94, 2.94, 2.94 -> 2.9 cada, média 2.9
            SituacaoAcademica situacao = call(2.94f, 2.94f, 2.94f, 80);
            assertEquals(SituacaoAcademica.REP, situacao);
        }
    }

    @Nested
    @DisplayName("Regras de negócio")
    class RegrasNegocio {

        @Test
        @DisplayName("Frequência <75 e média <3 => REMF")
        void remf() {
            assertEquals(SituacaoAcademica.REMF,
                call(2f, 2f, 2f, 70));
        }

        @Test
        @DisplayName("Frequência <75 e média >=3 => REPF")
        void repf() {
            assertEquals(SituacaoAcademica.REPF,
                call(3f, 3f, 3f, 70));
        }

        @Test
        @DisplayName("Frequência >=75 e média <3 => REP")
        void rep() {
            assertEquals(SituacaoAcademica.REP,
                call(2f, 2f, 2f, 80));
        }

        @Test
        @DisplayName("Frequência >=75 e média >=7 => APR")
        void apr() {
            assertEquals(SituacaoAcademica.APR,
                call(8f, 7f, 7f, 80));
        }

        @Test
        @DisplayName("Frequência >=75, média 5-<7, todas notas >=3 => APRN")
        void aprn() {
            assertEquals(SituacaoAcademica.APRN,
                call(5f, 5f, 5f, 80));
        }

        @Test
        @DisplayName("Frequência >=75, média >=5, mas alguma nota <3 => REC")
        void recPorNotaBaixa() {
            assertEquals(SituacaoAcademica.REC,
                call(2.9f, 6f, 6f, 80));
        }

        @Test
        @DisplayName("Frequência >=75, média >=3 e <5 => REC")
        void recMediaBaixa() {
            assertEquals(SituacaoAcademica.REC,
                call(4f, 4f, 4f, 80));
        }
    }
}