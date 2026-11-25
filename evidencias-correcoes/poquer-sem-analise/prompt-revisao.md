Você é especialista em testes unitários Java com JUnit 5. A suíte abaixo foi gerada por você e executada. Alguns testes falharam porque o resultado esperado está incorreto. Corrija somente o que for necessário para que cada teste falho passe, de acordo com a especificação e o código de produção. Não altere os testes que já passam, não remova testes e não enfraqueça as asserções. Responda somente com o código Java completo da suíte corrigida, com pacote e imports, sem cercas de Markdown e sem explicações.

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

`ClassificacaoMao.java`

```java
package br.com.poquer;

public enum ClassificacaoMao {
    SEQUENCIA_REAL_DO_MESMO_NAIPE, SEQUENCIA_DO_MESMO_NAIPE, QUADRA, TRINCA_E_PAR, CINCO_DO_MESMO_NAIPE,
    SEQUENCIA, TRINCA, DOIS_PARES, UM_PAR, CARTA_ALTA
}

```
`Mao.java`

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
`Naipe.java`

```java
package br.com.poquer;

public enum Naipe {
    COPAS, OUROS, PAUS, ESPADAS
}

```
`ValorCarta.java`

```java
package br.com.poquer;

public enum ValorCarta {
    AS(1), DOIS(2), TRES(3), QUATRO(4), CINCO(5), SEIS(6), SETE(7),
    OITO(8), NOVE(9), DEZ(10), VALETE(11), DAMA(12), REI(13);

    private final int numero;

    ValorCarta(int numero) {
        this.numero = numero;
    }

    int numero() {
        return numero;
    }
}

```

# Falhas observadas

- br.com.poquer.SuiteSemAnalise$Validacao.elementoNulo: java.lang.NullPointerException — 

# Suíte atual

