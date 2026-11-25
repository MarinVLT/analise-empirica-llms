Você é especialista em testes unitários Java com JUnit 5. Use o material fornecido para projetar os casos de teste, escolher as entradas e determinar os resultados ou exceções esperados. Responda somente com o código Java completo da suíte JUnit 5, incluindo o pacote e os imports necessários. Entregue o código em texto puro, sem cercas de Markdown nem explicações.

# Custo de locação de carro

## API pública

Classe `CalculadoraCustoLocacao` no pacote `br.com.aluguelcarros`. O tipo `BigDecimal` é de `java.math`.

```java
public CalculadoraCustoLocacao()
public BigDecimal calcularCustoLocacao(CategoriaVeiculo categoria, NivelCliente nivelCliente,
    int numeroDiarias, int quilometrosRodados, int horasAtraso, TipoSeguro seguro,
    boolean possuiAtrasoAnterior)
```

- `CategoriaVeiculo` — `ECONOMICO`, `INTERMEDIARIO`, `SUV`.
- `NivelCliente` — `COMUM`, `PRATA`, `OURO`.
- `TipoSeguro` — `SEM_SEGURO`, `BASICO`, `COMPLETO`.

## Código

`src/main/java/br/com/aluguelcarros/CalculadoraCustoLocacao.java`

```java
package br.com.aluguelcarros;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraCustoLocacao
{
    public BigDecimal calcularCustoLocacao(CategoriaVeiculo categoria, NivelCliente nivelCliente, int numeroDiarias, int quilometrosRodados, int horasAtraso, TipoSeguro seguro, boolean possuiAtrasoAnterior)
    {
        if (categoria == null || nivelCliente == null || seguro == null)
        {
            throw new NullPointerException("Argumentos não podem ser nulos");
        }

        if (numeroDiarias <= 0)
        {
            throw new IllegalArgumentException("Número de diárias deve ser maior que zero");
        }

        if (quilometrosRodados < 0)
        {
            throw new IllegalArgumentException("Quilometragem rodada não pode ser negativa");
        }

        if (horasAtraso < 0)
        {
            throw new IllegalArgumentException("Horas de atraso não podem ser negativas");
        }

        BigDecimal valorDiaria;
        if (categoria == CategoriaVeiculo.ECONOMICO)
        {
            valorDiaria = new BigDecimal("120.00");
        }
        else if (categoria == CategoriaVeiculo.INTERMEDIARIO)
        {
            valorDiaria = new BigDecimal("180.00");
        }
        else
        {
            valorDiaria = new BigDecimal("280.00");
        }

        BigDecimal taxaDescontoDuracao;
        if (numeroDiarias <= 2)
        {
            taxaDescontoDuracao = BigDecimal.ZERO;
        }
        else if (numeroDiarias <= 6)
        {
            taxaDescontoDuracao = new BigDecimal("0.05");
        }
        else if (numeroDiarias <= 14)
        {
            taxaDescontoDuracao = new BigDecimal("0.10");
        }
        else
        {
            taxaDescontoDuracao = new BigDecimal("0.15");
        }

        BigDecimal valorBrutoDiarias = valorDiaria.multiply(BigDecimal.valueOf(numeroDiarias));
        BigDecimal descontoDuracao = valorBrutoDiarias.multiply(taxaDescontoDuracao);
        BigDecimal valorDiariasComDesconto = valorBrutoDiarias.subtract(descontoDuracao);

        // Cada condição representa um requisito independente para o benefício.
        BigDecimal taxaDescontoFidelidade;
        if (nivelCliente == NivelCliente.PRATA && numeroDiarias >= 7 && !possuiAtrasoAnterior)
        {
            taxaDescontoFidelidade = new BigDecimal("0.05");
        }
        else if (nivelCliente == NivelCliente.OURO && numeroDiarias >= 5 && !possuiAtrasoAnterior)
        {
            taxaDescontoFidelidade = new BigDecimal("0.10");
        }
        else
        {
            taxaDescontoFidelidade = BigDecimal.ZERO;
        }

        BigDecimal descontoFidelidade = valorDiariasComDesconto.multiply(taxaDescontoFidelidade);
        BigDecimal valorFinalDiarias = valorDiariasComDesconto.subtract(descontoFidelidade);

        // O uso de long evita estouro ao multiplicar uma quantidade grande de diárias.
        long franquiaQuilometros = 100L * numeroDiarias;
        long quilometrosExcedentes = (long) quilometrosRodados - franquiaQuilometros;
        BigDecimal custoQuilometragem;
        if (quilometrosExcedentes <= 0)
        {
            custoQuilometragem = BigDecimal.ZERO;
        }
        else
        {
            BigDecimal tarifaQuilometrica;
            if (quilometrosExcedentes <= 100)
            {
                tarifaQuilometrica = new BigDecimal("0.80");
            }
            else if (quilometrosExcedentes <= 300)
            {
                tarifaQuilometrica = new BigDecimal("1.00");
            }
            else
            {
                tarifaQuilometrica = new BigDecimal("1.50");
            }
            custoQuilometragem = BigDecimal.valueOf(quilometrosExcedentes).multiply(tarifaQuilometrica);
        }

        BigDecimal valorSeguroDiaria;
        if (seguro == TipoSeguro.SEM_SEGURO)
        {
            valorSeguroDiaria = BigDecimal.ZERO;
        }
        else if (seguro == TipoSeguro.BASICO)
        {
            valorSeguroDiaria = new BigDecimal("25.00");
        }
        else
        {
            valorSeguroDiaria = new BigDecimal("45.00");
        }
        BigDecimal custoSeguro = valorSeguroDiaria.multiply(BigDecimal.valueOf(numeroDiarias));

        BigDecimal custoAtraso;
        if (horasAtraso <= 1)
        {
            custoAtraso = BigDecimal.ZERO;
        }
        else if (horasAtraso <= 3)
        {
            custoAtraso = valorDiaria.multiply(new BigDecimal("0.20")).multiply(BigDecimal.valueOf(horasAtraso));
        }
        else
        {
            custoAtraso = valorDiaria;
        }

        BigDecimal total = valorFinalDiarias.add(custoQuilometragem).add(custoSeguro).add(custoAtraso);
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
```

