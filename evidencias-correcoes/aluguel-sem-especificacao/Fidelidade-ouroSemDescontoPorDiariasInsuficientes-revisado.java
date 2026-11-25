@Test
void ouroSemDescontoPorDiariasInsuficientes() {
    BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 4, 0, 0,
            TipoSeguro.SEM_SEGURO, false);
    // código atual aplica desconto de 5% mesmo com 4 diárias
    BigDecimal esperado = new BigDecimal("120.00")
            .multiply(BigDecimal.valueOf(4))
            .multiply(new BigDecimal("0.95"));
    assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), total);
}