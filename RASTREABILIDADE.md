# Tabela de rastreabilidade

Pacote base: `br.pucpr.biblioteca`. Testes em `src/test/java/br/pucpr/biblioteca`.

| Req. | Padrão | Papéis → classes | Teste que comprova |
|---|---|---|---|
| 1 | **State** | Context: `dominio.Livro` · State: `estado.EstadoLivro` · Base: `EstadoBase` · ConcreteState: `EstadoLivre`, `EstadoReservado`, `EstadoEmprestado`, `EstadoRenovado`, `EstadoDevolvido`, `EstadoReparando`, `EstadoIncinerado` | `R01_StateTest` → `R01_terceiraRenovacaoRecusada`, `R01_livroDevolvidoNaoInspecionadoNaoPodeSerReservado`, `R01_laudoIrrecuperavel...` |
| 2 | **Strategy** | Strategy: `multa.PoliticaDeMulta` · ConcreteStrategy: `PoliticaOriginal`, `PoliticaComTeto`, `PoliticaComTolerancia` · Context: `multa.ServicoDeMulta` · Seleção: `multa.ConfiguracaoAdministrativa` | `R02_StrategyTest` → `R02_cincoDiasDeAtrasoOriginalETolerancia` |
| 3 | **Adapter** | Target: `banco.ContratoCobranca` · Adapter: `AdaptadorBancoA`, `AdaptadorBancoB` · Adaptee: `BancoAApi`, `BancoBApi` · Client: `ServicoDeMulta` | `R03_AdapterTest` → `R03_mesmaSolicitacaoDeSeteReais...`, `R03_notificacaoRepetida...` |
| 4 | **Facade** | Facade: `circulacao.FachadaDeCirculacao` · Subsistema: `RepositorioRelacionamento`, `RepositorioMovimentacao`, `RepositorioLivro` (Acervo), `Livro` · Retorno: `Comprovante` · Clientes: tratadores | `R04_FacadeTest` → `R04_efetivacaoValidaReserva...`, `R04_falhaNoUltimoPassoDesfaz...` |
| 5 | **Observer** | Subject: `evento.PublicadorDeDevolucao` · Observer: `OuvinteDeDevolucao` · ConcreteObserver: `OuvinteInspecao`, `OuvinteMulta`, `OuvinteIndicadores`, `OuvinteEmail` · Evento: `EventoDevolucao` | `R05_ObserverTest` → `R05_devolucaoAtrasada...`, `R05_devolucaoNoPrazoNaoGeraMulta`, `R05_falhaDoEmail...` |
| 6 | **Command** | Command: `comando.SolicitacaoDeCirculacao` · ConcreteCommand: `SolicitacaoDeReserva`, `SolicitacaoDeRenovacao` · Invoker: `ExecutorDeSolicitacoes` · Receiver: `FachadaDeCirculacao` · Client: `tratador.TratadorDeEmprestimo` | `R06_CommandTest` → `R06_renovacaoComMesmoIdentificador...`, `R06_duasSubmissoesSimultaneas...` |
| 7 | **Template Method** | AbstractClass: `cadastro.CadastroTemplate` (método `incluir` final, gancho `depoisDeArmazenar`) · ConcreteClass: `CadastroLivro`, `CadastroPessoa`, `CadastroEstudante`, `CadastroAtendente` | `R07_TemplateMethodTest` → `R07_incluirEstudanteDuplicado...`, `R07_estudanteComCpfNaoCadastrado...` |
| 8 | **Abstract Factory** | AbstractFactory: `persistencia.FabricaDeAcesso` · ConcreteFactory: `FabricaEmMemoria`, `FabricaRelacional` · Produtos: `UnidadeDeTrabalho` (`UnidadeEmMemoria`/`UnidadeJdbc`), `RepositorioLivro`, `RepositorioMovimentacao`, `RepositorioRelacionamento` e implementações · Escolha única: `app.Aplicacao.escolherFabrica` | `R08_AbstractFactoryTest` → `R08_configuracaoDeTeste...`, `R08_familiaRelacional...`, `R08_perfilDeProducao...` |
| 9 | **Decorator** | Component: `consulta.ConsultaAcervo` · ConcreteComponent: `ConsultaAcervoOriginal` · Decorator: `ConsultaComMedicao`, `ConsultaComCache` · Montagem: `app.Aplicacao` | `R09_DecoratorTest` → `R09_segundaConsulta...`, `R09_cacheAntigoNuncaAutorizaReserva...` |
| 10 | **Composite** | Component: `acervo.ComponenteDoAcervo` · Composite: `Grupo` · Leaf: `ItemLivro` | `R10_CompositeTest` → `R10_unidadeComDuasSecoes...`, `R10_moverLivro...`, `R10_grupoRecusaCiclos`, `R10_grupoRecusaLivro...` |

## Correspondência com o modelo (Astah)

| Elemento do modelo | Onde está no código |
|---|---|
| Máquina de estados de Livro (`Maquina de Estados - Livro`) | classes `estado.*`; cada transição = um método do estado de origem |
| `TratadorDeEmprestimo`, `TratadorDeDevolucao`, `TratadorDeMulta`, `TratadorDeAcervo`, `TratadorDeLivro/Pessoa/Estudante/Atendente` | pacote `tratador` |
| `Acervo` (AcervoDeLivros), `Relacionamento` (GrupoDePessoas/Academicos/Colaboradores), `Movimentacao` | `RepositorioLivro`, `RepositorioRelacionamento`, `RepositorioMovimentacao` |
| `BoletoDeMulta` (valorDiario 1,00, quitar) | `multa.Multa` + `PoliticaOriginal` |
| `iCobranca`, `iQuitacao` | `banco.ContratoCobranca` |
| `iLivro`, `iPessoa`, `iEstudante`, `iAtendente` | `dominio.i*` |
