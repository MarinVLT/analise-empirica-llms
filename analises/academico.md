# Critérios de teste — academico

> Orientação: As linhas preenchidas usam um pedido fictício e servem para testar a montagem dos prompts. Substituam todas essas linhas pela análise da funcionalidade deste arquivo antes de preparar o plano oficial. Acrescentem uma linha por classe, fronteira, regra ou obrigação de cobertura. Se um critério não se aplicar, substituam sua tabela por uma justificativa breve.

> Orientação: Registrem somente os critérios e seus resultados de análise. A LLM escolhe os valores de entrada para cada caso, determina o resultado esperado e escreve os testes JUnit.

### Análise caixa-preta do grupo

> Orientação: Usem a especificação como fonte. Considerem notas, frequência, arredondamento e as regras de classificação da situação acadêmica.

#### Classes de equivalência

> Orientação: Informem o parâmetro ou regra, deem um nome à classe e descrevam a faixa ou propriedade que inclui seus membros. Registrem classes válidas e inválidas quando previstas.

| Parâmetro ou regra | Classe de equivalência | Domínio ou condição |
|---|---|---|
| Nota | Inválida abaixo do domínio | Nota < 0,00 |
| Nota | Inválida acima do domínio | Nota > 10,00 |
| Nota | Inválida nula | Nota nula |
| Frequência | Inválida abaixo do domínio | Frequência < 0% |
| Frequência | Inválida acima do domínio | Frequência > 100% |
| Frequência | Inválida nula | Frequência nula |
| Frequência | Abaixo do mínimo | Frequência < 75%; situações possíveis: REMF, REPF |
| Frequência | Mínimo atingido | Frequência >= 75%; situações possíveis: REC, APRN, APR, REP |
| Média | Abaixo de 3 | Média < 3; situações possíveis: REMF, REP |
| Média | De 3 até abaixo de 7 | Média >= 3 e média < 7; situações possíveis: REPF, REC |
| Média | De 5 até abaixo de 7 | Média >= 5 e média < 7; situação possível: APRN |
| Média | A partir de 7 | Média >= 7; situação possível: APR |
| Notas das unidades | Todas as notas a partir de 3 | Todas as notas >= 3; situação possível: APRN |
| Notas das unidades | Pelo menos uma nota abaixo de 3 | Pelo menos uma nota < 3; situação possível: REC |

Critérios que definem a situação acadêmica: frequência, média e notas (as notas individuais influenciam apenas APRN e REC). Combinações que influenciam o resultado: frequência e média; frequência, notas e média.

#### Valores-limite

> Orientação: Indiquem o valor onde muda a validade ou o comportamento e descrevam as classes que ele separa. Esclareçam a qual classe pertence o próprio limite.

| Parâmetro ou regra | Valor-limite | Classes separadas e inclusão do limite |
|---|---|---|
| Frequência | 75% | Valor-limite de frequência tanto para aprovação quanto para reprovação. |
| Média | 3 | Valor-limite de média. |
| Média | 5 | Valor-limite de média. |
| Média | 7 | Valor-limite de média. |
| Notas e média (arredondamento) | Arredondamento `HALF_UP` para uma casa decimal | Cada nota é arredondada para uma casa decimal com `HALF_UP`, e a média aritmética das três notas também é arredondada para uma casa decimal com `HALF_UP`. As decisões usam os valores arredondados. Ou seja, notas um pouco abaixo do limite passam a ser o valor do limite, o que influencia no resultado final da situação acadêmica. |

#### Tabela de decisão

> Orientação: Uma linha descreve uma combinação geral de condições e a ação que a especificação prevê. Usem estados ou categorias, como condição satisfeita, condição insatisfeita ou indiferente, quando couber.

| Regra | Combinação de condições | Ação prevista na especificação |
|---|---|---|
| ACA-01 | Frequência < 75 e média < 3 | REMF |
| ACA-02 | Frequência < 75 e média >= 3 e < 5 | REPF |
| ACA-03 | Frequência < 75 e média >= 5 e < 7 | REPF |
| ACA-04 | Frequência < 75 e média >= 7 | REPF |
| ACA-05 | Frequência >= 75 e média < 3 | REP |
| ACA-06 | Frequência >= 75 e média >= 3 e < 5 | REC |
| ACA-07 | Frequência >= 75, média >= 5 e < 7 e pelo menos uma nota < 3 | REC |
| ACA-08 | Frequência >= 75, média >= 5 e < 7 e todas as notas >= 3 | APRN |
| ACA-09 | Frequência >= 75 e média >= 7 | APR |

Apenas as combinações de frequência e média e de frequência, notas e média influenciam as situações acadêmicas apresentadas. APRN e REC são as situações afetadas pelos três critérios: frequência, notas e média. Com frequência >= 75 e média >= 5 e < 7, caso alguma nota das unidades seja abaixo de 3 o aluno está em REC; caso todas sejam >= 3, o aluno está em APRN.

### Análise caixa-branca do grupo

> Orientação: Usem o código de produção. Na primeira coluna, indiquem o método e o trecho ou a decisão. Na segunda, descrevam em linguagem natural como a decisão, condição ou caminho funciona. Na terceira, indiquem quais resultados de decisão, ramos ou caminhos a suíte deve exercitar. Se houver curto-circuito, indiquem quando uma condição posterior chega a ser avaliada.

| Método ou trecho | Decisão, condição ou caminho | Obrigação de cobertura |
|---|---|---|
| PENDENTE | PENDENTE | PENDENTE |
