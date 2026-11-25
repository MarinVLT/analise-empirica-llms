package br.com.aluguelcarros;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SuiteSemAnalise {

    private final CalculadoraCustoLocacao calc = new CalculadoraCustoLocacao();

    @Test
    void calculaSemDescontosNenhumCustoExtra() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM,
                1,
                0,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        assertEquals(new BigDecimal("120.00"), total);
    }

    @Test
    void descontoDuracaoCincoPorcento() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.COMUM,
                4,
                0,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // 180 * 4 = 720 ; 5% discount = 36 ; total = 684
        assertEquals(new BigDecimal("684.00"), total);
    }

    @Test
    void fidelidadePrataCincoPorcentoAposDescontoDuracao() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.PRATA,
                7,
                0,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // 120*7 = 840 ; 10% duracao = 84 ; 756 ; 5% fidelidade = 37.80 ; total = 718.20
        assertEquals(new BigDecimal("718.20"), total);
    }

    @Test
    void fidelidadeOuroDezPorcentoAposDescontoDuracao() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.SUV,
                NivelCliente.OURO,
                5,
                0,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // 280*5 = 1400 ; 5% duracao = 70 ; 1330 ; 10% fidelidade = 133.00 ; total = 1197.00
        assertEquals(new BigDecimal("1197.00"), total);
    }

    @Test
    void custoQuilometragemPrimeiraFaixa() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM,
                3,
                350,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // diarias: 120*3=360 ; 5% discount =18 ; 342
        // franquia 300, excess 50 *0.80 =40
        // total = 382.00
        assertEquals(new BigDecimal("382.00"), total);
    }

    @Test
    void custoQuilometragemSegundaFaixa() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.INTERMEDIARIO,
                NivelCliente.COMUM,
                2,
                250,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // diarias: 180*2=360 ; no discount
        // franquia 200, excess 50 *0.80 =40
        // total = 400.00
        assertEquals(new BigDecimal("400.00"), total);
    }

    @Test
    void custoQuilometragemTerceiraFaixa() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.SUV,
                NivelCliente.COMUM,
                1,
                500,
                0,
                TipoSeguro.SEM_SEGURO,
                false);
        // diarias: 280
        // franquia 100, excess 400 *1.50 =600
        // total = 880.00
        assertEquals(new BigDecimal("880.00"), total);
    }

    @Test
    void custoSeguroBasico() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM,
                2,
                0,
                0,
                TipoSeguro.BASICO,
                false);
        // diarias 120*2=240 ; seguro 25*2=50 ; total 290.00
        assertEquals(new BigDecimal("290.00"), total);
    }

    @Test
    void atrasoDuasHoras() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM,
                1,
                0,
                2,
                TipoSeguro.SEM_SEGURO,
                false);
        // diaria 120 ; atraso 120*0.20*2 =48 ; total 168.00
        assertEquals(new BigDecimal("168.00"), total);
    }

    @Test
    void atrasoQuatroHoras() {
        BigDecimal total = calc.calcularCustoLocacao(
                CategoriaVeiculo.ECONOMICO,
                NivelCliente.COMUM,
                1,
                0,
                4,
                TipoSeguro.SEM_SEGURO,
                false);
        // atraso = one full daily =120 ; total 240.00
        assertEquals(new BigDecimal("240.00"), total);
    }

    @Test
    void nullCategoriaThrows() {
        assertThrows(NullPointerException.class, () ->
                calc.calcularCustoLocacao(
                        null,
                        NivelCliente.COMUM,
                        1,
                        0,
                        0,
                        TipoSeguro.SEM_SEGURO,
                        false));
    }

    @Test
    void nullNivelThrows() {
        assertThrows(NullPointerException.class, () ->
                calc.calcularCustoLocacao(
                        CategoriaVeiculo.ECONOMICO,
                        null,
                        1,
                        0,
                        0,
                        TipoSeguro.SEM_SEGURO,
                        false));
    }

    @Test
    void nullSeguroThrows() {
        assertThrows(NullPointerException.class, () ->
                calc.calcularCustoLocacao(
                        CategoriaVeiculo.ECONOMICO,
                        NivelCliente.COMUM,
                        1,
                        0,
                        0,
                        null,
                        false));
    }

    @Test
    void numeroDiariasZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                calc.calcularCustoLocacao(
                        CategoriaVeiculo.ECONOMICO,
                        NivelCliente.COMUM,
                        0,
                        0,
                        0,
                        TipoSeguro.SEM_SEGURO,
                        false));
    }

    @Test
    void quilometragemNegativaThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                calc.calcularCustoLocacao(
                        CategoriaVeiculo.ECONOMICO,
                        NivelCliente.COMUM,
                        1,
                        -10,
                        0,
                        TipoSeguro.SEM_SEGURO,
                        false));
    }

    @Test
    void horasAtrasoNegativasThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                calc.calcularCustoLocacao(
                        CategoriaVeiculo.ECONOMICO,
                        NivelCliente.COMUM,
                        1,
                        0,
                        -5,
                        TipoSeguro.SEM_SEGURO,
                        false));
    }
}