`src/main/java/br/com/aluguelcarros/CategoriaVeiculo.java`

```java
package br.com.aluguelcarros;

public enum CategoriaVeiculo
{
    ECONOMICO,
    INTERMEDIARIO,
    SUV
}
```

`src/main/java/br/com/aluguelcarros/NivelCliente.java`

```java
package br.com.aluguelcarros;

public enum NivelCliente
{
    COMUM,
    PRATA,
    OURO
}
```

`src/main/java/br/com/aluguelcarros/TipoSeguro.java`

```java
package br.com.aluguelcarros;

public enum TipoSeguro
{
    SEM_SEGURO,
    BASICO,
    COMPLETO
}
```

## Especificação

### Entradas válidas

Informe categoria, nível e seguro. O número de diárias deve ser positivo. Quilometragem rodada e horas de atraso devem ser inteiros não negativos. `possuiAtrasoAnterior` indica se o cliente tem registro de atraso anterior.

### Resultados e exceções

O total em reais soma diárias após descontos, quilometragem excedente, seguro e atraso. O retorno é um `BigDecimal` com duas casas decimais e arredondamento `HALF_UP`.

| Componente | Regra |
|---|---|
| Diária | `ECONOMICO` R$ 120, `INTERMEDIARIO` R$ 180, `SUV` R$ 280. |
| Desconto por duração | Sobre as diárias brutas: 1–2 → 0%, 3–6 → 5%, 7–14 → 10%, 15 ou mais → 15%. |
| Fidelidade | Sobre as diárias após o desconto por duração: `PRATA` 5% a partir de 7 diárias, `OURO` 10% a partir de 5. `COMUM` ou cliente com atraso anterior recebe 0%. |
| Quilometragem | Franquia de 100 km por diária. Cobra-se todo o excedente a uma única tarifa: até 100 km → R$ 0,80/km, 101–300 km → R$ 1,00/km, acima de 300 km → R$ 1,50/km. |
| Seguro por diária | `SEM_SEGURO` R$ 0, `BASICO` R$ 25, `COMPLETO` R$ 45. |
| Atraso | 0–1 h → isento, 2–3 h → 20% da diária sem desconto por hora, 4 h ou mais → uma diária sem desconto. |

