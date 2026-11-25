@Test
void prataComAtrasoAnteriorZeraDesconto() {
    BigDecimal total = calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 0, 0,
            TipoSeguro.SEM_SEGURO, true);
    // desconto de duração de 10% (7 diárias)
    BigDecimal valorBruto = new BigDecimal("120.00").multiply(BigDecimal.valueOf(7));
    BigDecimal descontoDuracao = valorBruto.multiply(new BigDecimal("0.10"));
    BigDecimal esperado = valorBruto.subtract(descontoDuracao);
    assertEquals(esperado.setScale(2, RoundingMode.HALF_UP), total);
}