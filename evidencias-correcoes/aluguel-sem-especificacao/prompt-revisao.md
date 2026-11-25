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

- br.com.aluguelcarros.SuiteSemEspecificacao$Fidelidade.prataComAtrasoAnteriorZeraDesconto: org.opentest4j.AssertionFailedError — expected: <840.00> but was: <756.00>
- br.com.aluguelcarros.SuiteSemEspecificacao$Fidelidade.prataComDesconto: org.opentest4j.AssertionFailedError — expected: <798.00> but was: <718.20>
- br.com.aluguelcarros.SuiteSemEspecificacao$Fidelidade.ouroComDesconto: org.opentest4j.AssertionFailedError — expected: <540.00> but was: <513.00>
- br.com.aluguelcarros.SuiteSemEspecificacao$Fidelidade.ouroSemDescontoPorDiariasInsuficientes: org.opentest4j.AssertionFailedError — expected: <480.00> but was: <456.00>
- br.com.aluguelcarros.SuiteSemEspecificacao$Seguro.seguroCompleto: org.opentest4j.AssertionFailedError — expected: <495.00> but was: <477.00>

# Suíte atual

package br.com.aluguelcarros;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteSemEspecificacao {

    private final CalculadoraCustoLocacao calc = new CalculadoraCustoLocacao();

    // ---------- Guardas de argumentos ----------
    @Test
    @DisplayName("LOC-01: categoria nula lança NullPointerException")
    void categoriaNulaLancaNPE() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(null, NivelCliente.COMUM, 1, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("LOC-01: nivelCliente nulo lança NullPointerException")
    void nivelNuloLancaNPE() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, null, 1, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("LOC-01: seguro nulo lança NullPointerException")
    void seguroNuloLancaNPE() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 0, null, false));
    }

    @Test
    @DisplayName("LOC-02: número de diárias <= 0 lança IllegalArgumentException")
    void diariasZeroLancaIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 0, 0, 0,
                        TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("LOC-03: quilometragem negativa lança IllegalArgumentException")
    void quilometragemNegativaLancaIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, -1, 0,
                        TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("LOC-04: horas de atraso negativas lançam IllegalArgumentException")
    void atrasoNegativoLancaIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, -5,
                        TipoSeguro.SEM_SEGURO, false));
    }

    // ---------- Valor da diária ----------
    @Nested
    @DisplayName("LOC-05: valor da diária por categoria")
    class ValorDiaria {

        @Test
        void economico() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), total);
        }

        @Test
        void intermediario() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM, 1, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("180.00"), total);
        }

        @Test
        void suv() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM, 1, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("280.00"), total);
        }
    }

    // ---------- Desconto por duração ----------
    @Nested
    @DisplayName("LOC-06: desconto por duração")
    class DescontoDuracao {

        @Test
        void duasDiariasSemDesconto() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), total);
        }

        @Test
        void tresDiariasDesconto5Porcento() {
            // 120 * 3 = 360 ; 5% = 18 ; total = 342
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("342.00"), total);
        }

        @Test
        void seteDiariasDesconto10Porcento() {
            // 120*7=840 ; 10% =84 ; total=756
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 7, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("756.00"), total);
        }

        @Test
        void quinzeDiariasDesconto15Porcento() {
            // 120*15=1800 ; 15% =270 ; total=1530
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 15, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("1530.00"), total);
        }
    }

    // ---------- Desconto de fidelidade ----------
    @Nested
    @DisplayName("LOC-07 e LOC-08: desconto de fidelidade")
    class Fidelidade {

        @Test
        void prataComDesconto() {
            // 7 diárias, econômico, 5% sobre valor já com desconto de duração (0%)
            BigDecimal esperado = new BigDecimal("120.00")
                    .multiply(BigDecimal.valueOf(7))
                    .multiply(new BigDecimal("0.95")); // 5% off
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
                    CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 0, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void prataSemDescontoPorDiariasInsuficientes() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 6, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            // apenas desconto de duração 5%
            BigDecimal esperado = new BigDecimal("120.00")
                    .multiply(BigDecimal.valueOf(6))
                    .multiply(new BigDecimal("0.95"));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), total);
        }

        @Test
        void prataComAtrasoAnteriorZeraDesconto() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 0, 0,
                    TipoSeguro.SEM_SEGURO, true);
            // só desconto de duração (0%)
            BigDecimal esperado = new BigDecimal("120.00").multiply(BigDecimal.valueOf(7));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), total);
        }

        @Test
        void ouroComDesconto() {
            // 5 diárias, 10% fidelidade
            BigDecimal esperado = new BigDecimal("120.00")
                    .multiply(BigDecimal.valueOf(5))
                    .multiply(new BigDecimal("0.90"));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
                    CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 5, 0, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void ouroSemDescontoPorDiariasInsuficientes() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 4, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            // apenas desconto de duração (0%)
            BigDecimal esperado = new BigDecimal("120.00").multiply(BigDecimal.valueOf(4));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), total);
        }
    }

    // ---------- Quilometragem ----------
    @Nested
    @DisplayName("LOC-10 e LOC-11: custo de quilometragem")
    class Quilometragem {

        @Test
        void dentroDaFranquia() {
            // 2 diárias => franquia 200 km, usar 150 km
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 150, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), total);
        }

        @Test
        void excedenteCurto() {
            // 1 diária => franquia 100 km, usar 150 km => excedente 50 km a 0.80
            BigDecimal esperado = new BigDecimal("120.00")
                    .add(new BigDecimal("50").multiply(new BigDecimal("0.80")));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
                    CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 150, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void excedenteMedio() {
            // 1 diária, 250 km => excedente 150 km tarifado a 1.00
            BigDecimal esperado = new BigDecimal("120.00")
                    .add(new BigDecimal("150").multiply(new BigDecimal("1.00")));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
                    CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 250, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void excedenteLongo() {
            // 1 diária, 500 km => excedente 400 km tarifado a 1.50
            BigDecimal esperado = new BigDecimal("120.00")
                    .add(new BigDecimal("400").multiply(new BigDecimal("1.50")));
            assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
                    CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 500, 0, TipoSeguro.SEM_SEGURO, false));
        }
    }

    // ---------- Seguro ----------
    @Nested
    @DisplayName("LOC-12: custo do seguro")
    class Seguro {

        @Test
        void semSeguro() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), total);
        }

        @Test
        void seguroBasico() {
            // 2 diárias * 25 = 50
            BigDecimal esperado = new BigDecimal("240.00").add(new BigDecimal("50.00"));
            assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 0, 0,
                    TipoSeguro.BASICO, false));
        }

        @Test
        void seguroCompleto() {
            // 3 diárias * 45 = 135
            BigDecimal esperado = new BigDecimal("360.00").add(new BigDecimal("135.00"));
            assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 0, 0,
                    TipoSeguro.COMPLETO, false));
        }
    }

    // ---------- Atraso ----------
    @Nested
    @DisplayName("LOC-13: custo de atraso")
    class Atraso {

        @Test
        void isentoZeroHoras() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 0,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), total);
        }

        @Test
        void isentoUmaHora() {
            BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 1,
                    TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), total);
        }

        @Test
        void proporcionalDuasHoras() {
            // 20% da diária por hora => 0.2*120*2 = 48
            BigDecimal esperado = new BigDecimal("120.00").add(new BigDecimal("48.00"));
            assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 2,
                    TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void proporcionalTresHoras() {
            // 0.2*120*3 = 72
            BigDecimal esperado = new BigDecimal("120.00").add(new BigDecimal("72.00"));
            assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 3,
                    TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        void maximoQuatroHoras() {
            // cobrança de uma diária inteira (120)
            BigDecimal esperado = new BigDecimal("240.00");
            assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0, 4,
                    TipoSeguro.SEM_SEGURO, false));
        }
    }

    // ---------- Arredondamento ----------
    @Test
    @DisplayName("LOC-14: arredondamento HALF_UP")
    void arredondamentoHalfUp() {
        // Montar situação que gera 0.005 centavos extra.
        // Use valor diário 120, 1 diária, sem descontos, km excedente 1 km a 0.80 => 120.80
        // Adicionar atraso proporcional 2h => 0.2*120*2 = 48 => 168.80
        // Não gera terceiro decimal. Para forçar, usar combinação que gera 0.005, por exemplo:
        // 1 diária econômica, 1 km excedente (0.80), atraso 2h (48) => 168.80
        // Vamos criar um caso onde custo quilometragem = 0.805 (ex.: 1 km excedente a 0.805) mas tarifa fixa impede.
        // Em vez disso, usar número de diárias que gera valor com centesimal .005 após soma de seguros.
        // 1 diária econômica 120, seguro básico 25 => 145, atraso 2h 48 => 193, km 0 => 193.00 (no)
        // Use 3 diárias econômicas 360, desconto 5% => 342, seguro básico 3*25=75 => 417, atraso 2h 0.2*120*2=48 => 465
        // Still .00
        // Simpler: verify that setScale HALF_UP works by comparing directly with expected scale.
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 101, 2,
                TipoSeguro.SEM_SEGURO, false);
        // cálculo: diária 120, desconto 0, km franquia 100 => excedente 1 km *0.80 =0.80, atraso 48 => total 168.80
        assertEquals(new BigDecimal("168.80"), total);
    }

    // ---------- Cobertura de caminhos de fidelidade ----------
    @Test
    @DisplayName("Caminho onde nenhum desconto de fidelidade é aplicado (nível comum)")
    void nenhumDescontoFidelidadeNivelComum() {
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 10, 0, 0,
                TipoSeguro.SEM_SEGURO, false);
        // 10 diárias, 10% duração => 1200 *0.90 =1080
        assertEquals(new BigDecimal("1080.00"), total);
    }
}
