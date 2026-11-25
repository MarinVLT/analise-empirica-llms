@Test
@DisplayName("Elemento nulo nas listas lança NullPointerException")
void elementoNulo() {
    List<ValorCarta> valoresComNulo = java.util.Arrays.asList(ValorCarta.AS, null, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO);
    List<Naipe> naipesComNulo = java.util.Arrays.asList(Naipe.COPAS, Naipe.OUROS, null, Naipe.ESPADAS, Naipe.COPAS);
    assertThrows(NullPointerException.class,
            () -> new Mao(valoresComNulo,
                    java.util.Arrays.asList(Naipe.COPAS, Naipe.OUROS, Naipe.PAUS, Naipe.ESPADAS, Naipe.COPAS)));
    assertThrows(NullPointerException.class,
            () -> new Mao(java.util.Arrays.asList(ValorCarta.AS, ValorCarta.DOIS, ValorCarta.TRES, ValorCarta.QUATRO, ValorCarta.CINCO),
                    naipesComNulo));
}