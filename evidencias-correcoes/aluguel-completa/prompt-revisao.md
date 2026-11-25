Você é especialista em testes unitários Java com JUnit 5. A suíte abaixo foi gerada por você e executada. Alguns testes falharam porque o resultado esperado está incorreto. Corrija somente o que for necessário para que cada teste falho passe, de acordo com a especificação e o código de produção. Não altere os testes que já passam, não remova testes e não enfraqueça as asserções. Responda somente com o código Java completo da suíte corrigida, com pacote e imports, sem cercas de Markdown e sem explicações.

# Especificação

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

## Entradas válidas

Informe categoria, nível e seguro. O número de diárias deve ser positivo. Quilometragem rodada e horas de atraso devem ser inteiros não negativos. `possuiAtrasoAnterior` indica se o cliente tem registro de atraso anterior.

## Resultados e exceções

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


# Código de produção

`CalculadoraCustoLocacao.java`

```java
package br.com.aluguelcarros;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraCustoLocacao
{
    /**
     * Calcula o custo da locação somando as diárias com descontos, o excedente
     * de quilometragem, o seguro e o atraso na devolução.
     *
     * @param categoria categoria do veículo
     * @param nivelCliente nível de fidelidade do cliente
     * @param numeroDiarias quantidade de diárias contratadas, no mínimo uma
     * @param quilometrosRodados quilometragem total, maior ou igual a zero
     * @param horasAtraso horas de atraso na devolução, maior ou igual a zero
     * @param seguro tipo de seguro contratado
     * @param possuiAtrasoAnterior indica se o cliente tem registro de atraso anterior
     * @return custo em reais, arredondado para duas casas decimais com metade para cima
     * @throws NullPointerException se a categoria, o nível do cliente ou o seguro for nulo
     * @throws IllegalArgumentException se houver menos de uma diária, quilometragem negativa ou atraso negativo
     */
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
`CategoriaVeiculo.java`

```java
package br.com.aluguelcarros;

public enum CategoriaVeiculo
{
    ECONOMICO,
    INTERMEDIARIO,
    SUV
}

```
`NivelCliente.java`

```java
package br.com.aluguelcarros;

public enum NivelCliente
{
    COMUM,
    PRATA,
    OURO
}

```
`TipoSeguro.java`

```java
package br.com.aluguelcarros;

public enum TipoSeguro
{
    SEM_SEGURO,
    BASICO,
    COMPLETO
}

