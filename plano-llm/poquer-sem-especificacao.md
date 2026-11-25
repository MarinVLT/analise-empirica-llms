Você é especialista em testes unitários Java com JUnit 5. Use o material fornecido para projetar os casos de teste, escolher as entradas e determinar os resultados ou exceções esperados. Responda somente com o código Java completo da suíte JUnit 5, incluindo o pacote e os imports necessários. Entregue o código em texto puro, sem cercas de Markdown nem explicações.

# Classificação de mão de pôquer

## API pública

Classe `Mao` no pacote `br.com.poquer`.

```java
public Mao(java.util.List<ValorCarta> valores, java.util.List<Naipe> naipes)
public ClassificacaoMao classificar() // método de Mao
```

O enum `ValorCarta` contém `AS`, `DOIS`, `TRES`, `QUATRO`, `CINCO`, `SEIS`, `SETE`, `OITO`, `NOVE`, `DEZ`, `VALETE`, `DAMA` e `REI`. O enum `Naipe` contém `COPAS`, `OUROS`, `PAUS` e `ESPADAS`.

## Código

`src/main/java/br/com/poquer/ClassificacaoMao.java`

```java
package br.com.poquer;

public enum ClassificacaoMao {
    SEQUENCIA_REAL_DO_MESMO_NAIPE, SEQUENCIA_DO_MESMO_NAIPE, QUADRA, TRINCA_E_PAR, CINCO_DO_MESMO_NAIPE,
    SEQUENCIA, TRINCA, DOIS_PARES, UM_PAR, CARTA_ALTA
}
```

`src/main/java/br/com/poquer/Mao.java`

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

`src/main/java/br/com/poquer/Naipe.java`

```java
package br.com.poquer;

public enum Naipe {
    COPAS, OUROS, PAUS, ESPADAS
}
```

`src/main/java/br/com/poquer/ValorCarta.java`

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

## Análise

# Critérios de teste — poquer



### Análise caixa-preta do grupo


#### Classes de equivalência


| Parâmetro ou regra | Classe de equivalência | Domínio ou condição |
|---|---|---|
| Composição da mão | Sequência real do mesmo naipe | Dez, valete, dama, rei e ás, todos do mesmo naipe |
| Composição da mão | Sequência do mesmo naipe | Cinco valores consecutivos distintos do mesmo naipe, sem ser a sequência real |
| Composição da mão | Quadra | Quatro cartas de um mesmo valor e uma carta de outro valor |
| Composição da mão | Trinca e par | Três cartas de um valor e duas cartas de outro valor |
| Composição da mão | Cinco do mesmo naipe | Cinco cartas do mesmo naipe que não formam sequência |
| Composição da mão | Sequência | Cinco valores consecutivos distintos com mais de um naipe |
| Composição da mão | Trinca | Três cartas de um valor, sem outro par e sem sequência nem naipe único |
| Composição da mão | Dois pares | Dois pares de valores distintos, com a quinta carta de outro valor |
| Composição da mão | Um par | Um único par, com as outras três cartas de valores distintos |
| Composição da mão | Carta alta | Nenhuma das combinações anteriores |
| Sequência | Ás na posição baixa | Ás, dois, três, quatro e cinco, com mais de um naipe, classificada como sequência |
| Sequência | Ás na posição alta | Dez, valete, dama, rei e ás, com mais de um naipe, classificada como sequência |
| Sequência | Ás fora das pontas | Ás no meio de valores altos, como dama, rei, ás, dois e três, não forma sequência |
| Sequência | Passagem de rei para ás | Rei e ás como valores extremos não formam sequência quando o ás não completa a sequência real |
| Sequência | Valores repetidos | Qualquer repetição de valor impede a sequência |
| Naipe | Naipe único | As cinco cartas compartilham o mesmo naipe |
| Naipe | Naipes mistos | Ao menos duas cartas têm naipes diferentes |
| Valores | Valores distintos | As cinco cartas têm valores diferentes |
| Valores | Um par de valores | Exatamente dois valores iguais e três distintos |
| Valores | Dois pares de valores | Dois valores repetidos duas vezes e um valor distinto |
| Valores | Trinca de valores | Um valor repetido três vezes |
| Valores | Quadra de valores | Um valor repetido quatro vezes |
| Tamanho das listas | Válido | Duas listas com exatamente cinco elementos cada |
| Tamanho das listas | Inválido menor | Lista de valores ou de naipes com menos de cinco elementos; `IllegalArgumentException` |
| Tamanho das listas | Inválido maior | Lista de valores ou de naipes com mais de cinco elementos; `IllegalArgumentException` |
| Cartas | Válidas distintas | Os cinco pares de valor e naipe são distintos |
| Cartas | Inválidas repetidas | Algum par de valor e naipe aparece mais de uma vez; `IllegalArgumentException` |
| Entradas nulas | Lista nula | Lista de valores ou de naipes nula; `NullPointerException` |
| Entradas nulas | Elemento nulo | Elemento nulo em qualquer das listas; `NullPointerException` |
| Ordem das cartas | Ordem indiferente | Reordenar as duas listas mantendo os pares não altera a classificação |

#### Valores-limite


