@Test
@DisplayName("Elemento nulo em valores")
void elementoNuloValores() {
    List<ValorCarta> valores = java.util.Arrays.asList(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
    List<Naipe> naipes = java.util.Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
    Executable exec = () -> new Mao(valores, naipes);
    assertThrows(NullPointerException.class, exec);
}

@Test
@DisplayName("Elemento nulo em naipes")
void elementoNuloNaipes() {
    List<ValorCarta> valores = java.util.Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
    List<Naipe> naipes = java.util.Arrays.asList(Naipe.COPAS, null, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS);
    Executable exec = () -> new Mao(valores, naipes);
    assertThrows(NullPointerException.class, exec);
}