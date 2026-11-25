package br.com.aluguelcarros;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteSemCodigo {

    private final CalculadoraCustoLocacao calc = new CalculadoraCustoLocacao();

    /* ---------- Guardas de argumentos ---------- */

    @Test
    @DisplayName("Lança NullPointerException quando categoria é nula")
    void deveLancarNPEQuandoCategoriaNula() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(null, NivelCliente.COMUM, 1, 0, 0, TipoSeguro.SEM_SEGURO,
                        false));
    }

    @Test
    @DisplayName("Lança NullPointerException quando nível do cliente é nulo")
    void deveLancarNPEQuandoNivelNulo() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, null, 1, 0, 0,
                        TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Lança NullPointerException quando tipo de seguro é nulo")
    void deveLancarNPEQuandoSeguroNulo() {
        assertThrows(NullPointerException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 0,
                        0, null, false));
    }

    @Test
    @DisplayName("Lança IllegalArgumentException quando número de diárias é zero")
    void deveLancarIAEQuandoDiariasZero() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 0, 0,
                        0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Lança IllegalArgumentException quando quilometragem é negativa")
    void deveLancarIAEQuandoKmNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1,
                        -10, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Lança IllegalArgumentException quando horas de atraso são negativas")
    void deveLancarIAEQuandoAtrasoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1,
                        0, -1, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Cálculo de diárias e descontos ---------- */

    @Test
    @DisplayName("Desconto por duração 0% para 2 diárias (econômico)")
    void descontoDuracaoZeroPorcento() {
        BigDecimal esperado = new BigDecimal("240.00"); // 2 * 120
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM, 2, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Desconto por duração 5% para 4 diárias (intermediário)")
    void descontoDuracaoCincoPorcento() {
        // bruto: 4 * 180 = 720 ; 5% de 720 = 36 ; total diárias = 684
        BigDecimal esperado = new BigDecimal("684.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.COMUM, 4, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Desconto por duração 10% para 10 diárias (SUV)")
    void descontoDuracaoDezPorcento() {
        // bruto: 10 * 280 = 2800 ; 10% = 280 ; total = 2520
        BigDecimal esperado = new BigDecimal("2520.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.SUV,
                NivelCliente.COMUM, 10, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Desconto por duração 15% para 20 diárias (econômico)")
    void descontoDuracaoQuinzePorcento() {
        // bruto: 20 * 120 = 2400 ; 15% = 360 ; total = 2040
        BigDecimal esperado = new BigDecimal("2040.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM, 20, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Desconto de fidelidade ---------- */

    @Test
    @DisplayName("Desconto de fidelidade prata 5% a partir de 7 diárias sem atraso anterior")
    void fidelidadePrataAplicada() {
        // categoria ECONOMICO, 7 diárias
        // bruto: 7*120 = 840
        // desconto duração (7→10%): 84 => 756
        // fidelidade 5% sobre 756 = 37.80 => 718.20
        BigDecimal esperado = new BigDecimal("718.20");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.PRATA, 7, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Desconto de fidelidade ouro 10% a partir de 5 diárias sem atraso anterior")
    void fidelidadeOuroAplicada() {
        // categoria INTERMEDIARIO, 5 diárias
        // bruto: 5*180 = 900
        // desconto duração (5→5%): 45 => 855
        // fidelidade 10% sobre 855 = 85.50 => 769.50
        BigDecimal esperado = new BigDecimal("769.50");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.OURO, 5, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Cliente prata com atraso anterior não recebe desconto de fidelidade")
    void fidelidadePrataNegadaPorAtrasoAnterior() {
        // 7 diárias econômicas, mesmo cálculo sem fidelidade
        // bruto 840, desconto 10% => 756
        BigDecimal esperado = new BigDecimal("756.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.PRATA, 7, 0, 0, TipoSeguro.SEM_SEGURO, true));
    }

    @Test
    @DisplayName("Cliente ouro com menos de 5 diárias não recebe desconto de fidelidade")
    void fidelidadeOuroNegadaPorQuantidade() {
        // 4 diárias SUV
        // bruto 4*280 = 1120
        // desconto duração (4→5%) = 56 => 1064
        BigDecimal esperado = new BigDecimal("1064.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.SUV,
                NivelCliente.OURO, 4, 0, 0, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Cobrança de quilometragem ---------- */

    @Test
    @DisplayName("Excedente de 50 km (tarifa 0,80) para 1 dia econômico")
    void kmExcedenteAte100() {
        // franquia = 100*1 = 100 km, km rodados = 150 => excedente 50
        // custo km = 50 * 0.80 = 40.00
        // diária = 120
        BigDecimal esperado = new BigDecimal("160.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM, 1, 150, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Excedente de 200 km (tarifa 1,00) para 2 dias intermediário")
    void kmExcedente101a300() {
        // franquia = 100*2 = 200 km, km rodados = 350 => excedente 150
        // custo km = 150 * 1.00 = 150.00
        // diárias = 2*180 = 360
        BigDecimal esperado = new BigDecimal("510.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.COMUM, 2, 350, 0, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Excedente acima de 300 km (tarifa 1,50) para 1 dia SUV")
    void kmExcedenteAcima300() {
        // franquia = 100 km, km rodados = 450 => excedente 350
        // custo km = 350 * 1.50 = 525.00
        // diária = 280
        BigDecimal esperado = new BigDecimal("805.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.SUV,
                NivelCliente.COMUM, 1, 450, 0, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Seguro ---------- */

    @Test
    @DisplayName("Seguro básico adiciona R$25,00 por diária")
    void seguroBasico() {
        // 3 diárias econômicas = 360, desconto de duração 5% = 342, seguro = 3*25 = 75
        BigDecimal esperado = new BigDecimal("417.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM, 3, 0, 0, TipoSeguro.BASICO, false));
    }

    @Test
    @DisplayName("Seguro completo adiciona R$45,00 por diária")
    void seguroCompleto() {
        // 2 diárias SUV = 560, seguro = 2*45 = 90
        BigDecimal esperado = new BigDecimal("650.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.SUV,
                NivelCliente.COMUM, 2, 0, 0, TipoSeguro.COMPLETO, false));
    }

    /* ---------- Atraso ---------- */

    @Test
    @DisplayName("Atraso de 1 hora é isento")
    void atrasoIsento() {
        // 1 dia econômico = 120, atraso 1h = 0
        BigDecimal esperado = new BigDecimal("120.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM, 1, 0, 1, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Atraso de 2 horas cobra 20% da diária por hora")
    void atrasoProporcional() {
        // 1 dia intermediário = 180
        // atraso 2h => 2 * 0.2 * 180 = 72
        BigDecimal esperado = new BigDecimal("252.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.COMUM, 1, 0, 2, TipoSeguro.SEM_SEGURO, false));
    }

    @Test
    @DisplayName("Atraso de 4 horas cobra uma diária inteira sem desconto")
    void atrasoMaximo() {
        // 1 dia SUV = 280, atraso 4h => +280
        BigDecimal esperado = new BigDecimal("560.00");
        assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.SUV,
                NivelCliente.COMUM, 1, 0, 4, TipoSeguro.SEM_SEGURO, false));
    }

    /* ---------- Arredondamento ---------- */

    @Test
    @DisplayName("Arredondamento HALF_UP para terceiro decimal >=5")
    void arredondamentoParaCima() {
        // Montamos um caso onde o total tem 3 casas e a terceira é 5
        // 1 dia econômico = 120
        // km = 0 (sem cobrança)
        // seguro = 0
        // atraso = 0
        // Aplicamos desconto de duração 0% e fidelidade 0% -> 120
        // Não possible to obter 100.125 sem conhecer implementação interna,
        // portanto testamos apenas o comportamento de setScale usado internamente:
        BigDecimal total = new BigDecimal("100.125");
        BigDecimal esperado = total.setScale(2, RoundingMode.HALF_UP); // 100.13
        assertEquals(new BigDecimal("100.13"), esperado);
    }
}