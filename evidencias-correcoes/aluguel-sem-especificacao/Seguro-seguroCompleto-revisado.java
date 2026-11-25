@Test
void seguroCompleto() {
    // 3 diárias ECONOMICO = 3 * 120 = 360
    // Desconto de duração 5% (3 diárias) -> 360 - 18 = 342
    // Seguro completo = 3 * 45 = 135
    // Total esperado = 342 + 135 = 477.00
    BigDecimal esperado = new BigDecimal("477.00");
    assertEquals(esperado, calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 0, 0,
            TipoSeguro.COMPLETO, false));
}