Categoria, nível ou seguro nulo geram `NullPointerException`. Com esses argumentos presentes, menos de uma diária, quilometragem negativa ou atraso negativo geram `IllegalArgumentException`.

## Análise

# Critérios de teste — aluguel



### Análise caixa-preta do grupo


#### Classes de equivalência


| Parâmetro ou regra | Classe de equivalência | Domínio ou condição |
|---|---|---|
| Categoria | Válida econômico | `ECONOMICO`; diária de R$ 120,00 |
| Categoria | Válida intermediário | `INTERMEDIARIO`; diária de R$ 180,00 |
| Categoria | Válida SUV | `SUV`; diária de R$ 280,00 |
| Categoria | Inválida nula | Categoria nula; `NullPointerException` |
| Nível do cliente | Válido comum | `COMUM`; nunca recebe desconto de fidelidade |
| Nível do cliente | Válido prata | `PRATA`; desconto condicionado a pelo menos 7 diárias e ausência de atraso anterior |
| Nível do cliente | Válido ouro | `OURO`; desconto condicionado a pelo menos 5 diárias e ausência de atraso anterior |
| Nível do cliente | Inválido nulo | Nível nulo; `NullPointerException` |
| Seguro | Válido sem seguro | `SEM_SEGURO`; R$ 0,00 por diária |
| Seguro | Válido básico | `BASICO`; R$ 25,00 por diária |
| Seguro | Válido completo | `COMPLETO`; R$ 45,00 por diária |
| Seguro | Inválido nulo | Seguro nulo; `NullPointerException` |
| Número de diárias | Curta duração | Inteiros de 1 a 2; desconto por duração de 0% |
| Número de diárias | Duração intermediária baixa | Inteiros de 3 a 6; desconto por duração de 5% |
| Número de diárias | Duração intermediária alta | Inteiros de 7 a 14; desconto por duração de 10% |
| Número de diárias | Longa duração | Inteiros de 15 em diante; desconto por duração de 15% |
| Número de diárias | Inválida não positiva | Inteiros menores ou iguais a zero; `IllegalArgumentException` |
| Quilometragem | Dentro da franquia | De 0 até 100 quilômetros por diária; excedente nulo e custo de quilometragem zero |
| Quilometragem | Excedente curto | Excedente de 1 a 100 quilômetros; todo o excedente tarifado a R$ 0,80 por quilômetro |
| Quilometragem | Excedente médio | Excedente de 101 a 300 quilômetros; todo o excedente tarifado a R$ 1,00 por quilômetro |
| Quilometragem | Excedente longo | Excedente acima de 300 quilômetros; todo o excedente tarifado a R$ 1,50 por quilômetro |
| Quilometragem | Inválida negativa | Quilometragem menor que zero; `IllegalArgumentException` |
| Horas de atraso | Isento | 0 ou 1 hora; custo de atraso zero |
| Horas de atraso | Atraso proporcional | 2 ou 3 horas; 20% da diária sem desconto multiplicado pelas horas de atraso |
| Horas de atraso | Atraso máximo | 4 horas ou mais; cobrança de uma diária sem desconto, independente do número de horas |
| Horas de atraso | Inválida negativa | Horas de atraso menores que zero; `IllegalArgumentException` |
| Atraso anterior | Sem registro | Falso; não impede o desconto de fidelidade |
| Atraso anterior | Com registro | Verdadeiro; zera o desconto de fidelidade de prata e ouro |
| Resultado | Total arredondado | Soma dos componentes com duas casas decimais e arredondamento `HALF_UP` |

