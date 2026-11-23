# Guia de arguição (leitura rápida, uma coisa por vez)

Todo integrante pode ser escolhido para qualquer pergunta. Nota individual = nota da equipe × fator (1,0 / 0,7 / 0,4 / 0).
Para cada requisito: **o que fazer → abrir o arquivo → responder em voz alta**.

## R1 State — `estado/*`, `dominio/Livro`
- *Onde está a guarda da renovação?* `EstadoEmprestado.renovar` e `EstadoRenovado.renovar`: `numRenovacoes >= 2` retorna `false` **antes** de `emprestimo.renovar()`, por isso o vencimento não muda.
- *Evento em estado que não aceita?* `EstadoBase` devolve `false`; o `Livro` não muda; a fachada transforma em `RegraDeNegocioException`.
- *Como grava/reconstitui?* Grava `getNomeEstado()` (texto); lê com `Livro.reconstituir` → `EstadoLivro.de(nome)`.

## R2 Strategy — `multa/*`
- *Nova política sem mexer no serviço?* Nova classe que implementa `PoliticaDeMulta` + um `case` em `Aplicacao.escolherPolitica`. `ServicoDeMulta` não muda.
- *Por que trocar não recalcula?* `Multa` grava valor e `politicaAplicada` na emissão e é salva; nunca é recalculada.
- *Dias de atraso/valores?* `ChronoUnit.DAYS.between(vencimento, devolução)`, mínimo 0; `BigDecimal` com 2 casas.

## R3 Adapter — `banco/*`
- *Onde ficam as conversões?* Só em `AdaptadorBancoA/B` (centavos, CPF só dígitos, data ISO no A; reais, CPF formatado, dd/MM/yyyy no B; PAID/LIQUIDADO).
- *Como escolhe o banco?* `Aplicacao.escolherBanco` a partir de `BIBLIOTECA_BANCO`.
- *Quitação repetida?* `ServicoDeMulta.receberNotificacao` é `synchronized` e ignora se `estaQuitada()`.

## R4 Facade — `circulacao/FachadaDeCirculacao`
- *O que coordena e em que ordem (empréstimo)?* Relacionamento (valida pessoas) → Movimentacao (valida e remove reserva, cria empréstimo) → Livro (`emprestar`) → Acervo (salva).
- *O que é desfeito se falhar?* `transacao(...)` chama `unidade.desfazer()`: cada passo registra um `aoDesfazer`; no JDBC também há `rollback`.
- *Por que a interface não acessa Movimentacao?* Para não expor coleções nem permitir pular regras; só recebe `Comprovante`.

## R5 Observer — `evento/*`
- *Novo ouvinte sem mexer no tratador?* Implementa `OuvinteDeDevolucao` e é registrado em `Aplicacao`.
- *E-mail falha?* `PublicadorDeDevolucao.publicar` captura a exceção de cada ouvinte e guarda em `falhas()`; a devolução já foi confirmada.
- *Por que o evento leva os dados?* Para o ouvinte não depender do estado do banco depois (o empréstimo já foi removido).

## R6 Command — `comando/*`
- *Quem cria/invoca/executa?* `TratadorDeEmprestimo` cria; `ExecutorDeSolicitacoes` invoca; `FachadaDeCirculacao` executa.
- *Duas submissões simultâneas?* `ConcurrentHashMap.computeIfAbsent(id, ...)` é atômico por chave.
- *Por que sem desfazer?* Renovar/reservar têm regra de negócio no domínio; desfazer não foi pedido e criaria estados inconsistentes.

## R7 Template Method — `cadastro/CadastroTemplate`
- *Fixos x variáveis?* Ordem fixa em `incluir` (final); passos variam nas subclasses.
- *Gancho?* `depoisDeArmazenar` (vazio por padrão).
- *Sem gravação parcial?* `verificarPreRequisitos` roda **antes** de `criar` e `armazenar`.

## R8 Abstract Factory — `persistencia/Fabrica*`
- *Onde escolhe a família?* `Aplicacao.escolherFabrica`, uma única vez, pelo `Perfil`.
- *Mesma transação?* Os três repositórios recebem a mesma `UnidadeJdbc` (uma `Connection`).
- *O que impede misturar?* Os construtores dos repositórios exigem `UnidadeEmMemoria` ou `UnidadeJdbc` (tipos diferentes) e são package-private.

## R9 Decorator — `consulta/*`
- *Cadeia e ordem?* `Medicao(Cache(Original))`. Medição por fora conta também o que o cache atendeu.
- *Cache falha?* `ConsultaComCache` captura a exceção e consulta o próximo.
- *Por que o cache não decide reserva?* `reservar` usa `Livro.reservar()` direto no `RepositorioLivro`; o cache só guarda título/id.

## R10 Composite — `acervo/*`
- *Contagem?* `Grupo.contarLivros` soma os filhos recursivamente; `ItemLivro` vale 1.
- *Ciclos / livro em dois grupos?* `Grupo.adicionar` testa `contem` (ciclo) e compara ids com `raiz().listarLivros()`.
- *Por que mover não toca Livro/Movimentacao?* A árvore só guarda ids; mover troca de pai o `ItemLivro`.

## Alteração ao vivo (treinem!)
1. **Nova política de multa** (ex.: R$ 2,00 por dia): classe nova em `multa/` + `case` em `Aplicacao.escolherPolitica`.
2. **Novo ouvinte** (ex.: log): classe `OuvinteX implements OuvinteDeDevolucao` + `publicador.registrar(...)` em `Aplicacao`.
