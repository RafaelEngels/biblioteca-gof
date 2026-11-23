# Registro de uso de LLM

Ferramenta: Claude (Anthropic), claude.ai. _(A equipe deve revisar e completar este arquivo.)_

| # | Pedido ao modelo | O que o modelo entregou | Decisão da equipe |
|---|---|---|---|
| 1 | Ler o enunciado do trabalho e o arquivo `PROJETO_2.asta` e gerar a implementação | Projeto Maven Java 17 com os dez padrões, testes, documentação | Aceito como base; **a equipe precisa ler e entender todo o código** |
| 2 | Reaproveitar os nomes do Trabalho 02 | Nomes de classes, operações e tratadores extraídos do Astah (Livro, Movimentacao, TratadorDeX, estados, iCobranca…) | Aceito; conferir contra o diagrama de classes |
| 3 | Persistência relacional | Família JDBC + `schema.sql`; teste com H2 em modo MySQL | Aceito; **testar num MySQL real antes da demo** |
| 4 | Documentação | README, rastreabilidade, guia de arguição, diagramas PlantUML | Aceito; revisar premissas |

## Pontos que a equipe deve preencher

- O que foi corrigido por vocês depois de ler o código: _…_
- O que foi descartado ou trocado: _…_
- Prompts adicionais que vocês fizerem (ex.: ajustes pedidos pelo professor): _…_

## Limitações conhecidas (informadas pelo modelo)

- O arquivo `.asta` não foi alterado pelo modelo (formato proprietário do Astah). Os diagramas novos
  estão em PlantUML (`docs/uml`) e precisam ser refeitos/importados no Astah pela equipe.
- O build com Maven não foi executado no ambiente do modelo (sem acesso ao Maven Central); os testes
  foram compilados e executados com `javac` + JUnit Console, 40 testes aprovados. Rodar `mvn test` na máquina de vocês.
- A família relacional foi validada com H2 em modo MySQL e com um servidor MariaDB 10.11 (driver MariaDB),
  não com o MySQL oficial + `mysql-connector-j`.
- Bug encontrado e corrigido ao rodar no MariaDB: a conexão ficava em modo transação e gravações feitas
  fora da fachada (quitação de multa, cadastros) não recebiam commit. Corrigido em `UnidadeJdbc`,
  `ServicoDeMulta` e `FachadaDeCirculacao`; teste novo `R08_tudoQueOAppDizTerGravadoEstaCommitado...`.