```

# Falhas observadas

- br.com.aluguelcarros.SuiteCompleta.arredondamentoParaCima: org.opentest4j.AssertionFailedError — expected: <121.00> but was: <120.80>

# Suíte atual

package br.com.aluguelcarros;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteCompleta {

    private final CalculadoraCustoLocacao calc = new CalculadoraCustoLocacao();

    /* ---------- Guardas de argumentos ---------- */

    @Test
    @DisplayName("NullPointerException quando categoria é nula")
    void deveLancarNPESeCategoriaNula() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(null, NivelCliente.COMUM, 1, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("NullPointerException quando nivelCliente é nulo")
    void deveLancarNPESeNivelClienteNulo() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, null, 1, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("NullPointerException quando tipo de seguro é nulo")
    void deveLancarNPESeSeguroNulo() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 0, null, false));
    }

    @Test
    @DisplayName("IllegalArgumentException quando número de diárias <= 0")
    void deveLancarIAESeDiariasNaoPositivas() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 0, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("IllegalArgumentException quando quilometragem negativa")
    void deveLancarIAESeQuilometragemNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, -10, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("IllegalArgumentException quando horas de atraso negativas")
    void deveLancarIAESeAtrasoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, -1, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Cobertura de categorias ---------- */

    @Test
    @DisplayName("Valor da diária ECONOMICO")
    void calculaDiariaEconomicoSemDescontos() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("120.00"), total);
    }

    @Test
    @DisplayName("Valor da diária INTERMEDIARIO")
    void calculaDiariaIntermediarioSemDescontos() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("180.00"), total);
    }

    @Test
    @DisplayName("Valor da diária SUV")
    void calculaDiariaSUVSemDescontos() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("280.00"), total);
    }

    /* ---------- Desconto por duração ---------- */

    @Test
    @DisplayName("Desconto 0% para 2 diárias")
    void descontoDuracaoZero() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                2, 0, 0, TipoSeguro.SEM_SEGURO, false);
        // 2 * 120 = 240, sem desconto
        assertEquals(new BigDecimal("240.00"), total);
    }

    @Test
    @DisplayName("Desconto 5% para 4 diárias")
    void descontoDuracaoCincoPorcento() {
        // Valor bruto: 4 * 180 = 720
        // Desconto 5%: 36 => 684
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM,
                4, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("684.00"), total);
    }

    @Test
    @DisplayName("Desconto 10% para 10 diárias")
    void descontoDuracaoDezPorcento() {
        // SUV 10 diárias: 10*280=2800, 10% =280 => 2520
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM,
                10, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("2520.00"), total);
    }

    @Test
    @DisplayName("Desconto 15% para 20 diárias")
    void descontoDuracaoQuinzePorcento() {
        // ECONOMICO 20 diárias: 20*120=2400, 15% =360 => 2040
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                20, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("2040.00"), total);
    }

    /* ---------- Desconto de fidelidade ---------- */

    @Test
    @DisplayName("Desconto fidelidade PRATA 5% a partir de 7 diárias sem atraso anterior")
    void fidelidadePrataApto() {
        // Diária ECONOMICO 7 diárias = 840
        // Desconto duração (7 -> 10%): 84 => 756
        // Fidelidade 5% sobre 756 = 37.80 => 718.20
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA,
                7, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("718.20"), total);
    }

    @Test
    @DisplayName("Fidelidade PRATA não aplicada quando diárias <7")
    void fidelidadePrataNaoApto() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA,
                6, 0, 0, TipoSeguro.SEM_SEGURO, false);
        // 6 diárias, desconto duração 5%: 6*120=720, 5% =36 => 684
        assertEquals(new BigDecimal("684.00"), total);
    }

    @Test
    @DisplayName("Fidelidade PRATA não aplicada quando há atraso anterior")
    void fidelidadePrataComAtrasoAnterior() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA,
                7, 0, 0, TipoSeguro.SEM_SEGURO, true);
        // Mesmo cálculo de desconto duração, sem fidelidade
        assertEquals(new BigDecimal("756.00"), total);
    }

    @Test
    @DisplayName("Fidelidade OURO 10% a partir de 5 diárias sem atraso anterior")
    void fidelidadeOuroApto() {
        // INTERMEDIARIO 5 diárias = 5*180=900
        // Desconto duração (5 -> 5%): 45 => 855
        // Fidelidade 10% de 855 = 85.50 => 769.50
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.OURO,
                5, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("769.50"), total);
    }

    @Test
    @DisplayName("Fidelidade OURO não aplicada quando diárias <5")
    void fidelidadeOuroNaoApto() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.OURO,
                4, 0, 0, TipoSeguro.SEM_SEGURO, false);
        // 4 diárias, desconto duração 5%: 4*180=720, 5% =36 => 684
        assertEquals(new BigDecimal("684.00"), total);
    }

    @Test
    @DisplayName("Fidelidade OURO não aplicada quando há atraso anterior")
    void fidelidadeOuroComAtrasoAnterior() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.OURO,
                5, 0, 0, TipoSeguro.SEM_SEGURO, true);
        // Só desconto duração 5%
        assertEquals(new BigDecimal("855.00"), total);
    }

    /* ---------- Quilometragem ---------- */

    @Test
    @DisplayName("Quilometragem dentro da franquia (0 custo)")
    void quilometragemDentroFranquia() {
        // 3 diárias => franquia 300 km, usar 250 km
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                3, 250, 0, TipoSeguro.SEM_SEGURO, false);
        // Diárias: 3*120=360, sem desconto (3 diárias => 5%? actually 3 => 5% discount)
        // Wait: 3 diárias => desconto 5% => 360*0.05=18 => 342
        // No quilometragem charge
        assertEquals(new BigDecimal("342.00"), total);
    }

    @Test
    @DisplayName("Excedente de 50 km (tarifa 0.80)")
    void quilometragemExcedenteCurto() {
        // 2 diárias => franquia 200 km, rodados 250 => excedente 50
        // Custo km = 50 * 0.80 = 40
        // Diárias: 2*120=240, sem desconto
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                2, 250, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("280.00"), total);
    }

    @Test
    @DisplayName("Excedente de 150 km (tarifa 1.00)")
    void quilometragemExcedenteMedio() {
        // 1 diária => franquia 100 km, rodados 250 => excedente 150
        // Custo km = 150 * 1.00 = 150
        // Diária: 1*180=180 (INTERMEDIARIO)
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM,
                1, 250, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("330.00"), total);
    }

    @Test
    @DisplayName("Excedente de 350 km (tarifa 1.50)")
    void quilometragemExcedenteLongo() {
        // 1 diária => franquia 100 km, rodados 450 => excedente 350
        // Custo km = 350 * 1.50 = 525
        // Diária: 1*280=280 (SUV)
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM,
                1, 450, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("805.00"), total);
    }

    /* ---------- Seguro ---------- */

    @Test
    @DisplayName("Seguro BASICO")
    void seguroBasico() {
        // 2 diárias ECONOMICO = 240, sem descontos, seguro 2*25=50
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                2, 0, 0, TipoSeguro.BASICO, false);
        assertEquals(new BigDecimal("290.00"), total);
    }

    @Test
    @DisplayName("Seguro COMPLETO")
    void seguroCompleto() {
        // 1 diária INTERMEDIARIO =180, seguro 45
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.COMPLETO, false);
        assertEquals(new BigDecimal("225.00"), total);
    }

    /* ---------- Atraso ---------- */

    @Test
    @DisplayName("Atraso 0 hora isento")
    void atrasoIsentoZero() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("120.00"), total);
    }

    @Test
    @DisplayName("Atraso 2 horas (20% da diária por hora)")
    void atrasoProporcional() {
        // Diária 180, 2h => 180*0.20*2 = 72
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM,
                1, 0, 2, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("252.00"), total);
    }

    @Test
    @DisplayName("Atraso 4 horas (cobrança de uma diária inteira)")
    void atrasoMaximo() {
        // Diária 280, atraso >=4 => +280
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM,
                1, 0, 4, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("560.00"), total);
    }

    /* ---------- Arredondamento ---------- */

    @Test
    @DisplayName("Arredondamento HALF_UP (terceira casa >=5)")
    void arredondamentoParaCima() {
        // Montar situação que gera 0.005 centavos extra.
        // Use diárias 1 ECONOMICO =120, km excedente 1 km (tarifa 0.80) => 0.80
        // Total =120.80 -> no terceiro decimal
        // To force .005, we can use 1 hora atraso 2h => 120*0.20*2 =48
        // Total =120+48=168 -> still integer.
        // Simpler: use 1 diárias, tarifa 0.80 * 1 =0.80, total 120.80 -> no rounding needed.
        // We'll test a value with 3rd decimal: e.g., 1 diárias ECONOMICO, 1 km excedente (0.80) and 1 hora atraso 2h? not.
        // Instead compute manually: 1 diárias ECONOMICO (120), km excedente 1 (0.80), atraso 2h (48) => 168.80
        // No third decimal. We'll create a case where discount creates fraction: 3 diárias ECONOMICO => bruto 360, desconto 5% =18 => 342.00 exact.
        // Use 5 diárias ECONOMICO => bruto 600, desconto 5% =30 =>570.00 exact.
        // Hard to get fraction; use combination of tarifa 0.80 with odd km to get .8 multiples only.
        // We'll trust that setScale works; just assert scale 2.
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                1, 101, 0, TipoSeguro.SEM_SEGURO, false);
        // 101 km -> franquia 100, excedente 1 km *1.00 =1.00 (since excedente 1 falls in 101-300 tariff)
        // Total =120+1=121.00
        assertEquals(new BigDecimal("121.00"), total);
    }

    @Test
    @DisplayName("Arredondamento para baixo quando terceira casa <5")
    void arredondamentoParaBaixo() {
        // Create total with .004 -> use combination that yields .004, e.g., 0.80 * 1 =0.80 not.
        // We'll simulate by calling private? Not possible. Assume method works; just verify scale.
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                1, 0, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(2, total.scale());
    }

    /* ---------- Cobertura de caminhos combinados ---------- */

    @Test
    @DisplayName("Caso complexo: SUV, OURO, 12 diárias, km excedente 350, seguro COMPLETO, atraso 3h, sem atraso anterior")
    void casoComplexo() {
        // Diárias brutas: 12*280 = 3360
        // Desconto duração (12 -> 10%): 336
        // Valor após desconto duração: 3024
        // Fidelidade OURO (12 >=5, sem atraso anterior): 10% => 302.40
        // Valor diárias final: 2721.60
        // Franquia km: 12*100 =1200, km rodados 1550 => excedente 350
        // Tarifa 1.50 => 350*1.5 = 525.00
        // Seguro COMPLETO: 12*45 = 540.00
        // Atraso 3h => 20% da diária *3 = 280*0.20*3 = 168.00
        // Total = 2721.60 + 525.00 + 540.00 + 168.00 = 3954.60
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.OURO,
                12, 1550, 3, TipoSeguro.COMPLETO, false);
        assertEquals(new BigDecimal("3954.60"), total);
    }
}
