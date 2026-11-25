package br.com.aluguelcarros;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
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
        // Diárias: 3*120=360, desconto 5% => 342
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
        // 1 diária ECONOMICO =120, km excedente 1 km (tarifa 0.80) => total 120.80
        BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM,
                1, 101, 0, TipoSeguro.SEM_SEGURO, false);
        assertEquals(new BigDecimal("120.80"), total);
    }

    @Test
    @DisplayName("Arredondamento para baixo quando terceira casa <5")
    void arredondamentoParaBaixo() {
        // Verify scale is 2
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