#### Valores-limite


| Parâmetro ou regra | Valor-limite | Classes separadas e inclusão do limite |
|---|---|---|
| Número de diárias | 1 | Menor diária válida; 0 e negativos são inválidos |
| Desconto por duração | 2 e 3 | 2 pertence à faixa de 0%; 3 pertence à faixa de 5% |
| Desconto por duração | 6 e 7 | 6 pertence à faixa de 5%; 7 pertence à faixa de 10% |
| Desconto por duração | 14 e 15 | 14 pertence à faixa de 10%; 15 pertence à faixa de 15% |
| Fidelidade prata | 7 | 7 diárias ou mais recebem 5%, se não houver atraso anterior; 6 ou menos não recebem |
| Fidelidade ouro | 5 | 5 diárias ou mais recebem 10%, se não houver atraso anterior; 4 ou menos não recebem |
| Franquia de quilometragem | 100 quilômetros por diária | Excedente igual a zero está dentro da franquia e não é cobrado |
| Excedente de quilometragem | 1 | Primeiro quilômetro excedente, tarifa de R$ 0,80 |
| Excedente de quilometragem | 100 e 101 | 100 pertence à faixa de R$ 0,80; 101 pertence à faixa de R$ 1,00 |
| Excedente de quilometragem | 300 e 301 | 300 pertence à faixa de R$ 1,00; 301 pertence à faixa de R$ 1,50 |
| Horas de atraso | 1 e 2 | 1 é isento; 2 inicia a cobrança de 20% da diária por hora |
| Horas de atraso | 3 e 4 | 3 ainda é proporcional; 4 inicia a cobrança de uma diária inteira |
| Arredondamento | Terceira casa decimal igual a 5 | Valores com terceira casa maior ou igual a 5 sobem; menores são truncados na segunda casa |

#### Tabela de decisão


| Regra | Combinação de condições | Ação prevista na especificação |
|---|---|---|
| LOC-01 | Categoria nula ou nível nulo ou seguro nulo | Lançar `NullPointerException` antes de qualquer cálculo |
| LOC-02 | Diárias não positivas | Lançar `IllegalArgumentException` |
| LOC-03 | Quilometragem negativa | Lançar `IllegalArgumentException` |
| LOC-04 | Atraso negativo | Lançar `IllegalArgumentException` |
| LOC-05 | Categoria econômica ou intermediária ou utilitária | Aplicar a diária correspondente: 120, 180 ou 280 |
| LOC-06 | Diárias de 1 a 2 ou de 3 a 6 ou de 7 a 14 ou de 15 em diante | Aplicar desconto por duração de 0%, 5%, 10% ou 15% sobre as diárias brutas |
| LOC-07 | Nível prata, pelo menos 7 diárias e sem atraso anterior | Aplicar 5% de fidelidade sobre as diárias já descontadas pela duração |
| LOC-08 | Nível ouro, pelo menos 5 diárias e sem atraso anterior | Aplicar 10% de fidelidade sobre as diárias já descontadas pela duração |
| LOC-09 | Nível comum, ou diárias abaixo do mínimo do nível, ou atraso anterior registrado | Não aplicar desconto de fidelidade |
| LOC-10 | Excedente de quilometragem nulo ou negativo | Não cobrar quilometragem |
| LOC-11 | Excedente de 1 a 100 ou de 101 a 300 ou acima de 300 | Cobrar todo o excedente a R$ 0,80, R$ 1,00 ou R$ 1,50 por quilômetro, respectivamente |
| LOC-12 | Seguro sem seguro, básico ou completo | Somar R$ 0,00, R$ 25,00 ou R$ 45,00 por diária |
| LOC-13 | Atraso de 0 a 1 hora ou de 2 a 3 horas ou de 4 horas em diante | Somar zero, 20% da diária por hora ou uma diária, sem desconto |
| LOC-14 | Todos os argumentos válidos | Somar diárias, quilometragem, seguro e atraso, e arredondar para duas casas com `HALF_UP` |

