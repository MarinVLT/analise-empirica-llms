Projete os casos de teste a partir do material fornecido. Escolha as entradas concretas e determine os resultados ou exceções esperados. Implemente os casos em uma suíte de testes unitários Java com JUnit 5 para a API a seguir.

# Classificação de mão de pôquer

## API pública

Classe `Mao` no pacote `br.com.poquer`.

```java
public Mao(java.util.List<ValorCarta> valores, java.util.List<Naipe> naipes)
public ClassificacaoMao classificar() // método de Mao
```

O enum `ValorCarta` contém `AS`, `DOIS`, `TRES`, `QUATRO`, `CINCO`, `SEIS`, `SETE`, `OITO`, `NOVE`, `DEZ`, `VALETE`, `DAMA` e `REI`. O enum `Naipe` contém `COPAS`, `OUROS`, `PAUS` e `ESPADAS`.

## Código

[Insira o código dos arquivos Java necessários em src/main/java/br/com/poquer/. Identifique o caminho de cada arquivo.]

## Especificação

### Entradas válidas

Passe duas listas com cinco elementos cada. `valores[i]` e `naipes[i]` formam uma carta. Os cinco pares devem ser distintos. A ordem da mão é livre, desde que valor e naipe de cada carta permaneçam juntos ao reordenar as listas.

### Resultados e exceções

Uma sequência tem cinco valores consecutivos distintos. O ás pode ocupar a primeira posição na sequência mais baixa ou a última na sequência mais alta. Em outras posições, a passagem de rei para ás quebra a sequência.

O método devolve a primeira classificação aplicável, nesta ordem de prioridade:

1. `SEQUENCIA_REAL_DO_MESMO_NAIPE` — 10–J–Q–K–A do mesmo naipe.
2. `SEQUENCIA_DO_MESMO_NAIPE` — sequência do mesmo naipe.
3. `QUADRA` — quatro cartas do mesmo valor.
4. `TRINCA_E_PAR` — três cartas de um valor e duas de outro.
5. `CINCO_DO_MESMO_NAIPE` — cinco cartas do mesmo naipe sem sequência.
6. `SEQUENCIA` — sequência com mais de um naipe.
7. `TRINCA` — três cartas do mesmo valor.
8. `DOIS_PARES` — dois pares de valores distintos.
9. `UM_PAR` — duas cartas do mesmo valor.
10. `CARTA_ALTA` — demais mãos.

Lista nula ou elemento nulo provoca `NullPointerException`. Lista com tamanho diferente de cinco ou par de valor e naipe repetido provoca `IllegalArgumentException`.

## Análise

# Critérios de teste — poquer

> Orientação: Substituam todos os campos entre colchetes. Acrescentem uma linha por classe, fronteira, regra ou obrigação de cobertura. Se um critério não se aplicar, substituam sua tabela por uma justificativa breve.

> Orientação: Registrem somente os critérios e seus resultados de análise. A LLM escolhe os valores de entrada para cada caso, determina o resultado esperado e escreve os testes JUnit.

### Análise caixa-preta do grupo

> Orientação: Usem a especificação como fonte. Considerem a composição da mão, os naipes, as repetições e as posições permitidas do ás.

#### Classes de equivalência

> Orientação: Informem o parâmetro ou regra, deem um nome à classe e descrevam a faixa ou propriedade que inclui seus membros. Registrem classes válidas e inválidas quando previstas.

| Parâmetro ou regra | Classe de equivalência | Domínio ou condição |
|---|---|---|
| [parâmetro ou regra] | [nome e validade da classe] | [faixa ou propriedade que define a classe] |

#### Valores-limite

> Orientação: Indiquem o valor onde muda a validade ou o comportamento e descrevam as classes que ele separa. Esclareçam a qual classe pertence o próprio limite.

| Parâmetro ou regra | Valor-limite | Classes separadas e inclusão do limite |
|---|---|---|
| [parâmetro ou regra] | [valor da fronteira] | [classes de cada lado e classe do limite] |

#### Tabela de decisão

> Orientação: Uma linha descreve uma combinação geral de condições e a ação que a especificação prevê. Usem estados ou categorias, como condição satisfeita, condição insatisfeita ou indiferente, quando couber.

| Regra | Combinação de condições | Ação prevista na especificação |
|---|---|---|
| [identificador da regra] | [condições e estados aplicáveis] | [ação geral] |

### Análise caixa-branca do grupo

> Orientação: Usem o código de produção. Na primeira coluna, indiquem o método e o trecho ou a decisão. Na segunda, descrevam em linguagem natural como a decisão, condição ou caminho funciona. Na terceira, indiquem quais resultados de decisão, ramos ou caminhos a suíte deve exercitar. Se houver curto-circuito, indiquem quando uma condição posterior chega a ser avaliada.

| Método ou trecho | Decisão, condição ou caminho | Obrigação de cobertura |
|---|---|---|
| [método ou referência ao trecho] | [descrição estrutural] | [resultados, ramos ou caminhos a exercitar] |
