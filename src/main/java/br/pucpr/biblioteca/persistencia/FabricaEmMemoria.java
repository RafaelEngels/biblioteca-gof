package br.pucpr.biblioteca.persistencia;

public class FabricaEmMemoria implements FabricaDeAcesso {
    private final UnidadeEmMemoria unidade = new UnidadeEmMemoria();
    private final LivrosEmMemoria livros = new LivrosEmMemoria(unidade);
    private final MovimentacaoEmMemoria movimentacao = new MovimentacaoEmMemoria(unidade);
    private final RelacionamentoEmMemoria relacionamento = new RelacionamentoEmMemoria(unidade);

    public UnidadeDeTrabalho unidade() { return unidade; }
    public RepositorioLivro livros() { return livros; }
    public RepositorioMovimentacao movimentacao() { return movimentacao; }
    public RepositorioRelacionamento relacionamento() { return relacionamento; }
}
