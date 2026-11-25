@Test
@DisplayName("Elemento nulo nas listas lança NullPointerException")
void elementoNulo() {
    List<ValorCarta> valoresComNulo = List.of(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
    List<Naipe> naipesComNulo = List.of(Naipe.COPAS, Naipe.OUROS, null, Naipe.ESPADAS, Naipe.COPAS);
    assertThrows(NullPointerException.class, () -> new Mao(valoresComNulo, List.of(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS)));
    assertThrows(NullPointerException.class, () -> new Mao(List.of(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO), naipesComNulo));
}