package br.com.poquer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SuiteSemAnalise {

    // Helper to create a hand
    private Mao criarMao(List<ValorCarta> valores, List<Naipe> naipes) {
        return new Mao(valores, naipes);
    }

    @Nested
    @DisplayName("Classificações de mão")
    class Classificacao {

        @Test
        @DisplayName("Sequência real do mesmo naipe (10 J Q K A)")
        void sequenciaRealMesmoNaipe() {
            Mao mao = criarMao(
                    List.of(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS),
                    List.of(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA_REAL_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        @DisplayName("Sequência do mesmo naipe (5 6 7 8 9)")
        void sequenciaMesmoNaipe() {
            Mao mao = criarMao(
                    List.of(ValorCarta.CINCO, ValorCarta.SEIS, ValorCarta.SETE, ValorCarta.OITO, ValorCarta.NOVE),
                    List.of(Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS, Naipe.OUROS));
            assertEquals(ClassificacaoMao.SEQUENCIA_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        @DisplayName("Quadra")
        void quadra() {
            Mao mao = criarMao(
                    List.of(ValorCarta.REI, ValorCarta.REI, ValorCarta.REI, ValorCarta.REI, ValorCarta.DOIS),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.QUADRA, mao.classificar());
        }

        @Test
        @DisplayName("Trinca e par (Full House)")
        void trincaEPar() {
            Mao mao = criarMao(
                    List.of(ValorCarta.AS, ValorCarta.AS, ValorCarta.AS, ValorCarta.DEZ, ValorCarta.DEZ),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.COPAS, Naipe.OUROS));
            assertEquals(ClassificacaoMao.TRINCA_E_PAR, mao.classificar());
        }

        @Test
        @DisplayName("Cinco do mesmo naipe sem sequência")
        void cincoMesmoNaipe() {
            Mao mao = criarMao(
                    List.of(ValorCarta.AS, ValorCarta.TRES, ValorCarta.CINCO, ValorCarta.SETE, ValorCarta.NOVE),
                    List.of(Naipe.ESPADAS, Naipe.ESPADAS, Naipe.ESPADAS, Naipe.ESPADAS, Naipe.ESPADAS));
            assertEquals(ClassificacaoMao.CINCO_DO_MESMO_NAIPE, mao.classificar());
        }

        @Test
        @DisplayName("Sequência misturando naipes")
        void sequenciaDiversosNaipe() {
            Mao mao = criarMao(
                    List.of(ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }

        @Test
        @DisplayName("Trinca sem par")
        void trinca() {
            Mao mao = criarMao(
                    List.of(ValorCarta.VALETE, ValorCarta.VALETE, ValorCarta.VALETE, ValorCarta.DOIS, ValorCarta.TRES),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.COPAS, Naipe.OUROS));
            assertEquals(ClassificacaoMao.TRINCA, mao.classificar());
        }

        @Test
        @DisplayName("Dois pares")
        void doisPares() {
            Mao mao = criarMao(
                    List.of(ValorCarta.CINCO, ValorCarta.CINCO, ValorCarta.NOVE, ValorCarta.NOVE, ValorCarta.AS),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.DOIS_PARES, mao.classificar());
        }

        @Test
        @DisplayName("Um par")
        void umPar() {
            Mao mao = criarMao(
                    List.of(ValorCarta.SEIS, ValorCarta.SEIS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.UM_PAR, mao.classificar());
        }

        @Test
        @DisplayName("Carta alta")
        void cartaAlta() {
            Mao mao = criarMao(
                    List.of(ValorCarta.AS, ValorCarta.TRES, ValorCarta.CINCO, ValorCarta.SETE, ValorCarta.NOVE),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.CARTA_ALTA, mao.classificar());
        }

        @Test
        @DisplayName("Sequência baixa A-2-3-4-5")
        void sequenciaBaixaA2345() {
            Mao mao = criarMao(
                    List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }

        @Test
        @DisplayName("Sequência alta 10-J-Q-K-A sem mesmo naipe")
        void sequenciaAltaSemMesmoNaipe() {
            Mao mao = criarMao(
                    List.of(ValorCarta.DEZ, ValorCarta.VALETE, ValorCarta.DAMA, ValorCarta.REI, ValorCarta.AS),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.SEQUENCIA, mao.classificar());
        }
    }

    @Nested
    @DisplayName("Validação de argumentos")
    class Validacao {

        @Test
        @DisplayName("Lista nula lança NullPointerException")
        void listaNula() {
            assertThrows(NullPointerException.class, () -> new Mao(null, List.of(Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS, Naipe.COPAS)));
            assertThrows(NullPointerException.class, () -> new Mao(List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO), null));
        }

        @Test
        @DisplayName("Elemento nulo nas listas lança NullPointerException")
        void elementoNulo() {
            List<ValorCarta> valoresComNulo = List.of(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
            List<Naipe> naipesComNulo = List.of(Naipe.COPAS, Naipe.OUROS, null, Naipe.ESPADAS, Naipe.COPAS);
            assertThrows(NullPointerException.class, () -> new Mao(valoresComNulo, List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS)));
            assertThrows(NullPointerException.class, () -> new Mao(List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO), naipesComNulo));
        }

        @Test
        @DisplayName("Tamanho diferente de 5 lança IllegalArgumentException")
        void tamanhoInvalido() {
            List<ValorCarta> quatro = List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> seis = List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS, Naipe.OUROS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(quatro, List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS)));
            assertThrows(IllegalArgumentException.class, () -> new Mao(List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO, ValorCarta.SEIS), seis));
        }

        @Test
        @DisplayName("Cartas duplicadas lançam IllegalArgumentException")
        void cartasDuplicadas() {
            List<ValorCarta> valores = List.of(ValorCarta.AS, ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO);
            List<Naipe> naipes = List.of(Naipe.COPAS, Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS);
            assertThrows(IllegalArgumentException.class, () -> new Mao(valores, naipes));
        }

        @Test
        @DisplayName("Mesmo valor com naipes diferentes é permitido")
        void mesmoValorDiferentesNaipe() {
            // Não deve lançar exceção, apenas classificar como par ou mais
            Mao mao = criarMao(
                    List.of(ValorCarta.REI, ValorCarta.REI, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO),
                    List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS));
            assertEquals(ClassificacaoMao.UM_PAR, mao.classificar());
        }
    }
}