### Análise caixa-branca do grupo


| Método ou trecho | Decisão, condição ou caminho | Obrigação de cobertura |
|---|---|---|
| `calcularCustoLocacao`, guarda de nulos | Condição composta `categoria == null \|\| nivelCliente == null \|\| seguro == null`, avaliada da esquerda para a direita com curto-circuito | Exercitar cada um dos três operandos como verdadeiro e o caminho em que todos são falsos; com categoria nula, os demais operandos não são avaliados |
| `calcularCustoLocacao`, diárias | Decisão simples `numeroDiarias <= 0` | Exercitar os resultados verdadeiro (exceção) e falso (cálculo prossegue) |
| `calcularCustoLocacao`, quilometragem negativa | Decisão simples `quilometrosRodados < 0` | Exercitar os resultados verdadeiro (exceção) e falso |
| `calcularCustoLocacao`, atraso negativo | Decisão simples `horasAtraso < 0` | Exercitar os resultados verdadeiro (exceção) e falso |
| `calcularCustoLocacao`, valor da diária | Cadeia `if`/`else if`/`else` com três ramos por categoria | Exercitar os três ramos: econômico, intermediário e utilitário |
| `calcularCustoLocacao`, desconto por duração | Cadeia `if`/`else if`/`else if`/`else` com quatro ramos | Exercitar os quatro ramos: até 2, até 6, até 14 e acima de 14 diárias |
| `calcularCustoLocacao`, fidelidade prata | Condição composta `nivelCliente == PRATA && numeroDiarias >= 7 && !possuiAtrasoAnterior`, com curto-circuito | Exercitar prata com 7 ou mais diárias e sem atraso; prata com menos de 7 diárias, quando a terceira condição não é avaliada; prata com atraso anterior, quando o benefício é negado; e nível não prata |
| `calcularCustoLocacao`, fidelidade ouro | Condição composta `nivelCliente == OURO && numeroDiarias >= 5 && !possuiAtrasoAnterior`, avaliada apenas no ramo `else` da prata | Exercitar ouro com 5 ou mais diárias e sem atraso; ouro com menos de 5 diárias; ouro com atraso anterior; e o caminho em que nenhum benefício se aplica |
| `calcularCustoLocacao`, franquia | Decisão `quilometrosExcedentes <= 0` sobre a diferença entre quilometragem e franquia | Exercitar excedente zero ou negativo (sem cobrança) e excedente positivo (cobrança) |
| `calcularCustoLocacao`, tarifa quilométrica | Cadeia `if`/`else if`/`else` com três ramos, avaliada só quando há excedente | Exercitar os ramos de excedente até 100, até 300 e acima de 300, além do caminho em que a tarifa nunca é calculada |
| `calcularCustoLocacao`, seguro | Cadeia `if`/`else if`/`else` com três ramos | Exercitar os três ramos: sem seguro, básico e completo |
| `calcularCustoLocacao`, atraso | Cadeia `if`/`else if`/`else` com três ramos | Exercitar os três ramos: até 1 hora, até 3 horas e acima de 3 horas |
| `calcularCustoLocacao`, arredondamento | `total.setScale(2, RoundingMode.HALF_UP)` no retorno | Exercitar um total com terceira casa decimal maior ou igual a 5 para verificar o arredondamento para cima e um total com terceira casa menor que 5 |
| `calcularCustoLocacao`, precedência | As guardas de nulo e de faixa executam antes de qualquer cálculo | Exercitar um caso com argumento nulo e outro argumento inválido para confirmar que a exceção lançada é a de nulo |
