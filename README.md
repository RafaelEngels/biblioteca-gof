# Biblioteca GoF — Empréstimo de livros com 10 padrões de projeto

PUCPR · Modelagem de Sistemas Computacionais · Prof. Edson Emílio Scalabrin · Turma A – 2S – 2026

Equipe: Rafael Engel, Gabriel Henrique de Alves, Isabella Schwab e Gabriel Colasso

Implementação executável do sistema de reserva, empréstimo, renovação e devolução de livros
(continuação do Trabalho 02), com os dez padrões GoF pedidos.

## Como rodar

Precisa de **Java 17+** e **Maven 3.8+**.

```bash
mvn test                                   # compila e roda os 40 testes (um comando só)
mvn -q compile exec:java -Dexec.args=demo  # demo: reserva, emprestimo, renovacao, devolucao atrasada, multa, quitacao
mvn -q compile exec:java                   # interface de linha de comando (digite "ajuda")
```

Sem configuração nenhuma, tudo roda no **perfil de teste** (dados em memória, sem banco).

### Perfil de produção (MySQL)

Crie o banco (`CREATE DATABASE bd_biblioteca;`) e defina as variáveis de ambiente.
As tabelas são criadas sozinhas na primeira execução (`src/main/resources/schema.sql`).

```bash
export BIBLIOTECA_PERFIL=PRODUCAO
export BIBLIOTECA_DB_URL=jdbc:mysql://localhost:3306/bd_biblioteca
export BIBLIOTECA_DB_USER=root
export BIBLIOTECA_DB_SENHA=suasenha
export BIBLIOTECA_BANCO=A            # A ou B (banco de cobranca)
export BIBLIOTECA_POLITICA=ORIGINAL  # ORIGINAL, COM_TETO ou COM_TOLERANCIA
mvn -q compile exec:java -Dexec.args=demo
```

Os testes `R08_...` rodam o fluxo completo na família relacional usando H2 em modo MySQL (sem exigir
MySQL instalado) e conferem, por uma segunda conexão, que tudo foi commitado. A demo também foi
executada contra um servidor MariaDB 10.11 (compatível com MySQL).
O Oracle continua sendo réplica e a aplicação não escreve nele.

## Estrutura

| Pacote | Conteúdo |
|---|---|
| `dominio` | Livro, Pessoa, Estudante, Atendente, Reserva, Emprestimo, Devolucao, interfaces `iLivro`, `iPessoa`, `iEstudante`, `iAtendente` |
| `estado` | State: 7 classes de estado (LIVRE … INCINERADO) |
| `multa` | Strategy (políticas), `ServicoDeMulta`, `Multa` |
| `banco` | Adapter: `ContratoCobranca`, `AdaptadorBancoA/B`, APIs simuladas |
| `circulacao` | Facade: `FachadaDeCirculacao`, `Comprovante` |
| `evento` | Observer: publicador e 4 ouvintes |
| `comando` | Command: solicitações e `ExecutorDeSolicitacoes` |
| `cadastro` | Template Method: `CadastroTemplate` + 4 cadastros |
| `persistencia` | Abstract Factory: família em memória e família relacional |
| `consulta` | Decorator: medição e cache |
| `acervo` | Composite: `Grupo` e `ItemLivro` |
| `tratador` | Tratadores chamados pela interface |
| `app` | `Aplicacao` (composição), `Main` (CLI), `Configuracao` |

A interface (`Main`) só chama tratadores. Os tratadores delegam à fachada, ao serviço de multa
ou aos cadastros. Nenhuma regra de domínio fica na interface.

Rastreabilidade completa (padrão → classes → teste): [`RASTREABILIDADE.md`](RASTREABILIDADE.md).
Modelos UML: pasta [`docs/uml`](docs/uml) e o arquivo Astah da equipe.

## Premissas adotadas

- Prazo inicial do empréstimo: 30 dias; cada renovação soma 30 dias; máximo de 2 renovações (`numRenovacoes < 2`).
- Evento de laudo irrecuperável leva REPARANDO → INCINERADO.
- O boleto de multa vence 7 dias após a devolução.
- Multa de valor zero (sem atraso, ou dentro da tolerância) não emite boleto.
- As multas ficam no repositório de Movimentação.
- A inspeção é decidida por `inspecionar(idLivro, bomEstado)`: bom estado → LIVRE; danificado → REPARANDO.
- O relógio é injetável (`RelogioAjustavel`) para simular atrasos na demo e nos testes.

## Uso de LLMs

- Ferramenta e modelo: **Claude Sonnet 5.5** (Anthropic), pelo chat do claude.ai.
- O Claude gerou o código-fonte Java dos dez padrões, os testes JUnit, o `schema.sql` e os diagramas de referência em PlantUML. Os nomes de classes e operações foram extraídos do arquivo Astah da equipe (`PROJETO_2.asta`).
- A equipe revisou o código gerado, executou os testes (`mvn test`, 40 testes aprovados) e a demonstração.
- Resumo dos pedidos e das decisões da equipe: [`REGISTRO_LLM.md`](REGISTRO_LLM.md).