| Parâmetro ou regra | Valor-limite | Classes separadas e inclusão do limite |
|---|---|---|
| Tamanho das listas | 4 e 5 | 4 é inválido; 5 é o único tamanho válido |
| Tamanho das listas | 5 e 6 | 5 é válido; 6 é inválido |
| Sequência real | Dez como menor valor | A sequência de dez a ás é real; a de nove a rei é apenas sequência do mesmo naipe |
| Sequência com ás baixo | Ás como menor valor | A sequência de ás a cinco é válida; a de dois a seis também é sequência, mas sem o ás |
| Sequência | Cinco valores consecutivos | A diferença entre o maior e o menor valor é exatamente 4 |
| Sequência | Seis valores consecutivos | Com seis valores distintos não há mão de cinco cartas; a sequência exige exatamente as cinco cartas |
| Repetição de valor | 1, 2, 3 e 4 ocorrências | 1 ocorrência é valor isolado; 2 forma par; 3 forma trinca; 4 forma quadra |
| Repetição de valor | 5 ocorrências | Não é possível em mão válida, porque exigiria cinco cartas do mesmo valor e as cartas precisam ser distintas |
| Pares | 0, 1 e 2 pares | 0 pares não formam par; 1 par forma um par; 2 pares formam dois pares |
| Prioridade | Primeira classificação aplicável | A sequência real do mesmo naipe vence sequência do mesmo naipe, que vence quadra, que vence trinca e par, e assim por diante |

#### Tabela de decisão


| Regra | Combinação de condições | Ação prevista na especificação |
|---|---|---|
| POK-01 | Cinco cartas do mesmo naipe com dez, valete, dama, rei e ás | `SEQUENCIA_REAL_DO_MESMO_NAIPE` |
| POK-02 | Cinco cartas do mesmo naipe, sequência que não é a real | `SEQUENCIA_DO_MESMO_NAIPE` |
| POK-03 | Quatro cartas de um valor, mesmo com naipes mistos | `QUADRA` |
| POK-04 | Três cartas de um valor e duas de outro, mesmo com naipes mistos | `TRINCA_E_PAR` |
| POK-05 | Cinco cartas do mesmo naipe que não formam sequência | `CINCO_DO_MESMO_NAIPE` |
| POK-06 | Sequência com mais de um naipe, incluindo o ás nas pontas | `SEQUENCIA` |
| POK-07 | Três cartas de um valor, sem par e sem sequência nem naipe único | `TRINCA` |
| POK-08 | Dois pares de valores distintos, sem trinca nem sequência nem naipe único | `DOIS_PARES` |
| POK-09 | Um único par, sem outra combinação | `UM_PAR` |
| POK-10 | Nenhuma combinação anterior | `CARTA_ALTA` |
| POK-11 | Lista nula ou elemento nulo em qualquer das listas | `NullPointerException` |
| POK-12 | Tamanho diferente de cinco em qualquer das listas | `IllegalArgumentException` |
| POK-13 | Par de valor e naipe repetido | `IllegalArgumentException` |

### Análise caixa-branca do grupo


| Método ou trecho | Decisão, condição ou caminho | Obrigação de cobertura |
|---|---|---|
| Construtor `Mao`, cópia defensiva | `List.copyOf` sobre as duas listas recebidas | Exercitar listas válidas e listas nulas, que provocam `NullPointerException` antes das demais validações |
| Construtor `Mao`, tamanho | Condição composta `valores.size() != 5 \|\| naipes.size() != 5`, com curto-circuito | Exercitar lista de valores com tamanho errado, quando a segunda condição não é avaliada; lista de naipes com tamanho errado; e ambos com tamanho cinco |
| Construtor `Mao`, repetição | Laço sobre os cinco índices que insere cada par valor e naipe em um `HashSet` e rejeita a inserção repetida | Exercitar um par repetido na primeira posição e outro na última, e uma mão em que as cinco cartas são distintas |
| `classificar`, ordenação | `distinct`, `sorted` e coleta sobre os valores; repetições reduzem a lista resultante a menos de cinco elementos | Exercitar mãos com valores distintos e mãos com valores repetidos, que não podem ser sequência |
| `classificar`, sequência real | `valoresOrdenados.equals(List.of(1, 10, 11, 12, 13))` | Exercitar a mão real e uma mão quase real, como nove, dez, valete, dama e rei |
| `classificar`, sequência | Condição composta `sequenciaAltaComAs \|\| (valoresOrdenados.size() == 5 && valoresOrdenados.get(4) - valoresOrdenados.get(0) == 4)`, com curto-circuito | Exercitar a sequência real, quando a segunda parte não é avaliada; uma sequência comum; uma mão com valores repetidos; e uma mão não consecutiva |
| `classificar`, naipe | `naipes.stream().allMatch(...)` comparando cada naipe ao primeiro | Exercitar mão de naipe único e mão mista, em que a verificação para no primeiro naipe diferente |
| `classificar`, frequências | Vetor de frequências de tamanho 14 indexado pelo número da carta, com laço sobre os cinco valores | Exercitar os valores extremos, ás como índice 1 e rei como índice 13, para confirmar que o vetor comporta todos os valores |
| `classificar`, contagem de pares | Laço que conta frequências iguais a 2 e guarda a maior frequência | Exercitar mãos com nenhum, um e dois pares, e mãos com trinca e quadra |
| `classificar`, prioridade | Cadeia de dez decisões de retorno, na ordem da especificação | Exercitar cada classificação uma vez e também mãos que satisfazem mais de uma combinação, para confirmar a precedência |
| `classificar`, ordem das cartas | O resultado depende apenas dos conjuntos de valores e naipes | Exercitar a mesma mão em ordens diferentes e confirmar a mesma classificação |
