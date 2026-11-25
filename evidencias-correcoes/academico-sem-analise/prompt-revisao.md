Você é especialista em testes unitários Java com JUnit 5. A suíte abaixo foi gerada por você e executada. Alguns testes falharam porque o resultado esperado está incorreto. Corrija somente o que for necessário para que cada teste falho passe, de acordo com a especificação e o código de produção. Não altere os testes que já passam, não remova testes e não enfraqueça as asserções. Responda somente com o código Java completo da suíte corrigida, com pacote e imports, sem cercas de Markdown e sem explicações.

# Especificação

# Consolidação parcial acadêmica

## API pública

Classe `ConsolidacaoParcial` no pacote `br.com.academico`.

```java
public static SituacaoAcademica consolidarParcialmente(
    Float nota1, Float nota2, Float nota3, Integer frequencia)
```

O enum de retorno `SituacaoAcademica` contém `REMF`, `REPF`, `REP`, `APR`, `APRN` e `REC`.

## Entradas válidas

As três notas devem ser finitas e pertencer a [0, 10]. A frequência, em porcentagem inteira já calculada sobre a base aplicável, deve pertencer a [0, 100].

## Resultados e exceções

Cada nota é arredondada para uma casa decimal com `HALF_UP`. A média aritmética dessas três notas também é arredondada para uma casa decimal com `HALF_UP`. As decisões usam os valores arredondados.

| Condição | `SituacaoAcademica` |
|---|---|
| Frequência abaixo de 75% e média menor que 3 | `REMF` — reprovado por média e faltas |
| Frequência abaixo de 75% e média de pelo menos 3 | `REPF` — reprovado por faltas |
| Frequência de pelo menos 75% e média menor que 3 | `REP` — reprovado por média |
| Frequência de pelo menos 75% e média de pelo menos 7 | `APR` — aprovado por média |
| Frequência de pelo menos 75%, média de 5 até abaixo de 7 e todas as notas de pelo menos 3 | `APRN` — aprovado por notas |
| Frequência de pelo menos 75% e demais casos com média de pelo menos 3 | `REC` — em recuperação |

Para entradas com um único argumento inválido, nulos provocam `NullPointerException` e valores fora do domínio, incluindo notas não finitas, provocam `IllegalArgumentException`.


# Código de produção

`ConsolidacaoParcial.java`

```java
package br.com.academico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ConsolidacaoParcial {
    /**
     * Determina a situação acadêmica parcial com base nas notas e na frequência.
     * As notas e a média são arredondadas para uma casa decimal com arredondamento
     * de metade para cima antes da aplicação das regras de aprovação.
     *
     * @param nota1 nota da primeira unidade, entre 0 e 10
     * @param nota2 nota da segunda unidade, entre 0 e 10
     * @param nota3 nota da terceira unidade, entre 0 e 10
     * @param frequencia frequência percentual inteira, entre 0 e 100
     * @return situação acadêmica após a consolidação parcial
     * @throws NullPointerException se algum argumento for nulo
     * @throws IllegalArgumentException se uma nota for não finita ou algum valor estiver fora da faixa
     */
    public static SituacaoAcademica consolidarParcialmente(
            Float nota1, Float nota2, Float nota3, Integer frequencia) {
        Objects.requireNonNull(frequencia, "frequencia");
        if (frequencia < 0 || frequencia > 100) {
            throw new IllegalArgumentException("Frequência deve estar entre 0 e 100");
        }

        BigDecimal primeira = arredondarNota(nota1);
        BigDecimal segunda = arredondarNota(nota2);
        BigDecimal terceira = arredondarNota(nota3);
        BigDecimal media = primeira.add(segunda).add(terceira)
                .divide(BigDecimal.valueOf(3), 1, RoundingMode.HALF_UP);

        if (media.compareTo(BigDecimal.valueOf(3)) < 0) {
            return frequencia < 75 ? SituacaoAcademica.REMF : SituacaoAcademica.REP;
        }
        if (frequencia < 75) {
            return SituacaoAcademica.REPF;
        }
        if (media.compareTo(BigDecimal.valueOf(7)) >= 0) {
            return SituacaoAcademica.APR;
        }
        BigDecimal menorNota = primeira.min(segunda).min(terceira);
        if (media.compareTo(BigDecimal.valueOf(5)) >= 0 && menorNota.compareTo(BigDecimal.valueOf(3)) >= 0) {
            return SituacaoAcademica.APRN;
        }
        return SituacaoAcademica.REC;
    }

    private static BigDecimal arredondarNota(Float nota) {
        Objects.requireNonNull(nota, "nota");
        if (!Float.isFinite(nota) || nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve ser finita e estar entre 0 e 10");
        }
        return new BigDecimal(nota.toString()).setScale(1, RoundingMode.HALF_UP);
    }
}

```
`SituacaoAcademica.java`

```java
package br.com.academico;

public enum SituacaoAcademica {
    REPF, REP, REC, APR, APRN, REMF
}

```

# Falhas observadas

- br.com.academico.SuiteSemAnalise$MediaArredondamento.arredondaMediaCorretamente: org.opentest4j.AssertionFailedError — expected: <REMF> but was: <REP>
- br.com.academico.SuiteSemAnalise$MediaArredondamento.arredondaNotasCorretamente: org.opentest4j.AssertionFailedError — expected: <REC> but was: <APRN>

# Suíte atual

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
            assertEquals(SituacaoAcademica.REC, situacao);
        }

        @Test
        void arredondaMediaCorretamente() {
            // notas 2.94, 2.94, 2.94 -> 2.9 cada, média 2.9
            SituacaoAcademica situacao = call(2.94f, 2.94f, 2.94f, 80);
            assertEquals(SituacaoAcademica.REMF, situacao);
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
