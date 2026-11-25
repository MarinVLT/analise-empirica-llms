Você é especialista em testes unitários Java com JUnit 5. O teste abaixo ainda falha: `List.of` rejeita elementos nulos e lança NullPointerException na montagem da lista, antes de chamar o construtor, então o teste não chega a exercitar o que pretende. Corrija usando java.util.Arrays.asList para conseguir passar listas que contêm null, mantendo a verificação de NullPointerException no construtor de Mao para lista de valores nula e para lista de naipes nula. Responda apenas com o método Java corrigido, completo, com a anotação @Test, sem cercas de Markdown e sem explicações.

# Especificação

# Classificação de mão de pôquer

## API pública

Classe `Mao` no pacote `br.com.poquer`.

```java
public Mao(java.util.List<ValorCarta> valores, java.util.List<Naipe> naipes)
public ClassificacaoMao classificar() // método de Mao
```

O enum `ValorCarta` contém `AS`, `DOIS`, `TRES`, `QUATRO`, `CINCO`, `SEIS`, `SETE`, `OITO`, `NOVE`, `DEZ`, `VALETE`, `DAMA` e `REI`. O enum `Naipe` contém `COPAS`, `OUROS`, `PAUS` e `ESPADAS`.

## Entradas válidas

Passe duas listas com cinco elementos cada. `valores[i]` e `naipes[i]` formam uma carta. Os cinco pares devem ser distintos. A ordem da mão é livre, desde que valor e naipe de cada carta permaneçam juntos ao reordenar as listas.

## Resultados e exceções

Uma sequência tem cinco valores consecutivos distintos. O ás pode ocupar a primeira posição na sequência mais baixa ou a última na sequência mais alta. Em outras posições, a passagem de rei para ás quebra a sequência.

O método devolve a primeira classificação aplicável, nesta ordem de prioridade:

1. `SEQUENCIA_REAL_DO_MESMO_NAIPE` — 10–J–Q–K–A do mesmo naipe.
2. `SEQUENCIA_DO_MESMO_NAIPE` — sequência do mesmo naipe.
3. `QUADRA` — quatro cartas do mesmo valor.
4. `TRINCA_E_PAR` — três cartas de um valor e duas de outro.
5. `CINCO_DO_MESMO_NAIPE` — cinco cartas do mesmo naipe sem sequência.
6. `SEQUENCIA` — sequência com mais de um naipe.
7. `TRINCA` — três cartas do mesmo valor.
8. `DOIS_PARES` — dois pares de valores distintos.
9. `UM_PAR` — duas cartas do mesmo valor.
10. `CARTA_ALTA` — demais mãos.

Lista nula ou elemento nulo provoca `NullPointerException`. Lista com tamanho diferente de cinco ou par de valor e naipe repetido provoca `IllegalArgumentException`.


# Código de produção

```java
package br.com.poquer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mao {
    private final List<ValorCarta> valores;
    private final List<Naipe> naipes;

    public Mao(List<ValorCarta> valores, List<Naipe> naipes) {
        this.valores = List.copyOf(valores);
        this.naipes = List.copyOf(naipes);
        if (this.valores.size() != 5 || this.naipes.size() != 5) {
            throw new IllegalArgumentException("Uma mão deve conter cinco valores e cinco naipes");
        }

        Set<Map.Entry<ValorCarta, Naipe>> cartasDistintas = new HashSet<>();
        for (int indice = 0; indice < this.valores.size(); indice++) {
            if (!cartasDistintas.add(Map.entry(this.valores.get(indice), this.naipes.get(indice)))) {
                throw new IllegalArgumentException("Uma mão deve conter cinco cartas distintas");
            }
        }
    }

    /**
     * Determina a classificação de maior prioridade para as cinco cartas da mão.
     * A ordem das cartas não altera o resultado. O ás pode iniciar a sequência
     * mais baixa ou terminar a sequência mais alta.
     *
     * @return classificação da mão de pôquer
     */
    public ClassificacaoMao classificar() {
        List<Integer> valoresOrdenados = valores.stream()
                .map(ValorCarta::numero).distinct().sorted().toList();
        boolean sequenciaAltaComAs = valoresOrdenados.equals(List.of(1, 10, 11, 12, 13));
        boolean sequencia = sequenciaAltaComAs
                || (valoresOrdenados.size() == 5 && valoresOrdenados.get(4) - valoresOrdenados.get(0) == 4);
        boolean mesmoNaipe = naipes.stream().allMatch(naipe -> naipe == naipes.get(0));

        int[] frequencias = new int[14];
        for (ValorCarta valor : valores) {
            frequencias[valor.numero()]++;
        }
        int pares = 0;
        int maiorFrequencia = 0;
        for (int frequencia : frequencias) {
            if (frequencia == 2) {
                pares++;
            }
            maiorFrequencia = Math.max(maiorFrequencia, frequencia);
        }

        if (mesmoNaipe && sequenciaAltaComAs) {
            return ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE;
        }
        if (mesmoNaipe && sequencia) {
            return ClassificacaoMao.SEQUENCIA_DO_MESMO_NAIPE;
        }
        if (maiorFrequencia == 4) {
            return ClassificacaoMao.QUADRA;
        }
        if (maiorFrequencia == 3 && pares == 1) {
            return ClassificacaoMao.TRINCA_E_PAR;
        }
        if (mesmoNaipe) {
            return ClassificacaoMao.CINCO_DO_MESMO_NAIPE;
        }
        if (sequencia) {
            return ClassificacaoMao.SEQUENCIA;
        }
        if (maiorFrequencia == 3) {
            return ClassificacaoMao.TRINCA;
        }
        if (pares == 2) {
            return ClassificacaoMao.DOIS_PARES;
        }
        if (pares == 1) {
            return ClassificacaoMao.UM_PAR;
        }
        return ClassificacaoMao.CARTA_ALTA;
    }
}

```

# Método com problema

@Test
@DisplayName("Elemento nulo nas listas lança NullPointerException")
void elementoNulo() {
    List<ValorCarta> valoresComNulo = List.of(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
    List<Naipe> naipesComNulo = List.of(Naipe.COPAS, Naipe.OUROS, null, Naipe.ESPADAS, Naipe.COPAS);
    assertThrows(NullPointerException.class, () -> new Mao(valoresComNulo, List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS)));
    assertThrows(NullPointerException.class, () -> new Mao(List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO), naipesComNulo));
}
