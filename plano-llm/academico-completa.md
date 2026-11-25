Você é especialista em testes unitários Java com JUnit 5. Use o material fornecido para projetar os casos de teste, escolher as entradas e determinar os resultados ou exceções esperados. Responda somente com o código Java completo da suíte JUnit 5, incluindo o pacote e os imports necessários. Entregue o código em texto puro, sem cercas de Markdown nem explicações.

# Consolidação parcial acadêmica

## API pública

Classe `ConsolidacaoParcial` no pacote `br.com.academico`.

```java
public static SituacaoAcademica consolidarParcialmente(
    Float nota1, Float nota2, Float nota3, Integer frequencia)
```

O enum de retorno `SituacaoAcademica` contém `REMF`, `REPF`, `REP`, `APR`, `APRN` e `REC`.

## Código

`src/main/java/br/com/academico/ConsolidacaoParcial.java`

```java
package br.com.academico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ConsolidacaoParcial {
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

`src/main/java/br/com/academico/SituacaoAcademica.java`

```java
package br.com.academico;

public enum SituacaoAcademica {
    REPF, REP, REC, APR, APRN, REMF
}
```

## Especificação

### Entradas válidas

As três notas devem ser finitas e pertencer a [0, 10]. A frequência, em porcentagem inteira já calculada sobre a base aplicável, deve pertencer a [0, 100].

### Resultados e exceções

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

## Análise

# Critérios de teste — academico



### Análise caixa-preta do grupo


#### Classes de equivalência


| Parâmetro ou regra | Classe de equivalência | Domínio ou condição |
|---|---|---|
| Nota | Inválida abaixo do domínio | Nota < 0,00 |
| Nota | Inválida acima do domínio | Nota > 10,00 |
| Nota | Inválida nula | Nota nula |
| Frequência | Inválida abaixo do domínio | Frequência < 0% |
| Frequência | Inválida acima do domínio | Frequência > 100% |
| Frequência | Inválida nula | Frequência nula |
| Frequência | Abaixo do mínimo | Frequência < 75%; situações possíveis: REMF, REPF |
| Frequência | Mínimo atingido | Frequência >= 75%; situações possíveis: REC, APRN, APR, REP |
| Média | Abaixo de 3 | Média < 3; situações possíveis: REMF, REP |
| Média | De 3 até abaixo de 7 | Média >= 3 e média < 7; situações possíveis: REPF, REC |
| Média | De 5 até abaixo de 7 | Média >= 5 e média < 7; situação possível: APRN |
| Média | A partir de 7 | Média >= 7; situação possível: APR |
| Notas das unidades | Todas as notas a partir de 3 | Todas as notas >= 3; situação possível: APRN |
| Notas das unidades | Pelo menos uma nota abaixo de 3 | Pelo menos uma nota < 3; situação possível: REC |

Critérios que definem a situação acadêmica: frequência, média e notas (as notas individuais influenciam apenas APRN e REC). Combinações que influenciam o resultado: frequência e média; frequência, notas e média.

#### Valores-limite


| Parâmetro ou regra | Valor-limite | Classes separadas e inclusão do limite |
|---|---|---|
| Frequência | 75% | Valor-limite de frequência tanto para aprovação quanto para reprovação. |
| Média | 3 | Valor-limite de média. |
| Média | 5 | Valor-limite de média. |
| Média | 7 | Valor-limite de média. |
| Notas e média (arredondamento) | Arredondamento `HALF_UP` para uma casa decimal | Cada nota é arredondada para uma casa decimal com `HALF_UP`, e a média aritmética das três notas também é arredondada para uma casa decimal com `HALF_UP`. As decisões usam os valores arredondados. Ou seja, notas um pouco abaixo do limite passam a ser o valor do limite, o que influencia no resultado final da situação acadêmica. |

#### Tabela de decisão


| Regra | Combinação de condições | Ação prevista na especificação |
|---|---|---|
| ACA-01 | Frequência < 75 e média < 3 | REMF |
| ACA-02 | Frequência < 75 e média >= 3 e < 5 | REPF |
| ACA-03 | Frequência < 75 e média >= 5 e < 7 | REPF |
| ACA-04 | Frequência < 75 e média >= 7 | REPF |
| ACA-05 | Frequência >= 75 e média < 3 | REP |
| ACA-06 | Frequência >= 75 e média >= 3 e < 5 | REC |
| ACA-07 | Frequência >= 75, média >= 5 e < 7 e pelo menos uma nota < 3 | REC |
| ACA-08 | Frequência >= 75, média >= 5 e < 7 e todas as notas >= 3 | APRN |
| ACA-09 | Frequência >= 75 e média >= 7 | APR |

Apenas as combinações de frequência e média e de frequência, notas e média influenciam as situações acadêmicas apresentadas. APRN e REC são as situações afetadas pelos três critérios: frequência, notas e média. Com frequência >= 75 e média >= 5 e < 7, caso alguma nota das unidades seja abaixo de 3 o aluno está em REC; caso todas sejam >= 3, o aluno está em APRN.

### Análise caixa-branca do grupo


| Método ou trecho | Decisão, condição ou caminho | Obrigação de cobertura |
|---|---|---|
| `consolidarParcialmente`, frequência nula | `Objects.requireNonNull(frequencia)` antes do arredondamento das notas | Exercitar frequência nula, que gera `NullPointerException` antes de qualquer validação de nota |
| `consolidarParcialmente`, faixa da frequência | Condição composta `frequencia < 0 \|\| frequencia > 100`, com curto-circuito | Exercitar frequência negativa, que não avalia a segunda condição; frequência acima de 100; e frequência dentro da faixa |
| `arredondarNota`, nota nula | `Objects.requireNonNull(nota)` em cada uma das três chamadas, executadas na ordem das notas | Exercitar nota nula na primeira, na segunda e na terceira posição; com nota inválida anterior, as seguintes não são avaliadas |
| `arredondarNota`, domínio | Condição composta `!Float.isFinite(nota) \|\| nota < 0 \|\| nota > 10`, com curto-circuito | Exercitar nota não finita, como `NaN` ou infinito, quando as comparações seguintes não são avaliadas; nota negativa; nota acima de 10; e nota no domínio |
| `arredondarNota`, arredondamento | `new BigDecimal(nota.toString()).setScale(1, RoundingMode.HALF_UP)` | Exercitar nota com segunda casa decimal igual a 5 para confirmar a subida, e nota com segunda casa menor que 5 |
| `consolidarParcialmente`, média | Soma das três notas arredondadas dividida por três com escala 1 e `HALF_UP` | Exercitar uma média exatamente no limite inferior, uma média logo abaixo e uma logo acima de 3, de 5 e de 7 |
| `consolidarParcialmente`, média abaixo de 3 | Decisão `media < 3` com expressão ternária sobre `frequencia < 75` | Exercitar média abaixo de 3 com frequência abaixo de 75, que retorna REMF, e com frequência de pelo menos 75, que retorna REP |
| `consolidarParcialmente`, frequência abaixo de 75 | Decisão `frequencia < 75`, alcançada quando a média é de pelo menos 3 | Exercitar frequência abaixo de 75 com média de pelo menos 3, que retorna REPF, e frequência de pelo menos 75 |
| `consolidarParcialmente`, aprovação por média | Decisão `media >= 7` | Exercitar média exatamente 7, que aprova, e média logo abaixo de 7 |
| `consolidarParcialmente`, menor nota | Encadeamento `primeira.min(segunda).min(terceira)` sobre as notas já arredondadas | Exercitar mãos de notas em que a menor nota está em cada uma das três posições |
| `consolidarParcialmente`, aprovação por notas | Condição composta `media >= 5 && menorNota >= 3`, com curto-circuito | Exercitar média de 5 até abaixo de 7 com todas as notas de pelo menos 3, que retorna APRN; média abaixo de 5, quando a menor nota não é avaliada; e média nessa faixa com alguma nota abaixo de 3, que retorna REC |
| `consolidarParcialmente`, recuperação | Retorno final após todas as decisões anteriores | Exercitar o caso de média de pelo menos 3 e abaixo de 5 com frequência suficiente, e o caso de média de 5 até abaixo de 7 com alguma nota abaixo de 3 |
