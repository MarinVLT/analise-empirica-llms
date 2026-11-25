@Test
void ouroComDesconto() {
    // 5 diárias, 5% desconto por duração e 10% fidelidade
    BigDecimal valorDiaria = new BigDecimal("120.00");
    int diarias = 5;
    BigDecimal bruto = valorDiaria.multiply(BigDecimal.valueOf(diarias)); // 600.00
    BigDecimal descontoDuracao = bruto.multiply(new BigDecimal("0.05")); // 30.00
    BigDecimal comDescontoDuracao = bruto.subtract(descontoDuracao); // 570.00
    BigDecimal descontoFidelidade = comDescontoDuracao.multiply(new BigDecimal("0.10")); // 57.00
    BigDecimal esperado = comDescontoDuracao.subtract(descontoFidelidade); // 513.00
    assertEquals(esperado.setScale(2, RoundingMode.HALF_UP),
            calc.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO,
                    5, 0, 0, TipoSeguro.SEM_SEGURO, false));
}