package br.com.academico;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteCompleta {

    /* ---------- validações de argumentos ---------- */

    @Test
    @DisplayName("Frequência nula lança NullPointerException")
    void frequenciaNula() {
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, null));
    }

    @Test
    @DisplayName("Nota nula na primeira posição lança NullPointerException")
    void notaNulaPrimeira() {
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(null, 5f, 5f, 80));
    }

    @Test
    @DisplayName("Nota nula na segunda posição lança NullPointerException")
    void notaNulaSegunda() {
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, null, 5f, 80));
    }

    @Test
    @DisplayName("Nota nula na terceira posição lança NullPointerException")
    void notaNulaTerceira() {
        assertThrows(NullPointerException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, null, 80));
    }

    @Test
    @DisplayName("Frequência fora do intervalo lança IllegalArgumentException")
    void frequenciaForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, -1));
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, 101));
    }

    @Test
    @DisplayName("Nota fora do intervalo lança IllegalArgumentException")
    void notaForaDoIntervalo() {
        // primeira nota inválida, as demais não são avaliadas
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(-0.1f, 5f, 5f, 80));
        // segunda nota inválida
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 10.1f, 5f, 80));
        // terceira nota inválida
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(5f, 5f, -1f, 80));
    }

    @Test
    @DisplayName("Nota não finita lança IllegalArgumentException")
    void notaNaoFinita() {
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(Float.NaN, 5f, 5f, 80));
        assertThrows(IllegalArgumentException.class,
                () -> ConsolidacaoParcial.consolidarParcialmente(Float.POSITIVE_INFINITY, 5f, 5f, 80));
    }

    /* ---------- arredondamento de notas ---------- */

    @Test
    @DisplayName("Arredondamento HALF_UP de nota 2,34 resulta 2,3")
    void arredondamentoNotaAbaixoMeio() {
        SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2.34f, 7f, 7f, 80);
        // média = (2.3+7.0+7.0)/3 = 5.4 -> REC (freq>=75, media>=3, <5? no, >=5 && <7 mas menorNota=2.3<3)
        assertEquals(SituacaoAcademica.REC, s);
    }

    @Test
    @DisplayName("Arredondamento HALF_UP de nota 2,35 resulta 2,4")
    void arredondamentoNotaMeio() {
        SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2.35f, 7f, 7f, 80);
        // média = (2.4+7.0+7.0)/3 = 5.5 -> REC (menorNota=2.4<3)
        assertEquals(SituacaoAcademica.REC, s);
    }

    /* ---------- casos de decisão ---------- */

    @Nested
    @DisplayName("Caminhos de média < 3")
    class MediaMenorQueTres {

        @Test
        void remfFrequenciaBaixa() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2f, 2f, 2f, 70);
            assertEquals(SituacaoAcademica.REMF, s);
        }

        @Test
        void repFrequenciaAlta() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2f, 2f, 2f, 80);
            assertEquals(SituacaoAcademica.REP, s);
        }
    }

    @Nested
    @DisplayName("Caminhos de frequência < 75 e média >= 3")
    class FrequenciaBaixaMediaAlta {

        @Test
        void repfMediaEntre3e5() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(3f, 3f, 3f, 70);
            assertEquals(SituacaoAcademica.REPF, s);
        }

        @Test
        void repfMediaEntre5e7() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, 70);
            assertEquals(SituacaoAcademica.REPF, s);
        }

        @Test
        void repfMediaAcima7() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(9f, 9f, 9f, 70);
            assertEquals(SituacaoAcademica.REPF, s);
        }
    }

    @Nested
    @DisplayName("Caminhos de frequência >= 75 e média >= 7")
    class AprovaçãoPorMedia {

        @Test
        void aprMediaExata7() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(7f, 7f, 7f, 75);
            assertEquals(SituacaoAcademica.APR, s);
        }

        @Test
        void aprMediaAcima7() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(8f, 9f, 7f, 90);
            assertEquals(SituacaoAcademica.APR, s);
        }
    }

    @Nested
    @DisplayName("Caminhos de frequência >= 75 e 5 <= média < 7")
    class MediaEntre5e7 {

        @Test
        void aprnTodasNotasMaiorIgual3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, 80);
            assertEquals(SituacaoAcademica.APRN, s);
        }

        @Test
        void recAlgumaNotaAbaixo3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2.9f, 6f, 6f, 80);
            assertEquals(SituacaoAcademica.REC, s);
        }
    }

    @Nested
    @DisplayName("Caminhos de frequência >= 75 e média entre 3 e 5")
    class MediaEntre3e5 {

        @Test
        void recMediaEntre3e5() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(4f, 4f, 4f, 80);
            assertEquals(SituacaoAcademica.REC, s);
        }
    }

    @Nested
    @DisplayName("Casos de limite de frequência 75")
    class LimiteFrequencia {

        @Test
        void limiteFrequencia75MediaAbaixo3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2f, 2f, 2f, 75);
            assertEquals(SituacaoAcademica.REP, s);
        }

        @Test
        void limiteFrequencia74MediaAbaixo3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2f, 2f, 2f, 74);
            assertEquals(SituacaoAcademica.REMF, s);
        }
    }

    @Nested
    @DisplayName("Casos de limite de média")
    class LimiteMedia {

        @Test
        void limiteMedia3FrequenciaBaixa() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(3f, 3f, 3f, 70);
            assertEquals(SituacaoAcademica.REPF, s);
        }

        @Test
        void limiteMedia3FrequenciaAlta() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(3f, 3f, 3f, 80);
            assertEquals(SituacaoAcademica.REC, s);
        }

        @Test
        void limiteMedia5TodasNotasMaiorIgual3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(5f, 5f, 5f, 80);
            assertEquals(SituacaoAcademica.APRN, s);
        }

        @Test
        void limiteMedia5ComNotaAbaixo3() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(2.9f, 5f, 5f, 80);
            assertEquals(SituacaoAcademica.REC, s);
        }

        @Test
        void limiteMedia7() {
            SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(7f, 7f, 7f, 80);
            assertEquals(SituacaoAcademica.APR, s);
        }
    }

    /* ---------- verificação de arredondamento da média ---------- */

    @Test
    @DisplayName("Média 2,95 arredondada para 3,0 altera decisão")
    void mediaArredondamentoLimiteInferior() {
        // notas 2,94 -> 2,9; 2,94 -> 2,9; 2,94 -> 2,9 => média 2,9 < 3 => REMF/REP
        SituacaoAcademica s1 = ConsolidacaoParcial.consolidarParcialmente(2.94f, 2.94f, 2.94f, 70);
        assertEquals(SituacaoAcademica.REMF, s1);

        // notas 2,95 -> 3,0; 2,95 -> 3,0; 2,95 -> 3,0 => média 3,0 >=3 => REPF
        SituacaoAcademica s2 = ConsolidacaoParcial.consolidarParcialmente(2.95f, 2.95f, 2.95f, 70);
        assertEquals(SituacaoAcademica.REPF, s2);
    }

    @Test
    @DisplayName("Média 6,95 arredondada para 7,0 aprova")
    void mediaArredondamentoLimiteSuperior() {
        // notas 6,95 -> 7,0; 6,95 -> 7,0; 6,95 -> 7,0 => média 7,0 => APR
        SituacaoAcademica s = ConsolidacaoParcial.consolidarParcialmente(6.95f, 6.95f, 6.95f, 80);
        assertEquals(SituacaoAcademica.APR, s);
    }
}