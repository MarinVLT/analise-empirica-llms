@Test
void prataComDesconto() {
    // 7 diárias, econômico, desconto de duração 10% e fidelidade prata 5%
    BigDecimal esperado = new BigDecimal("120.00")
            .multiply(BigDecimal.valueOf(7))
            .multiply(new BigDecimal("0.90")) // desconto de duração 10%
            .multiply(new BigDecimal("0.95")); // desconto de fidelidade 5%
    assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), calc.calcularCustoLocacao(
            CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 0, 0, TipoSeguro.SEM_SEGURO, false));
}